package edtech.afrilingo.lesson.content;

import edtech.afrilingo.lesson.Lesson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "lesson_contents")
public class LessonContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ContentType contentType; // TEXT, AUDIO, IMAGE
    @Column(columnDefinition = "TEXT")
    private String contentData;
    private String mediaUrl;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
}

