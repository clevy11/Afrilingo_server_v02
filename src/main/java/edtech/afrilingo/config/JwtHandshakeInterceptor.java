package edtech.afrilingo.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            var uri = request.getURI();
            var params = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
            String token = params.getFirst("access_token");
            if (token != null && !token.isBlank()) {
                String username = jwtService.extractUsername(token);
                if (username != null && !username.isBlank()) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        Principal principal = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        attributes.put("principal", principal);
                        logger.debug("WS handshake authenticated via query param for user: {}", username);
                    } else {
                        logger.warn("Invalid JWT on WS handshake for username: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("WS handshake auth failed: {}", e.getMessage());
        }
        return true; // proceed regardless; STOMP CONNECT interceptor may still auth
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
