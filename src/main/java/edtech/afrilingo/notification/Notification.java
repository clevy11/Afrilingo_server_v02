package edtech.afrilingo.notification;

import edtech.afrilingo.notification.NotificationType;
import edtech.afrilingo.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean read;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // Optional field to store the ID of the related entity (e.g., course, lesson, quiz)
    private Long relatedEntityId;

    // Enhanced fields for Flutter integration
    private LocalDateTime snoozedUntil;

    @Column(nullable = false)
    private boolean snoozed = false;

    @Column(nullable = false)
    private int priority = 2; // 1 = low, 2 = normal, 3 = high

    private String actionUrl; // Deep link URL for Flutter app
    private String imageUrl; // Optional image for rich notifications
}