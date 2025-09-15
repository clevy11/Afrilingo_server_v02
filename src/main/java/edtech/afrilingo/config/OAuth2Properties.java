package edtech.afrilingo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.oauth2")
public class OAuth2Properties {
    
    private Android android = new Android();
    private Ios ios = new Ios();
    private Backend backend = new Backend();
    
    @Data
    public static class Android {
        private String clientId;
    }
    
    @Data
    public static class Ios {
        private String clientId;
    }
    
    @Data
    public static class Backend {
        private String clientId;
        private String clientSecret;
    }
}