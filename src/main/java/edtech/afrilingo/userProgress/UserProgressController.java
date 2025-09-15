package edtech.afrilingo.userProgress;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.lesson.Lesson;
import edtech.afrilingo.lesson.LessonService;
import edtech.afrilingo.quiz.Quiz;
import edtech.afrilingo.quiz.QuizService;
import edtech.afrilingo.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Tag(name = "User Progress", description = "Endpoints for tracking user progress")
public class UserProgressController {

    private final UserProgressRepository userProgressRepository;
    private final LessonService lessonService;
    private final QuizService quizService;
    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final edtech.afrilingo.notification.WebSocketNotificationController wsNotifier;

    @Operation(summary = "Get user streak", description = "Returns the current streak for the authenticated user")
    @GetMapping("/streak")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStreak() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Get all user progress records
        List<UserProgress> allUserProgress = userProgressRepository.findByUserId(currentUser.getId());
        
        // Get all quiz attempts
        List<UserQuizAttempt> allQuizAttempts = userQuizAttemptRepository.findByUserId(currentUser.getId());
        
        // Calculate streak
        int streak = calculateStreak(allUserProgress, allQuizAttempts);
        
        Map<String, Object> response = new HashMap<>();
        response.put("streak", streak);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "Mark lesson as accessed", description = "Records that a user has accessed a lesson")
    @PostMapping("/lesson/access")
    public ResponseEntity<ApiResponse<UserProgress>> accessLesson(@RequestParam Long lessonId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Get the lesson
        Optional<Lesson> lessonOpt = lessonService.getLessonById(lessonId);
        if (lessonOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Lesson not found"));
        }
        
        Lesson lesson = lessonOpt.get();
        
        // Check if progress record already exists
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserIdAndLessonId(
                currentUser.getId(), lessonId);
        
        UserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            // Create new progress record
            progress = UserProgress.builder()
                    .user(currentUser)
                    .lesson(lesson)
                    .completed(false)
                    .score(0)
                    .build();
        }
        
        userProgressRepository.save(progress);
        
        try {
            // Send only via authenticated user destination to avoid duplicates
            wsNotifier.sendNotificationToUser(currentUser.getUsername(), "lesson_access:" + lessonId);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(ApiResponse.success(progress));
    }
    
    @Operation(summary = "Mark lesson as completed", description = "Records that a user has completed a lesson")
    @PostMapping("/lesson/complete")
    public ResponseEntity<ApiResponse<UserProgress>> completeLesson(@RequestParam Long lessonId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Get the lesson
        Optional<Lesson> lessonOpt = lessonService.getLessonById(lessonId);
        if (lessonOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Lesson not found"));
        }
        
        Lesson lesson = lessonOpt.get();
        
        // Check if progress record already exists
        Optional<UserProgress> existingProgress = userProgressRepository.findByUserIdAndLessonId(
                currentUser.getId(), lessonId);
        
        UserProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            // Create new progress record
            progress = UserProgress.builder()
                    .user(currentUser)
                    .lesson(lesson)
                    .build();
        }
        
        // Update progress
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        
        userProgressRepository.save(progress);
        
        try {
            // Send only via authenticated user destination to avoid duplicates
            wsNotifier.sendNotificationToUser(currentUser.getUsername(), "lesson_completed:" + lessonId);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(ApiResponse.success(progress));
    }
    
    @Operation(summary = "Record quiz progress", description = "Records that a user has attempted a quiz")
    @PostMapping("/quiz")
    public ResponseEntity<ApiResponse<UserQuizAttempt>> recordQuizProgress(
            @RequestParam Long quizId,
            @RequestParam int score,
            @RequestParam boolean passed) {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Get the quiz
        Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Quiz not found"));
        }
        
        Quiz quiz = quizOpt.get();
        
        // Create quiz attempt record
        UserQuizAttempt attempt = UserQuizAttempt.builder()
                .user(currentUser)
                .quiz(quiz)
                .score(score)
                .passed(passed)
                .attemptedAt(LocalDateTime.now())
                .build();
        
        userQuizAttemptRepository.save(attempt);
        
        try {
            // Send only via authenticated user destination to avoid duplicates
            wsNotifier.sendNotificationToUser(currentUser.getUsername(), "quiz_attempt:" + quizId);
            if (passed) {
                wsNotifier.sendNotificationToUser(currentUser.getUsername(), "challenge_passed:" + quizId);
            }
        } catch (Exception ignored) {}

        return ResponseEntity.ok(ApiResponse.success(attempt));
    }

    @Operation(summary = "Update learning time", description = "Accepts a batch of minutes learned to aggregate server-side")
    @PostMapping("/learning-time")
    public ResponseEntity<ApiResponse<String>> postLearningTime(@RequestBody Map<String, Object> body) {
        // Optional aggregation point; accept and emit a best-effort daily goal notification if possible
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Object minutesObj = body != null ? body.get("minutes") : null;
            int minutes = 0;
            if (minutesObj instanceof Number) {
                minutes = ((Number) minutesObj).intValue();
            }
            if (minutes >= 15) { // heuristic threshold when we lack aggregation
                wsNotifier.sendNotificationToUser(currentUser.getUsername(), "daily_goal_achieved:" + minutes);
            }
        } catch (Exception ignored) {}
        return ResponseEntity.ok(ApiResponse.success("ok"));
    }
    
    /**
     * Calculate the user's learning streak (consecutive days with activity)
     * @param allUserProgress All user progress records
     * @param allQuizAttempts All user quiz attempts
     * @return Streak count
     */
    private int calculateStreak(List<UserProgress> allUserProgress, List<UserQuizAttempt> allQuizAttempts) {
        // Combine all completion dates and attempt dates
        List<LocalDateTime> activityDates = new ArrayList<>();
        
        allUserProgress.stream()
                .filter(p -> p.getCompletedAt() != null)
                .map(UserProgress::getCompletedAt)
                .forEach(activityDates::add);
        
        allQuizAttempts.stream()
                .map(UserQuizAttempt::getAttemptedAt)
                .forEach(activityDates::add);
        
        if (activityDates.isEmpty()) {
            return 0;
        }
        
        // Sort dates in descending order (newest first)
        activityDates.sort((d1, d2) -> d2.compareTo(d1));
        
        // Group by date (ignoring time)
        Map<LocalDateTime, List<LocalDateTime>> dateGroups = activityDates.stream()
                .collect(Collectors.groupingBy(date -> 
                    date.truncatedTo(ChronoUnit.DAYS)));
        
        // Convert to sorted list of distinct days
        List<LocalDateTime> distinctDays = new ArrayList<>(dateGroups.keySet());
        distinctDays.sort((d1, d2) -> d2.compareTo(d1));
        
        // Calculate streak
        int streak = 1;
        LocalDateTime currentDay = distinctDays.get(0);
        
        for (int i = 1; i < distinctDays.size(); i++) {
            LocalDateTime nextDay = distinctDays.get(i);
            
            // Check if dates are consecutive
            if (ChronoUnit.DAYS.between(nextDay, currentDay) == 1) {
                streak++;
                currentDay = nextDay;
            } else {
                break;
            }
        }
        
        return streak;
    }

    @Operation(summary = "Get learning time snapshot", description = "Returns summarized learning time data for the authenticated user")
    @GetMapping("/learning-time")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getLearningTime() {
        // For now, provide a minimal stub that returns zeros; client primarily uses local cache
        Map<String, Integer> data = new HashMap<>();
        data.put("today", 0);
        data.put("week", 0);
        data.put("month", 0);
        data.put("total", 0);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Check lesson completion", description = "Returns completion status for a lesson")
    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> isLessonCompleted(@PathVariable Long lessonId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<UserProgress> progress = userProgressRepository.findByUserIdAndLessonId(currentUser.getId(), lessonId);
        Map<String, Object> data = new HashMap<>();
        data.put("completed", progress.isPresent() && Boolean.TRUE.equals(progress.get().isCompleted()));
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @Operation(summary = "Check quiz completion for lesson", description = "Returns pass/complete status for the lesson's quiz")
    @GetMapping("/quizzes/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> isQuizCompletedByLesson(@PathVariable Long lessonId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Quiz> quizzes = quizService.getQuizzesByLessonId(lessonId);
        boolean passed = false;
        if (!quizzes.isEmpty()) {
            Long quizId = quizzes.get(0).getId();
            List<UserQuizAttempt> attempts = userQuizAttemptRepository.findByUserIdAndQuizId(currentUser.getId(), quizId);
            passed = attempts.stream().anyMatch(UserQuizAttempt::isPassed);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("completed", passed);
        data.put("passed", passed);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
