package edtech.afrilingo.profile;

import edtech.afrilingo.course.Course;
import edtech.afrilingo.course.CourseService;
import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.exception.ResourceNotFoundException;
import edtech.afrilingo.language.Language;
import edtech.afrilingo.language.LanguageService;
import edtech.afrilingo.userProgress.UserProgress;
import edtech.afrilingo.userProgress.UserProgressRepository;
import edtech.afrilingo.userProgress.UserQuizAttempt;
import edtech.afrilingo.userProgress.UserQuizAttemptRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/profile-setup")
@RequiredArgsConstructor
@Tag(name = "Profile Setup", description = "Endpoints for user profile setup options")
public class ProfileSetupController {

    private final LanguageService languageService;
    @Operation(summary = "Get profile setup options", description = "Returns options for profile setup including countries, languages, and learning reasons")
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfileSetupOptions() {
        Map<String, Object> options = new HashMap<>();
        
        // List of countries
        List<String> countries = Arrays.asList(
                "Algeria", "Angola", "Benin", "Botswana", "Burkina Faso", 
                "Burundi", "Cabo Verde", "Cameroon", "Central African Republic", 
                "Chad", "Comoros", "Congo", "Côte d'Ivoire", "Djibouti", 
                "Egypt", "Equatorial Guinea", "Eritrea", "Eswatini", "Ethiopia",
                "Gabon", "Gambia", "Ghana", "Guinea", "Guinea-Bissau", 
                "Kenya", "Lesotho", "Liberia", "Libya", "Madagascar", 
                "Malawi", "Mali", "Mauritania", "Mauritius", "Morocco", 
                "Mozambique", "Namibia", "Niger", "Nigeria", "Rwanda", 
                "Sao Tome and Principe", "Senegal", "Seychelles", "Sierra Leone", 
                "Somalia", "South Africa", "South Sudan", "Sudan", "Tanzania", 
                "Togo", "Tunisia", "Uganda", "Zambia", "Zimbabwe",
                "United States", "United Kingdom", "Canada", "Australia", 
                "Germany", "France", "Brazil", "India", "China", "Japan",
                "Other"
        );
        
        // List of common languages
        List<String> commonLanguages = Arrays.asList(
                "English", "French", "Spanish", "Portuguese", "German", 
                "Italian", "Russian", "Arabic", "Chinese", "Japanese", 
                "Korean", "Hindi", "Urdu", "Bengali", "Turkish",
                "Swahili", "Yoruba", "Igbo", "Hausa", "Amharic",
                "Zulu", "Xhosa", "Shona", "Berber", "Oromo",
                "Other"
        );
        
        // Learning reasons
        List<String> learningReasons = Arrays.asList(
                "Travel to African countries",
                "Connect with heritage/ancestry",
                "Business/work opportunities",
                "Academic research",
                "Cultural appreciation",
                "Personal interest",
                "Communicate with friends/family",
                "Relocating to an African country",
                "Religious reasons",
                "Other"
        );
        
        // List of available learning languages
        List<Language> availableLanguages = languageService.getAllLanguages();
        
        // List of preferred learning times
        List<String> preferredLearningTimes = Arrays.asList(
                "Early morning (5AM-8AM)",
                "Morning (8AM-12PM)",
                "Afternoon (12PM-5PM)",
                "Evening (5PM-8PM)",
                "Night (8PM-12AM)",
                "Late night (12AM-5AM)",
                "No preference"
        );
        
        // Daily goal minutes options
        List<Integer> dailyGoalOptions = Arrays.asList(5, 10, 15, 20, 30, 45, 60);
        
        options.put("countries", countries);
        options.put("commonLanguages", commonLanguages);
        options.put("learningReasons", learningReasons);
        options.put("availableLanguages", availableLanguages);
        options.put("preferredLearningTimes", preferredLearningTimes);
        options.put("dailyGoalOptions", dailyGoalOptions);
        
        return ResponseEntity.ok(ApiResponse.success(options));
    }

    @Service
    @RequiredArgsConstructor
    public static class UserDashboardService {

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

            dashboardData.put("userProfile", userProfile);
            dashboardData.put("languagesToLearn", languagesToLearn);
            dashboardData.put("coursesByLanguage", coursesByLanguage);
            dashboardData.put("learningStats", learningStats);
            dashboardData.put("recommendedCourses", recommendedCourses);
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
}