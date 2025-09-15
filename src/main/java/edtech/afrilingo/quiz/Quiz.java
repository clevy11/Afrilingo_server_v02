package edtech.afrilingo.quiz;

import edtech.afrilingo.lesson.Lesson;
import edtech.afrilingo.question.Question;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private int minPassingScore;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    @JsonIgnoreProperties({"quizzes", "hibernateLazyInitializer", "handler"})
    private Lesson lesson;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("quiz")
    @JsonIgnore // fetch questions via dedicated endpoint to avoid huge payloads
    private List<Question> questions;
}
