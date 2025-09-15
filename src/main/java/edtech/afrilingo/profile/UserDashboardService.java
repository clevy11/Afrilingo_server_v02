package edtech.afrilingo.profile;

import edtech.afrilingo.course.Course;
import edtech.afrilingo.course.CourseService;
import edtech.afrilingo.exception.ResourceNotFoundException;
import edtech.afrilingo.language.Language;
import edtech.afrilingo.language.LanguageService;
import edtech.afrilingo.lesson.Lesson;
import edtech.afrilingo.user.User;
import edtech.afrilingo.userProgress.UserProgress;
import edtech.afrilingo.userProgress.UserProgressRepository;
import edtech.afrilingo.userProgress.UserQuizAttempt;
import edtech.afrilingo.userProgress.UserQuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDashboardService {

    private final UserProfileService userProfileService;
    private final CourseService courseService;
    private final LanguageService languageService;
    private final UserProgressRepository userProgressRepository;
    private final UserQuizAttemptRepository userQuizAttemptRepository;

    /**
     * Get user dashboard data
     * @param userId User ID
     * @return Map containing dashboard data
     */
    public Map<String, Object> getUserDashboardData(Long userId) {
        Map<String, Object> dashboardData = new HashMap<>();
        
        // Get user profile
        UserProfile userProfile = userProfileService.getUserProfileByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", userId));
        
        // Get user's languages to learn
        List<Language> languagesToLearn = userProfile.getLanguagesToLearn();
        
        // For each language, get active courses
        Map<Language, List<Course>> coursesByLanguage = new HashMap<>();
        for (Language language : languagesToLearn) {
            List<Course> activeCourses = courseService.getActiveCoursesByLanguageId(language.getId());
            coursesByLanguage.put(language, activeCourses);
        }
        
        // Get user progress for all lessons
        List<UserProgress> allUserProgress = userProgressRepository.findByUserId(userId);
        
        // Get user quiz attempts
        List<UserQuizAttempt> allQuizAttempts = userQuizAttemptRepository.findByUserId(userId);
        
        // Calculate learning stats
        Map<String, Object> learningStats = calculateLearningStats(userId, allUserProgress, allQuizAttempts);
        
        // Get recommended courses based on user's profile and progress
        List<Course> recommendedCourses = getRecommendedCourses(userId, userProfile, allUserProgress);
        
        // Get course progress for each course the user has engaged with
        Map<Long, Double> courseProgress = calculateCourseProgress(userId, allUserProgress);
        
        // Create a UserProfileDTO to avoid circular references
        Map<String, Object> profileDTO = new HashMap<>();
        profileDTO.put("id", userProfile.getId());
        profileDTO.put("country", userProfile.getCountry());
        profileDTO.put("firstLanguage", userProfile.getFirstLanguage());
        profileDTO.put("profilePicture", userProfile.getProfilePicture());
        profileDTO.put("reasonToLearn", userProfile.getReasonToLearn());
        profileDTO.put("dailyReminders", userProfile.isDailyReminders());
        profileDTO.put("dailyGoalMinutes", userProfile.getDailyGoalMinutes());
        profileDTO.put("preferredLearningTime", userProfile.getPreferredLearningTime());
        
        // Add user data
        Map<String, Object> userData = new HashMap<>();
        if (userProfile.getUser() != null) {
            userData.put("firstName", userProfile.getUser().getFirstName());
            userData.put("lastName", userProfile.getUser().getLastName());
            userData.put("email", userProfile.getUser().getEmail());
            userData.put("id", userProfile.getUser().getId());
        }
        profileDTO.put("user", userData);
        
        dashboardData.put("userProfile", profileDTO);
        dashboardData.put("learningStats", learningStats);
        
        // Convert recommended courses to DTOs to avoid circular references
        List<Map<String, Object>> recommendedCourseDTOs = new ArrayList<>();
        for (Course course : recommendedCourses) {
            Map<String, Object> courseDTO = new HashMap<>();
            courseDTO.put("id", course.getId());
            courseDTO.put("title", course.getTitle());
            courseDTO.put("description", course.getDescription());
            courseDTO.put("level", course.getLevel());
            courseDTO.put("image", course.getImage());
            
            // Add language info without circular reference
            if (course.getLanguage() != null) {
                Map<String, Object> languageDTO = new HashMap<>();
                languageDTO.put("id", course.getLanguage().getId());
                languageDTO.put("name", course.getLanguage().getName());
                languageDTO.put("code", course.getLanguage().getCode());
                courseDTO.put("language", languageDTO);
            }
            
            recommendedCourseDTOs.add(courseDTO);
        }
        dashboardData.put("recommendedCourses", recommendedCourseDTOs);
        dashboardData.put("courseProgress", courseProgress);
        
        return dashboardData;
    }
    
    /**
     * Calculate learning statistics for the user
     * @param userId User ID
     * @param allUserProgress All user progress records
     * @param allQuizAttempts All user quiz attempts
     * @return Map containing learning statistics
     */
    private Map<String, Object> calculateLearningStats(Long userId, List<UserProgress> allUserProgress, 
                                                     List<UserQuizAttempt> allQuizAttempts) {
        Map<String, Object> stats = new HashMap<>();
        
        // Count completed lessons
        int completedLessons = (int) allUserProgress.stream()
                .filter(UserProgress::isCompleted)
                .count();
        
        // Calculate average quiz score
        double averageQuizScore = allQuizAttempts.stream()
                .mapToInt(UserQuizAttempt::getScore)
                .average()
                .orElse(0.0);
        
        // Calculate streak (consecutive days with activity)
        int streak = calculateStreak(allUserProgress, allQuizAttempts);
        
        // Calculate total learning time (assuming 10 minutes per completed lesson + quiz attempt time)
        int totalLearningMinutes = completedLessons * 10 + allQuizAttempts.size() * 5;
        
        // Calculate pass rate for quizzes
        long passedQuizzes = allQuizAttempts.stream().filter(UserQuizAttempt::isPassed).count();
        double passRate = allQuizAttempts.isEmpty() ? 0 : (double) passedQuizzes / allQuizAttempts.size() * 100;
        
        stats.put("completedLessons", completedLessons);
        stats.put("averageQuizScore", averageQuizScore);
        stats.put("streak", streak);
        stats.put("totalLearningMinutes", totalLearningMinutes);
        stats.put("passRate", passRate);
        
        return stats;
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
    
    /**
     * Get recommended courses based on user's profile and progress
     * @param userId User ID
     * @param userProfile User profile
     * @param allUserProgress All user progress records
     * @return List of recommended courses
     */
    private List<Course> getRecommendedCourses(Long userId, UserProfile userProfile, List<UserProgress> allUserProgress) {
        List<Course> recommendations = new ArrayList<>();
        
        // Get user's languages to learn
        List<Language> languagesToLearn = userProfile.getLanguagesToLearn();
        
        // Get courses the user has already started (has progress records)
        Set<Long> startedCourseIds = allUserProgress.stream()
                .map(progress -> progress.getLesson().getCourse().getId())
                .collect(Collectors.toSet());
        
        // For each language, get active courses that the user hasn't started yet
        for (Language language : languagesToLearn) {
            List<Course> activeCourses = courseService.getActiveCoursesByLanguageId(language.getId());
            
            List<Course> notStartedCourses = activeCourses.stream()
                    .filter(course -> !startedCourseIds.contains(course.getId()))
                    .collect(Collectors.toList());
            
            recommendations.addAll(notStartedCourses);
        }
        
        // Limit to 5 recommendations
        if (recommendations.size() > 5) {
            recommendations = recommendations.subList(0, 5);
        }
        
        return recommendations;
    }
    
    /**
     * Calculate progress percentage for each course
     * @param userId User ID
     * @param allUserProgress All user progress records
     * @return Map of course ID to progress percentage
     */
    private Map<Long, Double> calculateCourseProgress(Long userId, List<UserProgress> allUserProgress) {
        Map<Long, Double> progressByCoursee = new HashMap<>();
        
        // Group progress entries by course ID
        Map<Long, List<UserProgress>> progressByCourse = allUserProgress.stream()
                .collect(Collectors.groupingBy(p -> p.getLesson().getCourse().getId()));
        
        // For each course, calculate progress percentage
        for (Map.Entry<Long, List<UserProgress>> entry : progressByCourse.entrySet()) {
            Long courseId = entry.getKey();
            List<UserProgress> courseProgress = entry.getValue();
            
            // Get course details to find total number of lessons
            Optional<Course> courseOpt = courseService.getCourseById(courseId);
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                int totalLessons = course.getLessons().size();
                
                if (totalLessons > 0) {
                    // Count completed lessons for this course
                    long completedLessons = courseProgress.stream()
                            .filter(UserProgress::isCompleted)
                            .count();
                    
                    // Calculate progress percentage
                    double progressPercentage = (double) completedLessons / totalLessons * 100;
                    progressByCoursee.put(courseId, progressPercentage);
                }
            }
        }
        
        return progressByCoursee;
    }
}