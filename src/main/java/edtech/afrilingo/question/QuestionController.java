package edtech.afrilingo.question;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.exception.ResourceNotFoundException;
import edtech.afrilingo.quiz.option.Option;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/questions"})
@RequiredArgsConstructor
@Tag(name = "Questions", description = "Endpoints for question management")
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "Get all questions", description = "Returns a list of all questions")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Question>>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @Operation(summary = "Get question by ID", description = "Returns a question by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        return ResponseEntity.ok(ApiResponse.success(question));
    }

    @Operation(summary = "Create a question", description = "Creates a new question")
    @PostMapping
    public ResponseEntity<ApiResponse<Question>> createQuestion(@RequestBody Question question) {
        try {
            Question createdQuestion = questionService.createQuestion(question);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdQuestion, "Question created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Update a question", description = "Updates an existing question")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> updateQuestion(
            @PathVariable Long id,
            @RequestBody Question questionDetails
    ) {
        try {
            if (!questionService.existsById(id)) {
                throw new ResourceNotFoundException("Question", "id", id);
            }

            Question updatedQuestion = questionService.updateQuestion(id, questionDetails);
            return ResponseEntity.ok(ApiResponse.success(updatedQuestion, "Question updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Delete a question", description = "Deletes a question by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        if (!questionService.existsById(id)) {
            throw new ResourceNotFoundException("Question", "id", id);
        }

        boolean deleted = questionService.deleteQuestion(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(200)
                        .message("Question deleted successfully")
                        .build()
        );
    }

    @Operation(summary = "Get options for a question", description = "Returns all options for a specific question")
    @GetMapping("/{id}/options")
    public ResponseEntity<ApiResponse<List<Option>>> getOptionsForQuestion(@PathVariable Long id) {
        if (!questionService.existsById(id)) {
            throw new ResourceNotFoundException("Question", "id", id);
        }

        List<Option> options = questionService.getOptionsForQuestion(id);
        return ResponseEntity.ok(ApiResponse.success(options));
    }

    @Operation(summary = "Add an option to a question", description = "Adds a new option to a question")
    @PostMapping("/{id}/options")
    public ResponseEntity<ApiResponse<Question>> addOptionToQuestion(
            @PathVariable Long id,
            @RequestBody Option option
    ) {
        try {
            if (!questionService.existsById(id)) {
                throw new ResourceNotFoundException("Question", "id", id);
            }

            // Set the question ID in the option
            option.setQuestion(Question.builder().id(id).build());

            Question updatedQuestion = questionService.addOptionToQuestion(id, option);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(updatedQuestion, "Option added to question successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Remove an option from a question", description = "Removes an option from a question")
    @DeleteMapping("/{questionId}/options/{optionId}")
    public ResponseEntity<ApiResponse<Question>> removeOptionFromQuestion(
            @PathVariable Long questionId,
            @PathVariable Long optionId
    ) {
        try {
            if (!questionService.existsById(questionId)) {
                throw new ResourceNotFoundException("Question", "id", questionId);
            }

            Question updatedQuestion = questionService.removeOptionFromQuestion(questionId, optionId);
            return ResponseEntity.ok(ApiResponse.success(updatedQuestion, "Option removed from question successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Get questions by quiz ID", description = "Returns questions for a specific quiz")
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<ApiResponse<List<Question>>> getQuestionsByQuizId(@PathVariable Long quizId) {
        List<Question> questions = questionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @Operation(summary = "Get questions by quiz ID and type", description = "Returns questions of a specific type for a quiz")
    @GetMapping("/quiz/{quizId}/type/{questionType}")
    public ResponseEntity<ApiResponse<List<Question>>> getQuestionsByQuizIdAndType(
            @PathVariable Long quizId,
            @PathVariable QuestionType questionType
    ) {
        List<Question> questions = questionService.getQuestionsByQuizIdAndType(quizId, questionType);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @Operation(summary = "Search questions by keyword", description = "Returns questions containing the given keyword")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Question>>> searchQuestionsByKeyword(@RequestParam String keyword) {
        try {
            List<Question> questions = questionService.searchQuestionsByKeyword(keyword);
            return ResponseEntity.ok(ApiResponse.success(questions));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Get paginated questions", description = "Returns a paginated list of questions")
    @GetMapping(value = "/paginated", produces = "application/json")
    public ResponseEntity<ApiResponse<Page<Question>>> getPaginatedQuestions(Pageable pageable) {
        Page<Question> questions = questionService.getQuestions(pageable);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }
}