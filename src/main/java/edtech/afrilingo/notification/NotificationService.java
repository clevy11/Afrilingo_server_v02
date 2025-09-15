package edtech.afrilingo.notification;

import edtech.afrilingo.exception.ResourceNotFoundException;
import edtech.afrilingo.notification.dto.DeviceTokenDTO;
import edtech.afrilingo.notification.dto.NotificationDTO;
import edtech.afrilingo.notification.dto.NotificationPreferencesDTO;
import edtech.afrilingo.user.User;
import edtech.afrilingo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserDeviceTokenRepository userDeviceTokenRepository;
    private final NotificationPreferencesRepository notificationPreferencesRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Create a notification with basic parameters
     */
    public NotificationDTO createNotification(Long userId, String message, NotificationType type, Long relatedEntityId) {
        return createNotification(userId, message, type, relatedEntityId, null, null, 2);
    }

    /**
     * Create a notification with all parameters
     */
    public NotificationDTO createNotification(Long userId, String message, NotificationType type,
                                              Long relatedEntityId, String actionUrl, String imageUrl, int priority) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .timestamp(LocalDateTime.now())
                .read(false)
                .type(type)
                .relatedEntityId(relatedEntityId)
                .actionUrl(actionUrl)
                .imageUrl(imageUrl)
                .priority(priority)
                .snoozed(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        NotificationDTO notificationDTO = mapToDTO(savedNotification);

        // Send real-time notification via WebSocket
        try {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),
                    "/queue/notifications",
                    notificationDTO
            );
            log.debug("Real-time notification sent to user: {}", userId);
        } catch (Exception e) {
            log.warn("Failed to send real-time notification to user {}: {}", userId, e.getMessage());
        }

        return notificationDTO;
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        validateUserExists(userId);
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<NotificationDTO> getPaginatedUserNotifications(Long userId, Pageable pageable) {
        validateUserExists(userId);
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId, pageable)
                .map(this::mapToDTO);
    }

    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        validateUserExists(userId);
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.findActiveUnreadNotifications(userId, now)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        validateUserExists(userId);
        return notificationRepository.countUnreadNotifications(userId);
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.isRead()) {
            notification.setRead(true);
            log.debug("Marked notification {} as read", notificationId);
        }

        return mapToDTO(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        validateUserExists(userId);

        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndReadOrderByTimestampDesc(userId, false);

        if (!unreadNotifications.isEmpty()) {
            unreadNotifications.forEach(notification -> notification.setRead(true));
            notificationRepository.saveAll(unreadNotifications);
            log.debug("Marked {} notifications as read for user {}", unreadNotifications.size(), userId);
        }
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }
        notificationRepository.deleteById(notificationId);
        log.debug("Deleted notification {}", notificationId);
    }

    /**
     * Register a device token for push notifications
     */
    @Transactional
    public void registerDeviceToken(Long userId, DeviceTokenDTO deviceTokenDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Optional<UserDeviceToken> existingToken = userDeviceTokenRepository
                .findByToken(deviceTokenDTO.getToken());

        if (existingToken.isPresent()) {
            // Update existing token
            UserDeviceToken token = existingToken.get();
            token.setUser(user); // Ensure it's associated with the current user
            token.setLastUsedAt(LocalDateTime.now());
            token.setActive(true);
            token.setAppVersion(deviceTokenDTO.getAppVersion());
            token.setDeviceType(deviceTokenDTO.getDeviceType());
            userDeviceTokenRepository.save(token);
            log.debug("Updated existing device token for user {}", userId);
        } else {
            // Create new token
            UserDeviceToken newToken = UserDeviceToken.builder()
                    .user(user)
                    .token(deviceTokenDTO.getToken())
                    .deviceType(deviceTokenDTO.getDeviceType())
                    .deviceId(deviceTokenDTO.getDeviceId())
                    .appVersion(deviceTokenDTO.getAppVersion())
                    .registeredAt(LocalDateTime.now())
                    .lastUsedAt(LocalDateTime.now())
                    .active(true)
                    .build();
            userDeviceTokenRepository.save(newToken);
            log.debug("Registered new device token for user {}", userId);
        }
    }

    /**
     * Remove a device token
     */
    @Transactional
    public void removeDeviceToken(Long userId, String token) {
        Optional<UserDeviceToken> deviceToken = userDeviceTokenRepository
                .findByTokenAndUserId(token, userId);

        if (deviceToken.isPresent()) {
            deviceToken.get().setActive(false);
            userDeviceTokenRepository.save(deviceToken.get());
            log.debug("Deactivated device token for user {}", userId);
        } else {
            log.warn("Device token not found for user {} and token {}", userId, token.substring(0, 10) + "...");
        }
    }

    /**
     * Get notification preferences for a user
     */
    public NotificationPreferencesDTO getNotificationPreferences(Long userId) {
        validateUserExists(userId);

        NotificationPreferences preferences = notificationPreferencesRepository
                .findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));

        return mapPreferencesToDTO(preferences);
    }

    /**
     * Update notification preferences for a user
     */
    @Transactional
    public NotificationPreferencesDTO updateNotificationPreferences(Long userId, NotificationPreferencesDTO preferencesDTO) {
        validateUserExists(userId);

        NotificationPreferences preferences = notificationPreferencesRepository
                .findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));

        // Update all preference fields
        updatePreferencesFromDTO(preferences, preferencesDTO);

        NotificationPreferences savedPreferences = notificationPreferencesRepository.save(preferences);
        log.debug("Updated notification preferences for user {}", userId);

        return mapPreferencesToDTO(savedPreferences);
    }

    /**
     * Mark multiple notifications as read
     */
    @Transactional
    public void markMultipleAsRead(Long userId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }

        List<Notification> notifications = notificationRepository
                .findByIdInAndUserId(notificationIds, userId);

        long updatedCount = notifications.stream()
                .filter(notification -> !notification.isRead())
                .peek(notification -> notification.setRead(true))
                .count();

        if (updatedCount > 0) {
            notificationRepository.saveAll(notifications);
            log.debug("Marked {} notifications as read for user {}", updatedCount, userId);
        }
    }

    /**
     * Delete multiple notifications
     */
    @Transactional
    public void deleteMultipleNotifications(Long userId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }

        List<Notification> notifications = notificationRepository
                .findByIdInAndUserId(notificationIds, userId);

        notificationRepository.deleteAll(notifications);
        log.debug("Deleted {} notifications for user {}", notifications.size(), userId);
    }

    /**
     * Get notifications by type
     */
    public List<NotificationDTO> getNotificationsByType(Long userId, NotificationType type, Pageable pageable) {
        validateUserExists(userId);

        Page<Notification> notifications = notificationRepository
                .findByUserIdAndTypeOrderByTimestampDesc(userId, type, pageable);

        return notifications.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get notification summary with statistics
     */
    public Map<String, Object> getNotificationSummary(Long userId) {
        validateUserExists(userId);

        Map<String, Object> summary = new HashMap<>();

        // Total counts
        summary.put("totalNotifications", notificationRepository.countByUserId(userId));
        summary.put("unreadNotifications", notificationRepository.countUnreadNotifications(userId));

        // Counts by type
        Map<String, Long> countsByType = new HashMap<>();
        for (NotificationType type : NotificationType.values()) {
            countsByType.put(type.name(), notificationRepository.countByUserIdAndType(userId, type));
        }
        summary.put("countsByType", countsByType);

        // Recent activity (last 24 hours)
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        summary.put("recentNotifications", notificationRepository
                .countByUserIdAndTimestampAfter(userId, last24Hours));

        // Device tokens count
        summary.put("activeDevices", userDeviceTokenRepository.findByUserIdAndActiveTrue(userId).size());

        return summary;
    }

    /**
     * Snooze a notification
     */
    @Transactional
    public void snoozeNotification(Long userId, Long notificationId, int minutes) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Notification does not belong to the user");
        }

        if (minutes <= 0) {
            throw new IllegalArgumentException("Snooze duration must be positive");
        }

        notification.setSnoozed(true);
        notification.setSnoozedUntil(LocalDateTime.now().plusMinutes(minutes));
        notificationRepository.save(notification);

        log.debug("Snoozed notification {} for {} minutes", notificationId, minutes);
    }

    /**
     * Clear all notifications for a user
     */
    @Transactional
    public void clearAllNotifications(Long userId) {
        validateUserExists(userId);

        long deletedCount = notificationRepository.countByUserId(userId);
        notificationRepository.deleteByUserId(userId);

        log.debug("Cleared {} notifications for user {}", deletedCount, userId);
    }

    /**
     * Process expired snoozed notifications (should be called periodically)
     */
    @Transactional
    public void processExpiredSnoozedNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> expiredNotifications = notificationRepository
                .findExpiredSnoozedNotifications(now);

        if (!expiredNotifications.isEmpty()) {
            expiredNotifications.forEach(notification -> {
                notification.setSnoozed(false);
                notification.setSnoozedUntil(null);
            });

            notificationRepository.saveAll(expiredNotifications);
            log.debug("Processed {} expired snoozed notifications", expiredNotifications.size());
        }
    }

    /**
     * Clean up old notifications (should be called periodically)
     */
    @Transactional
    public void cleanupOldNotifications(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<Notification> oldNotifications = notificationRepository.findOldNotifications(cutoffDate);

        if (!oldNotifications.isEmpty()) {
            notificationRepository.deleteAll(oldNotifications);
            log.info("Cleaned up {} old notifications older than {} days", oldNotifications.size(), daysToKeep);
        }
    }

    // Private helper methods

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
    }

    private NotificationPreferences createDefaultPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        NotificationPreferences preferences = NotificationPreferences.builder()
                .user(user)
                .pushNotificationsEnabled(true)
                .courseCompletionNotifications(true)
                .lessonCompletionNotifications(true)
                .quizCompletionNotifications(true)
                .achievementNotifications(true)
                .reminderNotifications(true)
                .systemNotifications(true)
                .respectQuietHours(false)
                .dailyDigest(false)
                .weeklyProgress(false)
                .build();

        NotificationPreferences saved = notificationPreferencesRepository.save(preferences);
        log.debug("Created default notification preferences for user {}", userId);

        return saved;
    }

    private void updatePreferencesFromDTO(NotificationPreferences preferences, NotificationPreferencesDTO dto) {
        preferences.setPushNotificationsEnabled(dto.isPushNotificationsEnabled());
        preferences.setCourseCompletionNotifications(dto.isCourseCompletionNotifications());
        preferences.setLessonCompletionNotifications(dto.isLessonCompletionNotifications());
        preferences.setQuizCompletionNotifications(dto.isQuizCompletionNotifications());
        preferences.setAchievementNotifications(dto.isAchievementNotifications());
        preferences.setReminderNotifications(dto.isReminderNotifications());
        preferences.setSystemNotifications(dto.isSystemNotifications());
        preferences.setQuietHoursStart(dto.getQuietHoursStart());
        preferences.setQuietHoursEnd(dto.getQuietHoursEnd());
        preferences.setRespectQuietHours(dto.isRespectQuietHours());
        preferences.setDailyDigest(dto.isDailyDigest());
        preferences.setDailyDigestTime(dto.getDailyDigestTime());
        preferences.setWeeklyProgress(dto.isWeeklyProgress());
        preferences.setWeeklyProgressDay(dto.getWeeklyProgressDay());
    }

    private NotificationPreferencesDTO mapPreferencesToDTO(NotificationPreferences preferences) {
        return NotificationPreferencesDTO.builder()
                .id(preferences.getId())
                .userId(preferences.getUser().getId())
                .pushNotificationsEnabled(preferences.isPushNotificationsEnabled())
                .courseCompletionNotifications(preferences.isCourseCompletionNotifications())
                .lessonCompletionNotifications(preferences.isLessonCompletionNotifications())
                .quizCompletionNotifications(preferences.isQuizCompletionNotifications())
                .achievementNotifications(preferences.isAchievementNotifications())
                .reminderNotifications(preferences.isReminderNotifications())
                .systemNotifications(preferences.isSystemNotifications())
                .quietHoursStart(preferences.getQuietHoursStart())
                .quietHoursEnd(preferences.getQuietHoursEnd())
                .respectQuietHours(preferences.isRespectQuietHours())
                .dailyDigest(preferences.isDailyDigest())
                .dailyDigestTime(preferences.getDailyDigestTime())
                .weeklyProgress(preferences.isWeeklyProgress())
                .weeklyProgressDay(preferences.getWeeklyProgressDay())
                .build();
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .message(notification.getMessage())
                .timestamp(notification.getTimestamp())
                .read(notification.isRead())
                .type(notification.getType())
                .relatedEntityId(notification.getRelatedEntityId())
                .snoozed(notification.isSnoozed())
                .snoozedUntil(notification.getSnoozedUntil())
                .priority(notification.getPriority())
                .actionUrl(notification.getActionUrl())
                .imageUrl(notification.getImageUrl())
                .build();
    }
}