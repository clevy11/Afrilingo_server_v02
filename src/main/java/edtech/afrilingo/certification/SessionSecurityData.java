package edtech.afrilingo.certification;

import lombok.Builder;
import lombok.Data;

import javax.crypto.SecretKey;

@Data
@Builder
public class SessionSecurityData {
    private Long sessionId;
    private String userId;
    private SecretKey encryptionKey;
    private String sessionToken;
    private long createdAt;
    private long lastActivity;
    private boolean active;
}