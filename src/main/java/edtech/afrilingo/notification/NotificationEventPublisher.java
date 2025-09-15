package edtech.afrilingo.notification;

import edtech.afrilingo.notification.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for publishing notification events.
 * Other services can use this to create notifications for users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventPublisher {

    private final NotificationService notificationService;
    private final FCMNotificationService fcmNotificationService;

    // ============ CORE NOTIFICATION METHODS ============

    /**
     * Create a notification with basic parameters
     */
    public NotificationDTO notifyUser(Long userId, String message, NotificationType type, Long relatedEntityId) {
        return notifyUser(userId, message, type, relatedEntityId, null, null, 2);
    }

    /**
     * Create a notification with all parameters
     */
    public NotificationDTO notifyUser(Long userId, String message, NotificationType type,
                                      Long relatedEntityId, String actionUrl, String imageUrl, int priority) {
        try {
            // Create notification in database
            NotificationDTO notification = notificationService.createNotification(
                    userId, message, type, relatedEntityId, actionUrl, imageUrl, priority);

            // Send push notification
            fcmNotificationService.sendPushNotification(userId, notification);

            log.debug("Notification created and sent for user {}: {}", userId, message);
            return notification;
        } catch (Exception e) {
            log.error("Error creating notification for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    // ============ COURSE-RELATED NOTIFICATIONS ============

    /**
     * Create a course completion notification
     */
    public NotificationDTO notifyCourseCompleted(Long userId, Long courseId, String courseName) {
        String message = "🎉 Congratulations! You have completed the course: " + courseName;
        String actionUrl = "afrilingo://course/" + courseId + "/certificate";
        return notifyUser(userId, message, NotificationType.COURSE_COMPLETED, courseId, actionUrl, null, 3);
    }

    /**
     * Create a new course availability notification
     */
    public NotificationDTO notifyNewCourseAvailable(Long userId, Long courseId, String courseName) {
        String message = "📚 A new course is now available: " + courseName;
        String actionUrl = "afrilingo://course/" + courseId;
        return notifyUser(userId, message, NotificationType.NEW_COURSE_AVAILABLE, courseId, actionUrl, null, 2);
    }

    /**
     * Notify course enrollment
     */
    public NotificationDTO notifyCourseEnrollment(Long userId, Long courseId, String courseName) {
        String message = "✅ You have successfully enrolled in: " + courseName + ". Start learning now!";
        String actionUrl = "afrilingo://course/" + courseId + "/start";
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, courseId, actionUrl, null, 2);
    }

    // ============ LESSON-RELATED NOTIFICATIONS ============

    /**
     * Create a lesson completion notification
     */
    public NotificationDTO notifyLessonCompleted(Long userId, Long lessonId, String lessonTitle) {
        String message = "✅ You have completed the lesson: " + lessonTitle;
        String actionUrl = "afrilingo://lesson/" + lessonId + "/next";
        return notifyUser(userId, message, NotificationType.LESSON_COMPLETED, lessonId, actionUrl, null, 2);
    }

    /**
     * Notify new lesson unlocked
     */
    public NotificationDTO notifyLessonUnlocked(Long userId, Long lessonId, String lessonTitle, String courseName) {
        String message = "🔓 New lesson unlocked in " + courseName + ": " + lessonTitle;
        String actionUrl = "afrilingo://lesson/" + lessonId;
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, lessonId, actionUrl, null, 2);
    }

    // ============ QUIZ-RELATED NOTIFICATIONS ============

    /**
     * Create a quiz completion notification
     */
    public NotificationDTO notifyQuizCompleted(Long userId, Long quizId, String quizTitle, double score) {
        String emoji = score >= 90 ? "🏆" : score >= 70 ? "👏" : "💪";
        String message = String.format("%s You scored %.0f%% on the quiz: %s", emoji, score, quizTitle);
        String actionUrl = "afrilingo://quiz/" + quizId + "/results";
        return notifyUser(userId, message, NotificationType.QUIZ_COMPLETED, quizId, actionUrl, null, 2);
    }

    /**
     * Notify quiz perfect score
     */
    public NotificationDTO notifyQuizPerfectScore(Long userId, Long quizId, String quizTitle) {
        String message = "💯 Perfect Score! You aced the quiz: " + quizTitle + ". Outstanding work!";
        String actionUrl = "afrilingo://quiz/" + quizId + "/celebration";
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, quizId, actionUrl, null, 3);
    }

    /**
     * Notify quiz retry suggestion
     */
    public NotificationDTO notifyQuizRetry(Long userId, Long quizId, String quizTitle, double score) {
        String message = String.format("📝 You scored %.0f%% on %s. Ready to try again and improve your score?", score, quizTitle);
        String actionUrl = "afrilingo://quiz/" + quizId + "/retry";
        return notifyUser(userId, message, NotificationType.REMINDER, quizId, actionUrl, null, 1);
    }

    // ============ ACHIEVEMENT NOTIFICATIONS ============

    /**
     * Create an achievement notification
     */
    public NotificationDTO notifyAchievementUnlocked(Long userId, String achievementName) {
        String message = "🏆 Achievement unlocked: " + achievementName;
        String actionUrl = "afrilingo://achievements";
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null, actionUrl, null, 3);
    }

    /**
     * Notify streak achievement
     */
    public NotificationDTO notifyStreakAchievement(Long userId, int streakDays, String streakType) {
        String message = String.format("🔥 Amazing! You've maintained a %d-day %s learning streak! Keep it up!",
                streakDays, streakType.toLowerCase());
        String actionUrl = "afrilingo://achievements/streak";
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null, actionUrl, null, 3);
    }

    /**
     * Notify milestone reached
     */
    public NotificationDTO notifyMilestone(Long userId, String milestoneType, int value, Long relatedEntityId) {
        String message = generateMilestoneMessage(milestoneType, value);
        String actionUrl = "afrilingo://achievements/" + milestoneType.toLowerCase();
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, relatedEntityId, actionUrl, null, 3);
    }

    /**
     * Notify level up
     */
    public NotificationDTO notifyLevelUp(Long userId, int newLevel, String languageName) {
        String message = String.format("🆙 Level Up! You've reached Level %d in %s! Your skills are improving!", newLevel, languageName);
        String actionUrl = "afrilingo://profile/level";
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null, actionUrl, null, 3);
    }

    // ============ STREAK AND PROGRESS NOTIFICATIONS ============

    /**
     * Notify streak maintenance
     */
    public NotificationDTO notifyStreakMaintained(Long userId, int streakDays) {
        String message = String.format("🔥 Streak alive! Day %d completed. Your consistency is building fluency!", streakDays);
        String actionUrl = "afrilingo://progress/streak";
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null, actionUrl, null, 2);
    }

    /**
     * Notify streak freeze used
     */
    public NotificationDTO notifyStreakFreezeUsed(Long userId, int remainingFreezes) {
        String message = String.format("❄️ Streak freeze used! Your streak is safe. You have %d freeze(s) remaining.", remainingFreezes);
        String actionUrl = "afrilingo://shop/streak-freeze";
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 2);
    }

    /**
     * Notify weekly progress
     */
    public NotificationDTO notifyWeeklyProgress(Long userId, int lessonsCompleted, int totalMinutes, double averageScore) {
        String message = String.format("📊 Week in Review: %d lessons completed, %d minutes studied, %.1f%% average score. Great progress!",
                lessonsCompleted, totalMinutes, averageScore);
        String actionUrl = "afrilingo://progress/weekly";
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 2);
    }

    /**
     * Notify monthly progress
     */
    public NotificationDTO notifyMonthlyProgress(Long userId, int lessonsCompleted, int coursesCompleted, int totalHours) {
        String message = String.format("📅 Monthly Summary: %d lessons, %d courses completed, %d hours of learning. Fantastic dedication!",
                lessonsCompleted, coursesCompleted, totalHours);
        String actionUrl = "afrilingo://progress/monthly";
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 2);
    }

    // ============ REMINDER NOTIFICATIONS ============

    /**
     * Send a general reminder
     */
    public NotificationDTO sendReminder(Long userId, String message, Long relatedEntityId) {
        String actionUrl = relatedEntityId != null ? "afrilingo://lesson/" + relatedEntityId : "afrilingo://dashboard";
        return notifyUser(userId, message, NotificationType.REMINDER, relatedEntityId, actionUrl, null, 2);
    }

    /**
     * Send daily learning reminder
     */
    public NotificationDTO sendDailyReminder(Long userId, int goalMinutes) {
        String message = String.format("📚 Time for your daily %d-minute learning session! Consistency builds fluency.", goalMinutes);
        String actionUrl = "afrilingo://dashboard";
        return notifyUser(userId, message, NotificationType.REMINDER, null, actionUrl, null, 2);
    }

    /**
     * Send streak reminder
     */
    public NotificationDTO sendStreakReminder(Long userId, int currentStreak, int hoursLeft) {
        String message = String.format("🔥 Don't break your %d-day streak! %d hours left to complete today's lesson.", currentStreak, hoursLeft);
        String actionUrl = "afrilingo://dashboard";
        return notifyUser(userId, message, NotificationType.REMINDER, null, actionUrl, null, 3);
    }

    /**
     * Send lesson reminder
     */
    public NotificationDTO sendLessonReminder(Long userId, String lessonTitle, Long lessonId) {
        String message = "📖 Continue your learning journey with: " + lessonTitle;
        String actionUrl = "afrilingo://lesson/" + lessonId;
        return notifyUser(userId, message, NotificationType.REMINDER, lessonId, actionUrl, null, 2);
    }

    /**
     * Send practice reminder
     */
    public NotificationDTO sendPracticeReminder(Long userId, String skillArea) {
        String message = "🎯 Time to practice your " + skillArea + " skills! A few minutes of practice makes a difference.";
        String actionUrl = "afrilingo://practice/" + skillArea.toLowerCase();
        return notifyUser(userId, message, NotificationType.REMINDER, null, actionUrl, null, 2);
    }

    // ============ GOAL NOTIFICATIONS ============

    /**
     * Notify goal achieved
     */
    public NotificationDTO notifyGoalAchieved(Long userId, String goalType, int goalValue, String period) {
        String message = String.format("🎯 Goal Achieved! You've reached your %s goal of %d for %s. Excellent work!",
                goalType, goalValue, period);
        String actionUrl = "afrilingo://goals";
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null, actionUrl, null, 3);
    }

    /**
     * Notify goal progress
     */
    public NotificationDTO notifyGoalProgress(Long userId, String goalType, int current, int target, String period) {
        int percentage = (int) ((double) current / target * 100);
        String message = String.format("📈 %d%% towards your %s goal! %d of %d completed for %s.",
                percentage, goalType, current, target, period);
        String actionUrl = "afrilingo://goals";
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 1);
    }

    // ============ SOCIAL NOTIFICATIONS ============

    /**
     * Notify friend achievement
     */
    public NotificationDTO notifyFriendAchievement(Long userId, String friendName, String achievement) {
        String message = String.format("👥 %s just earned: %s! Congratulate them on their progress.", friendName, achievement);
        String actionUrl = "afrilingo://social/friends";
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 2);
    }

    /**
     * Notify friend activity
     */
    public NotificationDTO notifyFriendActivity(Long userId, String friendName, String activity) {
        String message = String.format("👥 %s just %s. Keep learning together!", friendName, activity);
        String actionUrl = "afrilingo://social/friends";
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 1);
    }

    /**
     * Notify leaderboard position
     */
    public NotificationDTO notifyLeaderboardPosition(Long userId, int position, String leaderboardType) {
        String message = String.format("🏆 You're #%d on the %s leaderboard! Keep learning to climb higher.",
                position, leaderboardType);
        String actionUrl = "afrilingo://leaderboard/" + leaderboardType.toLowerCase();
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null, actionUrl, null, 2);
    }

    // ============ SYSTEM NOTIFICATIONS ============

    /**
     * Send system notification
     */
    public NotificationDTO notifySystem(Long userId, String message) {
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, null, null, 2);
    }

    /**
     * Notify app update
     */
    public NotificationDTO notifyAppUpdate(Long userId, String version, String features) {
        String message = String.format("🆕 App Update %s available! New features: %s", version, features);
        String actionUrl = "afrilingo://app/update";
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 2);
    }

    /**
     * Notify maintenance
     */
    public NotificationDTO notifyMaintenance(Long userId, String maintenanceTime, String duration) {
        String message = String.format("🔧 Scheduled maintenance on %s for %s. Plan your learning accordingly.",
                maintenanceTime, duration);
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, null, null, 2);
    }

    /**
     * Notify new feature
     */
    public NotificationDTO notifyNewFeature(Long userId, String featureName, String description) {
        String message = String.format("✨ New Feature: %s! %s Discover it now!", featureName, description);
        String actionUrl = "afrilingo://features/" + featureName.toLowerCase().replace(" ", "-");
        return notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 2);
    }

    // ============ ENGAGEMENT NOTIFICATIONS ============

    /**
     * Notify comeback after absence
     */
    public NotificationDTO notifyComebackEncouragement(Long userId, int daysAbsent) {
        String message = String.format("👋 Welcome back! We missed you for %d days. Ready to continue your language journey?", daysAbsent);
        String actionUrl = "afrilingo://dashboard";
        return notifyUser(userId, message, NotificationType.REMINDER, null, actionUrl, null, 2);
    }

    /**
     * Notify skill improvement
     */
    public NotificationDTO notifySkillImprovement(Long userId, String skillArea, double improvementPercentage) {
        String message = String.format("📈 Your %s skills improved by %.1f%% this week! Your practice is paying off.",
                skillArea, improvementPercentage);
        String actionUrl = "afrilingo://progress/skills";
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null, actionUrl, null, 2);
    }

    /**
     * Notify vocabulary milestone
     */
    public NotificationDTO notifyVocabularyMilestone(Long userId, int totalWords, String language) {
        String message = String.format("📚 Vocabulary Milestone! You now know %d words in %s. Your language skills are expanding!",
                totalWords, language);
        String actionUrl = "afrilingo://vocabulary";
        return notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null, actionUrl, null, 3);
    }

    // ============ BULK NOTIFICATION METHODS ============

    /**
     * Send notification to multiple users
     */
    public void notifyMultipleUsers(List<Long> userIds, String message, NotificationType type) {
        notifyMultipleUsers(userIds, message, type, null, null, null, 2);
    }

    /**
     * Send notification to multiple users with full parameters
     */
    public void notifyMultipleUsers(List<Long> userIds, String message, NotificationType type,
                                    Long relatedEntityId, String actionUrl, String imageUrl, int priority) {
        for (Long userId : userIds) {
            try {
                notifyUser(userId, message, type, relatedEntityId, actionUrl, imageUrl, priority);
            } catch (Exception e) {
                log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
            }
        }
        log.info("Bulk notification sent to {} users", userIds.size());
    }

    /**
     * Broadcast important announcement
     */
    public void broadcastAnnouncement(List<Long> userIds, String title, String message, String actionUrl) {
        String fullMessage = title + ": " + message;
        notifyMultipleUsers(userIds, fullMessage, NotificationType.SYSTEM_NOTIFICATION, null, actionUrl, null, 3);
    }

    // ============ HELPER METHODS ============

    private String generateMilestoneMessage(String milestoneType, int value) {
        return switch (milestoneType.toUpperCase()) {
            case "LESSONS_COMPLETED" -> String.format("🎓 Milestone! %d lessons completed. Your dedication shows!", value);
            case "QUIZ_PERFECT_SCORES" -> String.format("💯 Perfect! %d perfect quiz scores achieved. Excellence!", value);
            case "STUDY_HOURS" -> String.format("⏰ Time milestone! %d hours of study completed. Impressive!", value);
            case "VOCABULARY_LEARNED" -> String.format("📚 Vocabulary master! %d new words learned. Amazing progress!", value);
            case "CONSECUTIVE_DAYS" -> String.format("🔥 Consistency! %d consecutive days of learning. Outstanding!", value);
            case "COURSES_COMPLETED" -> String.format("🏆 Course champion! %d courses completed. Incredible achievement!", value);
            case "POINTS_EARNED" -> String.format("⭐ Point master! %d points earned. Your effort is remarkable!", value);
            default -> String.format("🏆 Milestone reached! %s: %d. Keep up the excellent work!", milestoneType, value);
        };
    }
}