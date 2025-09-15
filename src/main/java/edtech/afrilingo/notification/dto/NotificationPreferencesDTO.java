package edtech.afrilingo.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesDTO {
    private Long id;
    private Long userId;

    // Push notification preferences
    private boolean pushNotificationsEnabled;
    private boolean courseCompletionNotifications;
    private boolean lessonCompletionNotifications;
    private boolean quizCompletionNotifications;
    private boolean achievementNotifications;
    private boolean reminderNotifications;
    private boolean systemNotifications;

    // Timing preferences
    private String quietHoursStart; // "22:00"
    private String quietHoursEnd; // "08:00"
    private boolean respectQuietHours;

    // Frequency preferences
    private boolean dailyDigest;
    private String dailyDigestTime; // "09:00"
    private boolean weeklyProgress;
    private String weeklyProgressDay; // "SUNDAY"
}