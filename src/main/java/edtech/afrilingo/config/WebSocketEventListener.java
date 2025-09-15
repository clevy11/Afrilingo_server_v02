package edtech.afrilingo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("WebSocket session connected: {}", event.getMessage().getHeaders().get("simpSessionId"));
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        logger.info("WebSocket session disconnected: {}", event.getMessage().getHeaders().get("simpSessionId"));
    }
    
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        logger.info("WebSocket session subscribed: {} to {}", 
                   event.getMessage().getHeaders().get("simpSessionId"),
                   event.getMessage().getHeaders().get("simpDestination"));
    }
    
    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        logger.info("WebSocket session unsubscribed: {}", event.getMessage().getHeaders().get("simpSessionId"));
    }
}

