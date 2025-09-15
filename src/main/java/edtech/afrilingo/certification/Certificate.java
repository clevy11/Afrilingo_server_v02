package edtech.afrilingo.certification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edtech.afrilingo.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String certificateId; // Unique verifiable identifier
    
    private String languageTested;
    private String proficiencyLevel;
    private int finalScore;
    private LocalDateTime completedAt;
    private LocalDateTime issuedAt;
    private String certificateUrl; // URL to PDF certificate
    private boolean verified;
    
    // Store the certificate PDF as Base64 to allow direct retrieval
    @Lob
    @Column(name = "certificate_pdf_base64")
    private String certificatePdfBase64;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore  // Prevent circular reference
    private User user;
    
    @OneToOne(mappedBy = "certificate", cascade = CascadeType.ALL)
    @JsonIgnore  // Prevent circular reference
    private CertificationSession session;
}
