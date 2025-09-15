package edtech.afrilingo.notification;

import edtech.afrilingo.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Push notification preferences
    @Column(nullable = false)
    private boolean pushNotificationsEnabled = true;

    @Column(nullable = false)
    private boolean courseCompletionNotifications = true;

    @Column(nullable = false)
    private boolean lessonCompletionNotifications = true;

    @Column(nullable = false)
    private boolean quizCompletionNotifications = true;

    @Column(nullable = false)
    private boolean achievementNotifications = true;

    @Column(nullable = false)
    private boolean reminderNotifications = true;

    @Column(nullable = false)
    private boolean systemNotifications = true;

    // Timing preferences
    private String quietHoursStart; // "22:00"
    private String quietHoursEnd; // "08:00"

    @Column(nullable = false)
    private boolean respectQuietHours = false;

    // Frequency preferences
    @Column(nullable = false)
    private boolean dailyDigest = false;
    private String dailyDigestTime; // "09:00"

    @Column(nullable = false)
    private boolean weeklyProgress = false;
    private String weeklyProgressDay; // "SUNDAY"
}
