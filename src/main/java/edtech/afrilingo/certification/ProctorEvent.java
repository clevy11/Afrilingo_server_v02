package edtech.afrilingo.certification;

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
@Table(name = "proctor_events")
public class ProctorEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private ProctorEventType eventType;
    
    private String description;
    private LocalDateTime timestamp;
    private String videoSnippetUrl;
    private double confidenceScore; // AI confidence in detection
    private boolean flagged;
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    private CertificationSession session;
}
