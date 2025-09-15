package edtech.afrilingo.certification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edtech.afrilingo.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "certification_sessions")
public class CertificationSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String sessionId;
    
    private String languageCode;
    private String testLevel; // BEGINNER, INTERMEDIATE, ADVANCED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean completed;
    private boolean passed;
    private int totalQuestions;
    private int correctAnswers;
    private int finalScore;
    
    // Proctoring data
    private boolean cameraVerified;
    private boolean environmentVerified;
    private String videoRecordingUrl;
    private String proxyData; // JSON storing proctoring events
    private int suspiciousActivityCount;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore  // Prevent circular reference
    private User user;
    
    @OneToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    @JsonIgnore  // Prevent circular reference and deep nesting
    private List<ProctorEvent> proctorEvents;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    @JsonIgnore  // Prevent circular reference and deep nesting
    private List<CertificationQuestionResponse> responses;
}
