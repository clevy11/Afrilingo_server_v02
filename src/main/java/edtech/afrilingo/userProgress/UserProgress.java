package edtech.afrilingo.userProgress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edtech.afrilingo.lesson.Lesson;
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
@Table(name = "user_progress")
public class UserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private boolean completed;
    private int score;
    private LocalDateTime completedAt;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"profile", "authorities", "password"})
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    @JsonIgnoreProperties({"contents", "quizzes", "course"})
    private Lesson lesson;
}
