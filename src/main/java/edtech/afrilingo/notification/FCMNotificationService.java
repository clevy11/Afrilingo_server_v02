package edtech.afrilingo.notification;

import edtech.afrilingo.notification.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMNotificationService {

    @Value("${firebase.server.key:}")
    private String firebaseServerKey;

    @Value("${firebase.api.url:https://fcm.googleapis.com/fcm/send}")
    private String firebaseApiUrl;

    @Value("${firebase.enabled:true}")
    private boolean firebaseEnabled;

    private final UserDeviceTokenRepository userDeviceTokenRepository;
    private final NotificationPreferencesRepository notificationPreferencesRepository;
    private final RestTemplate restTemplate;

    /**
     * Send push notification to user's devices
     * @param userId User ID
     * @param notification Notification to send
     */
    public void sendPushNotification(Long userId, NotificationDTO notification) {
        if (!firebaseEnabled || firebaseServerKey.isEmpty()) {
            log.debug("Firebase push notifications are disabled or not configured");
            return;
        }

        try {
            // Check if user has push notifications enabled
            NotificationPreferences preferences = notificationPreferencesRepository
                    .findByUserId(userId)
                    .orElse(null);

            if (preferences == null || !preferences.isPushNotificationsEnabled()) {
                log.debug("Push notifications disabled for user {}", userId);
                return;
            }

            // Check notification type preferences
            if (!isNotificationTypeEnabled(preferences, notification.getType())) {
                log.debug("Notification type {} disabled for user {}", notification.getType(), userId);
                return;
            }

            // Check quiet hours
            if (preferences.isRespectQuietHours() && isInQuietHours(preferences)) {
                log.debug("In quiet hours for user {}, skipping push notification", userId);
                return;
            }

            // Get active device tokens
            List<UserDeviceToken> deviceTokens = userDeviceTokenRepository
                    .findByUserIdAndActiveTrue(userId);

            if (deviceTokens.isEmpty()) {
                log.debug("No active device tokens for user {}", userId);
                return;
            }

            // Send to each device asynchronously
            for (UserDeviceToken deviceToken : deviceTokens) {
                CompletableFuture.runAsync(() -> sendToDevice(deviceToken, notification));
            }

        } catch (Exception e) {
            log.error("Error processing push notification for user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Send notification to multiple users
     * @param userIds List of user IDs
     * @param notification Notification to send
     */
    public void sendBulkPushNotification(List<Long> userIds, NotificationDTO notification) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }

        log.debug("Sending bulk push notification to {} users", userIds.size());

        for (Long userId : userIds) {
            CompletableFuture.runAsync(() -> sendPushNotification(userId, notification));
        }
    }

    /**
     * Send notification to a specific device token
     * @param deviceToken Device token
     * @param notification Notification to send
     */
    private void sendToDevice(UserDeviceToken deviceToken, NotificationDTO notification) {
        try {
            Map<String, Object> fcmMessage = buildFCMMessage(deviceToken, notification);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "key=" + firebaseServerKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(fcmMessage, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    firebaseApiUrl, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.debug("Push notification sent successfully to device {}",
                        deviceToken.getDeviceId());

                // Update last used timestamp
                deviceToken.setLastUsedAt(java.time.LocalDateTime.now());
                userDeviceTokenRepository.save(deviceToken);

            } else {
                log.warn("Failed to send push notification to device {}: Status {}, Body: {}",
                        deviceToken.getDeviceId(), response.getStatusCode(), response.getBody());

                // If the response indicates invalid token, deactivate it
                if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    handleInvalidToken(deviceToken, response.getBody());
                }
            }

        } catch (RestClientException e) {
            log.error("Network error sending push notification to device {}: {}",
                    deviceToken.getDeviceId(), e.getMessage());

            // Check if it's a token-related error
            if (e.getMessage() != null && (
                    e.getMessage().contains("InvalidRegistration") ||
                            e.getMessage().contains("NotRegistered"))) {
                handleInvalidToken(deviceToken, e.getMessage());
            }

        } catch (Exception e) {
            log.error("Unexpected error sending push notification to device {}: {}",
                    deviceToken.getDeviceId(), e.getMessage(), e);
        }
    }

    /**
     * Handle invalid token by deactivating it
     * @param deviceToken Device token to deactivate
     * @param errorMessage Error message from FCM
     */
    private void handleInvalidToken(UserDeviceToken deviceToken, String errorMessage) {
        try {
            deviceToken.setActive(false);
            userDeviceTokenRepository.save(deviceToken);
            log.info("Deactivated invalid device token for device {}: {}",
                    deviceToken.getDeviceId(), errorMessage);
        } catch (Exception e) {
            log.error("Error deactivating invalid token: {}", e.getMessage());
        }
    }

    /**
     * Build FCM message payload
     * @param deviceToken Device token
     * @param notification Notification data
     * @return FCM message map
     */
    private Map<String, Object> buildFCMMessage(UserDeviceToken deviceToken, NotificationDTO notification) {
        Map<String, Object> message = new HashMap<>();
        message.put("to", deviceToken.getToken());

        // Notification payload (for display when app is in background)
        Map<String, Object> notificationPayload = new HashMap<>();
        notificationPayload.put("title", getNotificationTitle(notification.getType()));
        notificationPayload.put("body", notification.getMessage());
        notificationPayload.put("sound", "default");

        // Add click action for better deep linking
        if (notification.getActionUrl() != null) {
            notificationPayload.put("click_action", "FLUTTER_NOTIFICATION_CLICK");
        }

        // Add image if available
        if (notification.getImageUrl() != null) {
            notificationPayload.put("image", notification.getImageUrl());
        }

        // Device-specific configurations
        Map<String, Object> androidConfig = new HashMap<>();
        Map<String, Object> androidNotification = new HashMap<>();
        androidNotification.put("channel_id", "afrilingo_notifications");
        androidNotification.put("priority", notification.getPriority() >= 3 ? "high" : "default");
        androidConfig.put("notification", androidNotification);

        // Data payload (for handling when app is in foreground)
        Map<String, Object> dataPayload = new HashMap<>();
        dataPayload.put("notificationId", notification.getId().toString());
        dataPayload.put("type", notification.getType().toString());
        dataPayload.put("timestamp", notification.getTimestamp().toString());
        dataPayload.put("priority", String.valueOf(notification.getPriority()));
        dataPayload.put("userId", notification.getUserId().toString());

        if (notification.getRelatedEntityId() != null) {
            dataPayload.put("relatedEntityId", notification.getRelatedEntityId().toString());
        }

        if (notification.getActionUrl() != null) {
            dataPayload.put("actionUrl", notification.getActionUrl());
        }

        if (notification.getImageUrl() != null) {
            dataPayload.put("imageUrl", notification.getImageUrl());
        }

        message.put("notification", notificationPayload);
        message.put("data", dataPayload);

        // Set priority based on notification priority
        if (notification.getPriority() >= 3) {
            message.put("priority", "high");
        } else {
            message.put("priority", "normal");
        }

        // Add time to live (TTL) for message delivery
        message.put("time_to_live", 86400); // 24 hours

        return message;
    }

    /**
     * Get notification title based on type
     * @param type Notification type
     * @return Title string
     */
    private String getNotificationTitle(NotificationType type) {
        return switch (type) {
            case COURSE_COMPLETED -> "Course Completed! 🎉";
            case LESSON_COMPLETED -> "Lesson Completed! ✅";
            case QUIZ_COMPLETED -> "Quiz Completed! 📝";
            case NEW_COURSE_AVAILABLE -> "New Course Available! 📚";
            case ACHIEVEMENT_UNLOCKED -> "Achievement Unlocked! 🏆";
            case REMINDER -> "Learning Reminder 📚";
            case SYSTEM_NOTIFICATION -> "AfriLingo";
        };
    }

    /**
     * Check if notification type is enabled for user
     * @param preferences User preferences
     * @param type Notification type
     * @return true if enabled
     */
    private boolean isNotificationTypeEnabled(NotificationPreferences preferences, NotificationType type) {
        return switch (type) {
            case COURSE_COMPLETED -> preferences.isCourseCompletionNotifications();
            case LESSON_COMPLETED -> preferences.isLessonCompletionNotifications();
            case QUIZ_COMPLETED -> preferences.isQuizCompletionNotifications();
            case ACHIEVEMENT_UNLOCKED -> preferences.isAchievementNotifications();
            case REMINDER -> preferences.isReminderNotifications();
            case SYSTEM_NOTIFICATION, NEW_COURSE_AVAILABLE -> preferences.isSystemNotifications();
        };
    }

    /**
     * Check if current time is in quiet hours
     * @param preferences User preferences
     * @return true if in quiet hours
     */
    private boolean isInQuietHours(NotificationPreferences preferences) {
        if (preferences.getQuietHoursStart() == null || preferences.getQuietHoursEnd() == null) {
            return false;
        }

        try {
            LocalTime now = LocalTime.now();
            LocalTime start = LocalTime.parse(preferences.getQuietHoursStart());
            LocalTime end = LocalTime.parse(preferences.getQuietHoursEnd());

            if (start.isBefore(end)) {
                // Same day quiet hours (e.g., 14:00 to 18:00)
                return !now.isBefore(start) && now.isBefore(end);
            } else {
                // Overnight quiet hours (e.g., 22:00 to 08:00)
                return !now.isBefore(start) || now.isBefore(end);
            }
        } catch (DateTimeParseException e) {
            log.warn("Error parsing quiet hours for user {}: {}",
                    preferences.getUser().getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Test push notification functionality
     * @param userId User ID to send test notification to
     * @return true if test was sent successfully
     */
    public boolean sendTestNotification(Long userId) {
        if (!firebaseEnabled) {
            log.warn("Firebase is disabled, cannot send test notification");
            return false;
        }

        try {
            NotificationDTO testNotification = NotificationDTO.builder()
                    .id(-1L) // Temporary ID for test
                    .userId(userId)
                    .message("This is a test notification to verify your push notification settings.")
                    .timestamp(java.time.LocalDateTime.now())
                    .type(NotificationType.SYSTEM_NOTIFICATION)
                    .priority(2)
                    .read(false)
                    .build();

            sendPushNotification(userId, testNotification);
            return true;
        } catch (Exception e) {
            log.error("Error sending test notification: {}", e.getMessage());
            return false;
        }
    }
}

// Configuration class for RestTemplate
@Configuration
class FCMConfiguration {

    @Bean
    @ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = true)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}