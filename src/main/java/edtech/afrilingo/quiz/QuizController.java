package edtech.afrilingo.quiz;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.exception.ResourceNotFoundException;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.question.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Endpoints for quiz management")
public class QuizController {

    private final QuizService quizService;
    private final QuestionService questionService;

    @Operation(summary = "Get all quizzes", description = "Returns a list of all quizzes")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Quiz>>> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }

    @Operation(summary = "Get quiz by ID", description = "Returns a quiz by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Quiz>> getQuizById(@PathVariable Long id) {
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        return ResponseEntity.ok(ApiResponse.success(quiz));
    }

    @Operation(summary = "Get quizzes by lesson ID", description = "Returns a list of quizzes for a specific lesson")
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<List<Quiz>>> getQuizzesByLessonId(@PathVariable Long lessonId) {
        List<Quiz> quizzes = quizService.getQuizzesByLessonId(lessonId);
        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }

    @Operation(summary = "Create a quiz", description = "Creates a new quiz")
    @PostMapping
    public ResponseEntity<ApiResponse<Quiz>> createQuiz(@RequestBody Quiz quiz) {
        try {
            Quiz createdQuiz = quizService.createQuiz(quiz);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdQuiz, "Quiz created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Update a quiz", description = "Updates an existing quiz")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Quiz>> updateQuiz(
            @PathVariable Long id,
            @RequestBody Quiz quizDetails
    ) {
        try {
            if (!quizService.existsById(id)) {
                throw new ResourceNotFoundException("Quiz", "id", id);
            }

            Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
            return ResponseEntity.ok(ApiResponse.success(updatedQuiz, "Quiz updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Delete a quiz", description = "Deletes a quiz by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long id) {
        if (!quizService.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", "id", id);
        }

        boolean deleted = quizService.deleteQuiz(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(200)
                        .message("Quiz deleted successfully")
                        .build()
        );
    }

    @Operation(summary = "Get questions for a quiz", description = "Returns all questions for a specific quiz")
    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<List<Question>>> getQuestionsForQuiz(@PathVariable Long id) {
        if (!quizService.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", "id", id);
        }

        List<Question> questions = questionService.getQuestionsByQuizId(id);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @Operation(summary = "Add a question to a quiz", description = "Adds a new question to a quiz")
    @PostMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<Quiz>> addQuestionToQuiz(
            @PathVariable Long id,
            @RequestBody Question question
    ) {
        try {
            if (!quizService.existsById(id)) {
                throw new ResourceNotFoundException("Quiz", "id", id);
            }

            // Set the quiz ID in the question
            question.setQuiz(Quiz.builder().id(id).build());

            Quiz updatedQuiz = quizService.addQuestionToQuiz(id, question);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(updatedQuiz, "Question added to quiz successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Remove a question from a quiz", description = "Removes a question from a quiz")
    @DeleteMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Quiz>> removeQuestionFromQuiz(
            @PathVariable Long quizId,
            @PathVariable Long questionId
    ) {
        try {
            if (!quizService.existsById(quizId)) {
                throw new ResourceNotFoundException("Quiz", "id", quizId);
            }

            if (!questionService.existsById(questionId)) {
                throw new ResourceNotFoundException("Question", "id", questionId);
            }

            Quiz updatedQuiz = quizService.removeQuestionFromQuiz(quizId, questionId);
            return ResponseEntity.ok(ApiResponse.success(updatedQuiz, "Question removed from quiz successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Get quiz statistics", description = "Returns statistics for a specific quiz")
    @GetMapping("/{id}/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuizStatistics(@PathVariable Long id) {
        if (!quizService.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", "id", id);
        }

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalQuestions", questionService.getQuestionsByQuizId(id).size());
        statistics.put("totalPoints", questionService.calculateTotalPoints(id));
        statistics.put("averageScore", quizService.getAverageScore(id));
        statistics.put("passRate", quizService.getPassRate(id));

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @Operation(summary = "Get paginated quizzes", description = "Returns a paginated list of quizzes")
    @GetMapping(value = "/paginated", produces = "application/json")
    public ResponseEntity<ApiResponse<Page<Quiz>>> getPaginatedQuizzes(Pageable pageable) {
        Page<Quiz> quizzes = quizService.getQuizzes(pageable);
        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }
}