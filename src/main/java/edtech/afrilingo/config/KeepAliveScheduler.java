package edtech.afrilingo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeepAliveScheduler {

    @Value("${afrilingo.keepalive.enabled:false}")
    private boolean enabled;

    @Value("${afrilingo.keepalive.url:}")
    private String keepAliveUrl;

    // Optional auth header for protected endpoints (e.g., Bearer token)
    @Value("${afrilingo.keepalive.auth-header-name:}")
    private String authHeaderName;

    @Value("${afrilingo.keepalive.auth-header-value:}")
    private String authHeaderValue;

    // Default ~9 minutes. Render free idles after ~15 minutes.
    @Value("${afrilingo.keepalive.period-ms:540000}")
    private long periodMs;

    // Initial delay to allow the app to start
    @Value("${afrilingo.keepalive.initial-delay-ms:60000}")
    private long initialDelayMs;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Scheduled(initialDelayString = "${afrilingo.keepalive.initial-delay-ms:60000}",
               fixedDelayString   = "${afrilingo.keepalive.period-ms:540000}")
    public void pingSelf() {
        if (!enabled) {
            return;
        }
        if (keepAliveUrl == null || keepAliveUrl.isBlank()) {
            log.debug("KeepAlive is enabled but afrilingo.keepalive.url is not set");
            return;
        }
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(keepAliveUrl))
                    .timeout(Duration.ofSeconds(10))
                    .GET();

            if (authHeaderName != null && !authHeaderName.isBlank()
                    && authHeaderValue != null && !authHeaderValue.isBlank()) {
                builder.header(authHeaderName, authHeaderValue);
            }

            HttpRequest request = builder.build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            log.debug("KeepAlive ping status: {} to {}", response.statusCode(), keepAliveUrl);
        } catch (Exception e) {
            log.debug("KeepAlive ping failed: {}", e.getMessage());
        }
    }
}
