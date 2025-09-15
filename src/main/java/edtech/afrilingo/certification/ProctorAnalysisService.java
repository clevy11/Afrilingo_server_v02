package edtech.afrilingo.certification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProctorAnalysisService {
    
    private final ProctorEventRepository proctorEventRepository;
    
    public AnalysisResult analyzeSession(CertificationSession session) {
        List<ProctorEvent> flaggedEvents = proctorEventRepository.findBySessionAndFlaggedTrue(session);
        
        int totalSuspiciousEvents = flaggedEvents.size();
        double averageConfidence = flaggedEvents.stream()
                .mapToDouble(ProctorEvent::getConfidenceScore)
                .average()
                .orElse(0.0);
        
        // Calculate violation severity
        ViolationSeverity severity = calculateViolationSeverity(flaggedEvents);
        
        // Determine if session is certifiable
        boolean certifiable = determineCertifiability(totalSuspiciousEvents, severity, averageConfidence);
        
        return AnalysisResult.builder()
                .sessionId(session.getId())
                .totalSuspiciousEvents(totalSuspiciousEvents)
                .averageConfidenceScore(averageConfidence)
                .violationSeverity(severity)
                .certifiable(certifiable)
                .analysisTimestamp(LocalDateTime.now())
                .recommendedAction(getRecommendedAction(severity, certifiable))
                .build();
    }
    
    private ViolationSeverity calculateViolationSeverity(List<ProctorEvent> flaggedEvents) {
        int criticalEvents = 0;
        int majorEvents = 0;
        int minorEvents = 0;
        
        for (ProctorEvent event : flaggedEvents) {
            switch (event.getEventType()) {
                case MULTIPLE_FACES_DETECTED:
                case PROHIBITED_OBJECT_DETECTED:
                case COPY_PASTE_ATTEMPT:
                case SCREENSHOT_ATTEMPT:
                    criticalEvents++;
                    break;
                case EXTENDED_LOOK_AWAY:
                case SUSPICIOUS_MOVEMENT:
                case BROWSER_TAB_CHANGE:
                    if (event.getConfidenceScore() > 0.9) {
                        majorEvents++;
                    } else {
                        minorEvents++;
                    }
                    break;
                case FACE_NOT_DETECTED:
                case AUDIO_ANOMALY:
                    minorEvents++;
                    break;
                default:
                    break;
            }
        }
        
        if (criticalEvents > 0) return ViolationSeverity.CRITICAL;
        if (majorEvents > 3) return ViolationSeverity.MAJOR;
        if (minorEvents > 10) return ViolationSeverity.MODERATE;
        if (minorEvents > 5) return ViolationSeverity.MINOR;
        return ViolationSeverity.NONE;
    }
    
    private boolean determineCertifiability(int suspiciousEvents, ViolationSeverity severity, double avgConfidence) {
        // Critical violations = not certifiable
        if (severity == ViolationSeverity.CRITICAL) return false;
        
        // Major violations with high confidence = not certifiable
        if (severity == ViolationSeverity.MAJOR && avgConfidence > 0.85) return false;
        
        // Too many minor violations = not certifiable
        if (suspiciousEvents > 15) return false;
        
        return true;
    }
    
    private String getRecommendedAction(ViolationSeverity severity, boolean certifiable) {
        if (!certifiable) {
            return "Certificate not issued due to policy violations. Manual review required.";
        }
        
        switch (severity) {
            case MINOR:
                return "Certificate issued with minor flags noted for review.";
            case MODERATE:
                return "Certificate issued with recommendation for manual review.";
            case MAJOR:
                return "Certificate issued but session flagged for investigation.";
            default:
                return "Certificate issued with clean proctoring record.";
        }
    }
}
