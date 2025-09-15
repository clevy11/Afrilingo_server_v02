package edtech.afrilingo.userProgress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.quiz.option.Option;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_answers")
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    @JsonIgnoreProperties({"answers", "user", "quiz"})
    private UserQuizAttempt attempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnoreProperties({"options", "quiz"})
    private Question question;

    @ManyToOne
    @JoinColumn(name = "option_id")
    @JsonIgnoreProperties({"question"})
    private Option option;
}
