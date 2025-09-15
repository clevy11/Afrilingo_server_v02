package edtech.afrilingo.quiz;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.quiz.option.Option;
import edtech.afrilingo.user.User;
import edtech.afrilingo.userProgress.UserAnswer;
import edtech.afrilingo.userProgress.UserQuizAttempt;
import edtech.afrilingo.userProgress.UserQuizAttemptRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quiz Submission", description = "Endpoints for submitting quiz answers")
public class QuizSubmissionController {

    private final QuizService quizService;
    private final UserQuizAttemptRepository userQuizAttemptRepository;

    @Operation(summary = "Submit quiz answers", description = "Submit answers for a quiz and get results")
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody List<QuizAnswerRequest> answers) {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Get the quiz
        Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Quiz not found"));
        }
        
        Quiz quiz = quizOpt.get();
        
        // Calculate score
        int totalQuestions = quiz.getQuestions().size();
        int correctAnswers = 0;
        
        // Create a map of question ID to selected option ID for easy lookup
        Map<Long, Long> userAnswerMap = answers.stream()
                .collect(Collectors.toMap(QuizAnswerRequest::getQuestionId, QuizAnswerRequest::getSelectedOptionId));
        
        // Create user answers
        List<UserAnswer> userAnswers = new ArrayList<>();
        
        for (Question question : quiz.getQuestions()) {
            Long selectedOptionId = userAnswerMap.get(question.getId());
            
            if (selectedOptionId != null) {
                // Find the selected option
                Optional<Option> selectedOption = question.getOptions().stream()
                        .filter(option -> option.getId().equals(selectedOptionId))
                        .findFirst();
                
                if (selectedOption.isPresent()) {
                    boolean isCorrect = selectedOption.get().isCorrect();
                    
                    if (isCorrect) {
                        correctAnswers++;
                    }
                    
                    // Create user answer (will be saved with the attempt)
                    UserAnswer userAnswer = UserAnswer.builder()
                            .question(question)
                            .option(selectedOption.get())
                            .isCorrect(isCorrect)
                            .build();
                    
                    userAnswers.add(userAnswer);
                }
            }
        }
        
        // Calculate score as percentage
        int score = totalQuestions > 0 ? (correctAnswers * 100) / totalQuestions : 0;
        
        // Determine if passed (assuming 70% is passing)
        boolean passed = score >= 70;
        
        // Create quiz attempt
        UserQuizAttempt attempt = UserQuizAttempt.builder()
                .user(currentUser)
                .quiz(quiz)
                .score(score)
                .passed(passed)
                .attemptedAt(LocalDateTime.now())
                .build();
        
        // Save attempt
        UserQuizAttempt savedAttempt = userQuizAttemptRepository.save(attempt);
        
        // Update user answers with the saved attempt
        userAnswers.forEach(answer -> answer.setAttempt(savedAttempt));
        
        // Update the attempt with answers
        savedAttempt.setAnswers(userAnswers);
        userQuizAttemptRepository.save(savedAttempt);
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("quizId", quizId);
        response.put("totalQuestions", totalQuestions);
        response.put("correctAnswers", correctAnswers);
        response.put("score", score);
        response.put("passed", passed);
        response.put("attemptId", savedAttempt.getId());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

/**
 * Request object for quiz answer submission
 */
class QuizAnswerRequest {
    private Long questionId;
    private Long selectedOptionId;
    
    // Getters and setters
    public Long getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public Long getSelectedOptionId() {
        return selectedOptionId;
    }
    
    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
