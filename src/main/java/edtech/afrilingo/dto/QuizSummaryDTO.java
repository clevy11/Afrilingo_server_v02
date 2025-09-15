package edtech.afrilingo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private int minPassingScore;
    private Long lessonId;
    private String lessonTitle;
}
