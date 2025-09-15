package edtech.afrilingo.certification;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnalysisResult {
    private Long sessionId;
    private int totalSuspiciousEvents;
    private double averageConfidenceScore;
    private ViolationSeverity violationSeverity;
    private boolean certifiable;
    private LocalDateTime analysisTimestamp;
    private String recommendedAction;
}