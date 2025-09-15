package edtech.afrilingo.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for quiz submission containing user answers
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDTO {
    /**
     * Map of question IDs to selected option IDs
     */
    private Map<Long, Long> answers;
}