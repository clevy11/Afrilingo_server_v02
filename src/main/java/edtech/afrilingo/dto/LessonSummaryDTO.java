package edtech.afrilingo.dto;

import edtech.afrilingo.lesson.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private LessonType type;
    private int orderIndex;
    private boolean required;
    private Long courseId;
    private String courseTitle;
}
