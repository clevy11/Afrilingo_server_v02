package edtech.afrilingo.certification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.quiz.option.Option;
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
@Table(name = "certification_responses")
public class CertificationQuestionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime answeredAt;
    private long timeSpentMs; // Time spent on this question
    private boolean correct;

    @Column(columnDefinition = "TEXT")
    private String textAnswer; // For open-ended or translation questions

    private Integer score; // Score for AI-graded questions
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonIgnore  // Prevent circular reference
    private CertificationSession session;
    
    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnore  // Prevent deep nesting
    private Question question;
    
    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    @JsonIgnore  // Prevent deep nesting
    private Option selectedOption;
}
