package edtech.afrilingo.certification;

import edtech.afrilingo.notification.NotificationEventPublisher;
import edtech.afrilingo.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificationNotificationService {
    
    private final NotificationEventPublisher notificationPublisher;
    
    public void notifyCertificationStarted(Long userId, String language) {
        String message = String.format("🎓 Your %s certification test has started. Good luck!", 
                getLanguageDisplayName(language));
        
        notificationPublisher.notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, 
                null, null, null, 3);
    }
    
    public void notifyCertificationCompleted(Long userId, String language, boolean passed, int score) {
        String message;
        NotificationType type;
        
        if (passed) {
            message = String.format("🎉 Congratulations! You passed your %s certification with %d%%. Your certificate is being generated.", 
                    getLanguageDisplayName(language), score);
            type = NotificationType.ACHIEVEMENT_UNLOCKED;
        } else {
            message = String.format("📚 You scored %d%% on your %s certification. Keep practicing and try again!", 
                    score, getLanguageDisplayName(language));
            type = NotificationType.SYSTEM_NOTIFICATION;
        }
        
        notificationPublisher.notifyUser(userId, message, type, null, null, null, 3);
    }
    
    public void notifyCertificateIssued(Long userId, String certificateId, String language) {
        String message = String.format("📜 Your %s proficiency certificate is ready! Certificate ID: %s", 
                getLanguageDisplayName(language), certificateId);
        
        String actionUrl = "afrilingo://certificates/" + certificateId;
        
        notificationPublisher.notifyUser(userId, message, NotificationType.ACHIEVEMENT_UNLOCKED, 
                null, actionUrl, null, 3);
    }
    
    public void notifyProctorViolation(Long userId, String violationType) {
        String message = String.format("⚠️ Proctoring alert: %s detected during your certification test. Please ensure you follow all test guidelines.", 
                violationType);
        
        notificationPublisher.notifyUser(userId, message, NotificationType.SYSTEM_NOTIFICATION, 
                null, null, null, 2);
    }
    
    private String getLanguageDisplayName(String languageCode) {
        switch (languageCode.toLowerCase()) {
            case "rw": case "kin": return "Kinyarwanda";
            case "sw": case "swa": return "Swahili";
            case "am": case "amh": return "Amharic";
            case "ha": case "hau": return "Hausa";
            case "yo": case "yor": return "Yoruba";
            case "ig": case "ibo": return "Igbo";
            case "zu": case "zul": return "Zulu";
            case "af": case "afr": return "Afrikaans";
            default: return languageCode.toUpperCase();
        }
    }
}
