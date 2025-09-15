package edtech.afrilingo.notification;

import edtech.afrilingo.notification.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    /**
     * Handles notification acknowledgments from clients
     * @param notificationDTO The notification that was acknowledged
     */
    @MessageMapping("/notifications/ack")
    public void acknowledgeNotification(NotificationDTO notificationDTO) {
        if (notificationDTO.getId() != null) {
            notificationService.markAsRead(notificationDTO.getId());
        }
    }
} 