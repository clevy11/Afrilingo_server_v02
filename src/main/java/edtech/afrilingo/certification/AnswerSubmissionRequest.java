package edtech.afrilingo.certification;

import lombok.Data;

@Data
public class AnswerSubmissionRequest {
    private Long questionId;
    private Long selectedOptionId;
    private String textAnswer;
    private Integer score;
    private long timeSpentMs;
}
