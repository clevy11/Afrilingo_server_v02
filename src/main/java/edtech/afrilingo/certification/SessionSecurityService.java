package edtech.afrilingo.certification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionSecurityService {
    
    private final ConcurrentHashMap<String, SessionSecurityData> activeSecureSessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    public String createSecureSession(Long sessionId, String userId) {
        try {
            // Generate session encryption key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            
            // Generate session token
            SecureRandom random = new SecureRandom();
            byte[] tokenBytes = new byte[32];
            random.nextBytes(tokenBytes);
            String sessionToken = Base64.getEncoder().encodeToString(tokenBytes);
            
            // Store session security data
            SessionSecurityData securityData = SessionSecurityData.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .encryptionKey(secretKey)
                    .sessionToken(sessionToken)
                    .createdAt(System.currentTimeMillis())
                    .lastActivity(System.currentTimeMillis())
                    .active(true)
                    .build();
            
            activeSecureSessions.put(sessionToken, securityData);
            
            // Schedule session timeout (2 hours)
            scheduler.schedule(() -> expireSession(sessionToken), 2, TimeUnit.HOURS);
            
            log.info("Secure session created for certification session: {}", sessionId);
            return sessionToken;
            
        } catch (Exception e) {
            log.error("Error creating secure session: {}", e.getMessage());
            throw new RuntimeException("Failed to create secure session", e);
        }
    }
    
    public boolean validateSessionToken(String sessionToken) {
        SessionSecurityData securityData = activeSecureSessions.get(sessionToken);
        if (securityData == null || !securityData.isActive()) {
            return false;
        }
        
        // Update last activity
        securityData.setLastActivity(System.currentTimeMillis());
        
        // Check for session timeout (30 minutes of inactivity)
        long inactiveTime = System.currentTimeMillis() - securityData.getLastActivity();
        if (inactiveTime > TimeUnit.MINUTES.toMillis(30)) {
            expireSession(sessionToken);
            return false;
        }
        
        return true;
    }
    
    public String encryptData(String sessionToken, String data) {
        try {
            SessionSecurityData securityData = activeSecureSessions.get(sessionToken);
            if (securityData == null) {
                throw new IllegalArgumentException("Invalid session token");
            }
            
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, securityData.getEncryptionKey());
            
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
            
        } catch (Exception e) {
            log.error("Error encrypting data: {}", e.getMessage());
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    public String decryptData(String sessionToken, String encryptedData) {
        try {
            SessionSecurityData securityData = activeSecureSessions.get(sessionToken);
            if (securityData == null) {
                throw new IllegalArgumentException("Invalid session token");
            }
            
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, securityData.getEncryptionKey());
            
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);
            
            return new String(decryptedData);
            
        } catch (Exception e) {
            log.error("Error decrypting data: {}", e.getMessage());
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    public void expireSession(String sessionToken) {
        SessionSecurityData securityData = activeSecureSessions.get(sessionToken);
        if (securityData != null) {
            securityData.setActive(false);
            activeSecureSessions.remove(sessionToken);
            log.info("Session expired: {}", sessionToken);
        }
    }
    
    public void terminateAllUserSessions(String userId) {
        activeSecureSessions.entrySet().removeIf(entry -> {
            SessionSecurityData data = entry.getValue();
            if (userId.equals(data.getUserId())) {
                data.setActive(false);
                log.info("Terminated session for user: {}", userId);
                return true;
            }
            return false;
        });
    }
}