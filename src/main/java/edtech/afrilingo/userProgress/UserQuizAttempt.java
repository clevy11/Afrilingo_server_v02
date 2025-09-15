package edtech.afrilingo.userProgress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edtech.afrilingo.quiz.Quiz;
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
@Table(name = "user_quiz_attempts")
public class UserQuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int score;
    private boolean passed;
    private LocalDateTime attemptedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"profile", "authorities", "password"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonIgnoreProperties({"questions", "lesson"})
    private Quiz quiz;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("attempt")
    private List<UserAnswer> answers;
}
