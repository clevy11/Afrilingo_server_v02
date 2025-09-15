package edtech.afrilingo.notification.dto;

import edtech.afrilingo.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;
    private NotificationType type;
    private Long relatedEntityId;

    // Enhanced fields for Flutter integration
    private boolean snoozed;
    private LocalDateTime snoozedUntil;
    private int priority;
    private String actionUrl;
    private String imageUrl;
}