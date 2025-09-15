package edtech.afrilingo.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketNotificationController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketNotificationController.class);
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public String ping(String msg) {
        return msg == null ? "pong" : msg;
    }

    public void sendNotification(Long userId, String payload) {
        // Deprecated broadcast to topic retained for compatibility but not used by new flows
        String destination = "/topic/notifications." + userId;
        log.info("[WS] Publishing (compat) to {} payload={}", destination, payload);
        messagingTemplate.convertAndSend(destination, payload);
    }

    /**
     * Send a notification to a specific authenticated user destination using STOMP user prefix.
     * Clients should subscribe to /user/queue/notifications to receive messages addressed to them.
     */
    public void sendNotificationToUser(String username, String payload) {
        String destination = "/queue/notifications";
        log.info("[WS] Publishing to user='{}' destination={} payload={}", username, destination, payload);
        messagingTemplate.convertAndSendToUser(username, destination, payload);
    }
}


