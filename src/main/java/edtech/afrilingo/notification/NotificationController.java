package edtech.afrilingo.notification;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.notification.dto.DeviceTokenDTO;
import edtech.afrilingo.notification.dto.NotificationDTO;
import edtech.afrilingo.notification.dto.NotificationPreferencesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import edtech.afrilingo.user.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Comprehensive notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationEventPublisher notificationEventPublisher;
    private final FCMNotificationService fcmNotificationService;

    // ============ BASIC NOTIFICATION CRUD OPERATIONS ============

    @Operation(summary = "Get current user's notifications", description = "Returns a list of all notifications for the current authenticated user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getCurrentUserNotifications() {
        User currentUser = getCurrentUser();
        List<NotificationDTO> notifications = notificationService.getUserNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @Operation(summary = "Get paginated notifications", description = "Returns a paginated list of notifications for the current user")
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getPaginatedNotifications(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<NotificationDTO> notifications = notificationService.getPaginatedUserNotifications(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @Operation(summary = "Get unread notifications", description = "Returns a list of unread notifications for the current user")
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications() {
        User currentUser = getCurrentUser();
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @Operation(summary = "Get unread notification count", description = "Returns the count of unread notifications for the current user")
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        User currentUser = getCurrentUser();
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @Operation(summary = "Mark a notification as read", description = "Marks a specific notification as read")
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDTO>> markAsRead(@PathVariable Long id) {
        NotificationDTO notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification marked as read"));
    }

    @Operation(summary = "Mark all notifications as read", description = "Marks all notifications for the current user as read")
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        User currentUser = getCurrentUser();
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("All notifications marked as read")
                .build());
    }

    @Operation(summary = "Delete a notification", description = "Deletes a specific notification")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Notification deleted successfully")
                .build());
    }

    // ============ COURSE-RELATED NOTIFICATIONS ============

    @Operation(summary = "Notify course completion", description = "Creates a course completion notification for a user")
    @PostMapping("/course-completed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyCourseCompleted(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Course ID") @RequestParam Long courseId,
            @Parameter(description = "Course name") @RequestParam String courseName) {

        NotificationDTO notification = notificationEventPublisher.notifyCourseCompleted(userId, courseId, courseName);
        return ResponseEntity.ok(ApiResponse.success(notification, "Course completion notification sent"));
    }

    @Operation(summary = "Notify new course available", description = "Creates a new course availability notification")
    @PostMapping("/new-course-available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyNewCourseAvailable(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Course ID") @RequestParam Long courseId,
            @Parameter(description = "Course name") @RequestParam String courseName) {

        NotificationDTO notification = notificationEventPublisher.notifyNewCourseAvailable(userId, courseId, courseName);
        return ResponseEntity.ok(ApiResponse.success(notification, "New course notification sent"));
    }

    @Operation(summary = "Bulk notify new course to all users", description = "Notifies all active users about a new course")
    @PostMapping("/new-course-available/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> bulkNotifyNewCourse(
            @Parameter(description = "Course ID") @RequestParam Long courseId,
            @Parameter(description = "Course name") @RequestParam String courseName,
            @Parameter(description = "List of user IDs") @RequestBody List<Long> userIds) {

        for (Long userId : userIds) {
            notificationEventPublisher.notifyNewCourseAvailable(userId, courseId, courseName);
        }

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("New course notifications sent to " + userIds.size() + " users")
                .build());
    }

    // ============ LESSON-RELATED NOTIFICATIONS ============

    @Operation(summary = "Notify lesson completion", description = "Creates a lesson completion notification for a user")
    @PostMapping("/lesson-completed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyLessonCompleted(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Lesson ID") @RequestParam Long lessonId,
            @Parameter(description = "Lesson title") @RequestParam String lessonTitle) {

        NotificationDTO notification = notificationEventPublisher.notifyLessonCompleted(userId, lessonId, lessonTitle);
        return ResponseEntity.ok(ApiResponse.success(notification, "Lesson completion notification sent"));
    }

    // ============ QUIZ-RELATED NOTIFICATIONS ============

    @Operation(summary = "Notify quiz completion", description = "Creates a quiz completion notification with score")
    @PostMapping("/quiz-completed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyQuizCompleted(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Quiz ID") @RequestParam Long quizId,
            @Parameter(description = "Quiz title") @RequestParam String quizTitle,
            @Parameter(description = "Score achieved") @RequestParam double score) {

        NotificationDTO notification = notificationEventPublisher.notifyQuizCompleted(userId, quizId, quizTitle, score);
        return ResponseEntity.ok(ApiResponse.success(notification, "Quiz completion notification sent"));
    }

    // ============ ACHIEVEMENT NOTIFICATIONS ============

    @Operation(summary = "Notify achievement unlocked", description = "Creates an achievement unlock notification")
    @PostMapping("/achievement-unlocked")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyAchievementUnlocked(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Achievement name") @RequestParam String achievementName) {

        NotificationDTO notification = notificationEventPublisher.notifyAchievementUnlocked(userId, achievementName);
        return ResponseEntity.ok(ApiResponse.success(notification, "Achievement notification sent"));
    }

    @Operation(summary = "Notify streak achievement", description = "Creates a learning streak achievement notification")
    @PostMapping("/streak-achievement")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyStreakAchievement(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Streak count") @RequestParam int streakDays,
            @Parameter(description = "Streak type") @RequestParam(defaultValue = "DAILY") String streakType) {

        String achievementName = String.format("%d-Day %s Learning Streak", streakDays, streakType);
        String message = String.format("🔥 Amazing! You've maintained a %d-day %s learning streak! Keep up the great work!",
                streakDays, streakType.toLowerCase());

        NotificationDTO notification = notificationEventPublisher.notifyUser(
                userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null,
                "afrilingo://achievements/streak", null, 3);

        return ResponseEntity.ok(ApiResponse.success(notification, "Streak achievement notification sent"));
    }

    // ============ REMINDER NOTIFICATIONS ============

    @Operation(summary = "Send learning reminder", description = "Sends a learning reminder notification")
    @PostMapping("/reminder")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> sendLearningReminder(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Reminder message") @RequestParam String message,
            @Parameter(description = "Related entity ID", required = false) @RequestParam(required = false) Long relatedEntityId) {

        NotificationDTO notification = notificationEventPublisher.sendReminder(userId, message, relatedEntityId);
        return ResponseEntity.ok(ApiResponse.success(notification, "Learning reminder sent"));
    }

    @Operation(summary = "Send daily learning reminder", description = "Sends a daily learning reminder")
    @PostMapping("/reminder/daily")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> sendDailyReminder(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Minutes goal") @RequestParam(defaultValue = "15") int dailyGoalMinutes) {

        String message = String.format("📚 Time for your daily %d-minute learning session! Your consistent effort is building fluency.",
                dailyGoalMinutes);

        NotificationDTO notification = notificationEventPublisher.sendReminder(userId, message, null);
        return ResponseEntity.ok(ApiResponse.success(notification, "Daily reminder sent"));
    }

    @Operation(summary = "Send streak reminder", description = "Reminds user to maintain their learning streak")
    @PostMapping("/reminder/streak")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> sendStreakReminder(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Current streak") @RequestParam int currentStreak,
            @Parameter(description = "Hours until streak expires") @RequestParam(defaultValue = "2") int hoursLeft) {

        String message = String.format("🔥 Don't break your %d-day streak! You have %d hours left to complete today's lesson.",
                currentStreak, hoursLeft);

        NotificationDTO notification = notificationEventPublisher.sendReminder(userId, message, null);
        return ResponseEntity.ok(ApiResponse.success(notification, "Streak reminder sent"));
    }

    // ============ MILESTONE NOTIFICATIONS ============

    @Operation(summary = "Notify learning milestone", description = "Creates a learning milestone notification")
    @PostMapping("/milestone")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyMilestone(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Milestone type") @RequestParam String milestoneType,
            @Parameter(description = "Milestone value") @RequestParam int milestoneValue,
            @Parameter(description = "Related entity ID", required = false) @RequestParam(required = false) Long relatedEntityId) {

        String message = generateMilestoneMessage(milestoneType, milestoneValue);
        String actionUrl = generateMilestoneActionUrl(milestoneType, relatedEntityId);

        NotificationDTO notification = notificationEventPublisher.notifyUser(
                userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, relatedEntityId,
                actionUrl, null, 3);

        return ResponseEntity.ok(ApiResponse.success(notification, "Milestone notification sent"));
    }

    // ============ PROGRESS NOTIFICATIONS ============

    @Operation(summary = "Notify weekly progress", description = "Sends weekly learning progress summary")
    @PostMapping("/progress/weekly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyWeeklyProgress(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Lessons completed this week") @RequestParam int lessonsCompleted,
            @Parameter(description = "Total learning time in minutes") @RequestParam int totalMinutes,
            @Parameter(description = "Quiz average score") @RequestParam(defaultValue = "0") double averageScore) {

        String message = String.format("📊 Week in Review: You completed %d lessons, studied for %d minutes, and achieved an average quiz score of %.1f%%. Great progress!",
                lessonsCompleted, totalMinutes, averageScore);

        NotificationDTO notification = notificationEventPublisher.notifyUser(
                userId, message, NotificationType.SYSTEM_NOTIFICATION, null,
                "afrilingo://progress/weekly", null, 2);

        return ResponseEntity.ok(ApiResponse.success(notification, "Weekly progress notification sent"));
    }

    @Operation(summary = "Notify learning goal achieved", description = "Notifies when user achieves their learning goal")
    @PostMapping("/goal-achieved")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyGoalAchieved(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Goal type") @RequestParam String goalType,
            @Parameter(description = "Goal target") @RequestParam int goalTarget,
            @Parameter(description = "Achievement period") @RequestParam(defaultValue = "today") String period) {

        String message = String.format("🎯 Goal Achieved! You've reached your %s goal of %d %s %s. Excellent dedication!",
                goalType, goalTarget, goalType.toLowerCase(), period);

        NotificationDTO notification = notificationEventPublisher.notifyUser(
                userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, null,
                "afrilingo://goals", null, 3);

        return ResponseEntity.ok(ApiResponse.success(notification, "Goal achievement notification sent"));
    }

    // ============ SOCIAL NOTIFICATIONS ============

    @Operation(summary = "Notify friend activity", description = "Notifies about friend's learning activity")
    @PostMapping("/friend-activity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    public ResponseEntity<ApiResponse<NotificationDTO>> notifyFriendActivity(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "Friend's name") @RequestParam String friendName,
            @Parameter(description = "Activity type") @RequestParam String activityType,
            @Parameter(description = "Activity details") @RequestParam String activityDetails) {

        String message = String.format("👥 %s just %s %s! Keep learning together!",
                friendName, activityType, activityDetails);

        NotificationDTO notification = notificationEventPublisher.notifyUser(
                userId, message, NotificationType.SYSTEM_NOTIFICATION, null,
                "afrilingo://social", null, 2);

        return ResponseEntity.ok(ApiResponse.success(notification, "Friend activity notification sent"));
    }

    // ============ SYSTEM NOTIFICATIONS ============

    @Operation(summary = "Send system notification", description = "Sends a general system notification")
    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDTO>> sendSystemNotification(
            @Parameter(description = "User ID") @RequestParam Long userId,
            @Parameter(description = "System message") @RequestParam String message,
            @Parameter(description = "Priority level") @RequestParam(defaultValue = "2") int priority,
            @Parameter(description = "Action URL", required = false) @RequestParam(required = false) String actionUrl,
            @Parameter(description = "Image URL", required = false) @RequestParam(required = false) String imageUrl) {

        NotificationDTO notification = notificationEventPublisher.notifyUser(
                userId, message, NotificationType.SYSTEM_NOTIFICATION, null,
                actionUrl, imageUrl, priority);

        return ResponseEntity.ok(ApiResponse.success(notification, "System notification sent"));
    }

    @Operation(summary = "Broadcast system notification", description = "Sends a system notification to multiple users")
    @PostMapping("/system/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> broadcastSystemNotification(
            @Parameter(description = "List of user IDs") @RequestBody List<Long> userIds,
            @Parameter(description = "System message") @RequestParam String message,
            @Parameter(description = "Priority level") @RequestParam(defaultValue = "2") int priority,
            @Parameter(description = "Action URL", required = false) @RequestParam(required = false) String actionUrl,
            @Parameter(description = "Image URL", required = false) @RequestParam(required = false) String imageUrl) {

        for (Long userId : userIds) {
            notificationEventPublisher.notifyUser(
                    userId, message, NotificationType.SYSTEM_NOTIFICATION, null,
                    actionUrl, imageUrl, priority);
        }

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("System notification broadcast to " + userIds.size() + " users")
                .build());
    }

    // ============ DEVICE TOKEN MANAGEMENT ============

    @Operation(summary = "Register device token for push notifications",
            description = "Registers a device token for Firebase Cloud Messaging (FCM) push notifications")
    @PostMapping("/device-token")
    public ResponseEntity<ApiResponse<Void>> registerDeviceToken(@RequestBody DeviceTokenDTO deviceTokenDTO) {
        User currentUser = getCurrentUser();
        notificationService.registerDeviceToken(currentUser.getId(), deviceTokenDTO);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Device token registered successfully")
                .build());
    }

    @Operation(summary = "Remove device token",
            description = "Removes a device token (useful when user logs out)")
    @DeleteMapping("/device-token/{token}")
    public ResponseEntity<ApiResponse<Void>> removeDeviceToken(@PathVariable String token) {
        User currentUser = getCurrentUser();
        notificationService.removeDeviceToken(currentUser.getId(), token);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Device token removed successfully")
                .build());
    }

    // ============ NOTIFICATION PREFERENCES ============

    @Operation(summary = "Get notification preferences",
            description = "Returns the user's notification preferences")
    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<NotificationPreferencesDTO>> getNotificationPreferences() {
        User currentUser = getCurrentUser();
        NotificationPreferencesDTO preferences = notificationService.getNotificationPreferences(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(preferences));
    }

    @Operation(summary = "Update notification preferences",
            description = "Updates the user's notification preferences")
    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<NotificationPreferencesDTO>> updateNotificationPreferences(
            @RequestBody NotificationPreferencesDTO preferencesDTO) {
        User currentUser = getCurrentUser();
        NotificationPreferencesDTO updatedPreferences = notificationService.updateNotificationPreferences(
                currentUser.getId(), preferencesDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedPreferences, "Notification preferences updated successfully"));
    }

    // ============ ADVANCED OPERATIONS ============

    @Operation(summary = "Mark multiple notifications as read",
            description = "Marks multiple notifications as read by providing a list of notification IDs")
    @PutMapping("/bulk-read")
    public ResponseEntity<ApiResponse<Void>> markMultipleAsRead(@RequestBody List<Long> notificationIds) {
        User currentUser = getCurrentUser();
        notificationService.markMultipleAsRead(currentUser.getId(), notificationIds);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Notifications marked as read")
                .build());
    }

    @Operation(summary = "Delete multiple notifications",
            description = "Deletes multiple notifications by providing a list of notification IDs")
    @DeleteMapping("/bulk-delete")
    public ResponseEntity<ApiResponse<Void>> deleteMultipleNotifications(@RequestBody List<Long> notificationIds) {
        User currentUser = getCurrentUser();
        notificationService.deleteMultipleNotifications(currentUser.getId(), notificationIds);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Notifications deleted successfully")
                .build());
    }

    @Operation(summary = "Get notifications by type",
            description = "Returns notifications filtered by type")
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotificationsByType(
            @PathVariable NotificationType type,
            Pageable pageable) {
        User currentUser = getCurrentUser();
        List<NotificationDTO> notifications = notificationService.getNotificationsByType(currentUser.getId(), type, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    @Operation(summary = "Get notification summary",
            description = "Returns a summary of notifications including counts by type")
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationSummary() {
        User currentUser = getCurrentUser();
        Map<String, Object> summary = notificationService.getNotificationSummary(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @Operation(summary = "Test notification",
            description = "Sends a test notification to the current user (for testing purposes)")
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<NotificationDTO>> sendTestNotification() {
        User currentUser = getCurrentUser();
        NotificationDTO notification = notificationEventPublisher.notifySystem(
                currentUser.getId(),
                "This is a test notification to verify your notification settings are working correctly."
        );
        return ResponseEntity.ok(ApiResponse.success(notification, "Test notification sent"));
    }

    @Operation(summary = "Test FCM push notification", description = "Sends a test FCM push notification")
    @PostMapping("/test/fcm")
    public ResponseEntity<ApiResponse<Void>> sendTestFCMNotification() {
        User currentUser = getCurrentUser();
        boolean success = fcmNotificationService.sendTestNotification(currentUser.getId());

        if (success) {
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status(200)
                    .message("Test FCM notification sent successfully")
                    .build());
        } else {
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status(500)
                    .message("Failed to send test FCM notification")
                    .build());
        }
    }

    @Operation(summary = "Snooze notification",
            description = "Snoozes a notification for a specified duration")
    @PutMapping("/{id}/snooze")
    public ResponseEntity<ApiResponse<Void>> snoozeNotification(
            @PathVariable Long id,
            @RequestParam int minutes) {
        User currentUser = getCurrentUser();
        notificationService.snoozeNotification(currentUser.getId(), id, minutes);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Notification snoozed for " + minutes + " minutes")
                .build());
    }

    @Operation(summary = "Clear all notifications",
            description = "Deletes all notifications for the current user")
    @DeleteMapping("/clear-all")
    public ResponseEntity<ApiResponse<Void>> clearAllNotifications() {
        User currentUser = getCurrentUser();
        notificationService.clearAllNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("All notifications cleared")
                .build());
    }

    // ============ HELPER METHODS ============

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private String generateMilestoneMessage(String milestoneType, int milestoneValue) {
        return switch (milestoneType.toUpperCase()) {
            case "LESSONS_COMPLETED" -> String.format("🎓 Milestone Reached! You've completed %d lessons. Your dedication is paying off!", milestoneValue);
            case "QUIZ_PERFECT_SCORES" -> String.format("💯 Perfect Performance! You've achieved %d perfect quiz scores. Outstanding work!", milestoneValue);
            case "STUDY_HOURS" -> String.format("⏰ Time Milestone! You've studied for %d hours total. Your commitment is impressive!", milestoneValue);
            case "VOCABULARY_LEARNED" -> String.format("📚 Vocabulary Master! You've learned %d new words. Your language skills are growing!", milestoneValue);
            case "CONSECUTIVE_DAYS" -> String.format("🔥 Consistency Champion! You've learned for %d consecutive days. Amazing dedication!", milestoneValue);
            default -> String.format("🏆 Milestone Achieved! You've reached the %s milestone of %d. Keep up the excellent work!", milestoneType, milestoneValue);
        };
    }

    private String generateMilestoneActionUrl(String milestoneType, Long relatedEntityId) {
        return switch (milestoneType.toUpperCase()) {
            case "LESSONS_COMPLETED" -> "afrilingo://achievements/lessons";
            case "QUIZ_PERFECT_SCORES" -> "afrilingo://achievements/quizzes";
            case "STUDY_HOURS" -> "afrilingo://achievements/time";
            case "VOCABULARY_LEARNED" -> "afrilingo://achievements/vocabulary";
            case "CONSECUTIVE_DAYS" -> "afrilingo://achievements/streak";
            default -> relatedEntityId != null ? "afrilingo://achievements/" + relatedEntityId : "afrilingo://achievements";
        };
    }
}