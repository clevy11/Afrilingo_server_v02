package edtech.afrilingo.certification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponseDTO {
    private String certificateId;
    private String languageTested;
    private String proficiencyLevel;
    private int finalScore;
    private LocalDateTime completedAt;
    private LocalDateTime issuedAt;
    private String certificateUrl;
    private boolean verified;
    private String certificatePdfBase64;
    
    // User information (minimal to avoid circular references)
    private String userName;
    private String userEmail;
}