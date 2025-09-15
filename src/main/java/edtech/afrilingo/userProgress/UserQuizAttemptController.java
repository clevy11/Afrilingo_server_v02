package edtech.afrilingo.userProgress;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import edtech.afrilingo.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quiz-attempts")
@RequiredArgsConstructor
@Tag(name = "Quiz Attempts", description = "Endpoints for managing user quiz attempts")
public class UserQuizAttemptController {

    private final UserQuizAttemptService userQuizAttemptService;

    @Operation(summary = "Get all quiz attempts for current user", description = "Returns all quiz attempts for the authenticated user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserQuizAttempt>>> getCurrentUserQuizAttempts() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserQuizAttempt> attempts = userQuizAttemptService.getQuizAttemptsByUserId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(attempts));
    }

    @Operation(summary = "Get quiz attempt by ID", description = "Returns a specific quiz attempt")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserQuizAttempt>> getQuizAttemptById(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserQuizAttempt attempt = userQuizAttemptService.getQuizAttemptById(id);

        // Check if the attempt belongs to the current user
        if (!attempt.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<UserQuizAttempt>error(403, "You do not have permission to access this quiz attempt"));
        }

        return ResponseEntity.ok(ApiResponse.success(attempt));
    }

    @Operation(summary = "Create a new quiz attempt", description = "Submits a quiz attempt with answers")
    @PostMapping("/quiz/{quizId}")
    public ResponseEntity<ApiResponse<UserQuizAttempt>> createQuizAttempt(
            @PathVariable Long quizId,
            @RequestBody Map<Long, Long> answers
    ) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            UserQuizAttempt attempt = userQuizAttemptService.createQuizAttempt(
                    currentUser.getId(), quizId, answers);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(attempt, "Quiz attempt submitted successfully"));
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.<UserQuizAttempt>error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Get answers for a quiz attempt", description = "Returns all answers for a specific quiz attempt")
    @GetMapping("/{id}/answers")
    public ResponseEntity<ApiResponse<List<UserAnswer>>> getQuizAttemptAnswers(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserQuizAttempt attempt = userQuizAttemptService.getQuizAttemptById(id);

        // Check if the attempt belongs to the current user
        if (!attempt.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<List<UserAnswer>>error(403, "You do not have permission to access this quiz attempt"));
        }

        List<UserAnswer> answers = userQuizAttemptService.getUserAnswersForAttempt(id);
        return ResponseEntity.ok(ApiResponse.success(answers));
    }

    @Operation(summary = "Get quiz attempts for a specific quiz", description = "Returns all attempts by the current user for a specific quiz")
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<ApiResponse<List<UserQuizAttempt>>> getQuizAttemptsByQuizId(@PathVariable Long quizId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserQuizAttempt> attempts = userQuizAttemptService.getQuizAttemptsByUserIdAndQuizId(
                currentUser.getId(), quizId);

        return ResponseEntity.ok(ApiResponse.success(attempts));
    }

    @Operation(summary = "Get latest quiz attempt for a specific quiz", description = "Returns the most recent attempt by the current user for a specific quiz")
    @GetMapping("/quiz/{quizId}/latest")
    public ResponseEntity<ApiResponse<UserQuizAttempt>> getLatestQuizAttemptForQuiz(@PathVariable Long quizId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserQuizAttempt attempt = userQuizAttemptService.getLatestQuizAttemptByUserIdAndQuizId(
                currentUser.getId(), quizId);

        if (attempt == null) {
            return ResponseEntity.ok(
                    ApiResponse.<UserQuizAttempt>builder()
                            .timestamp(LocalDateTime.now())
                            .status(200)
                            .message("No attempts found for this quiz")
                            .build()
            );
        }

        return ResponseEntity.ok(ApiResponse.success(attempt));
    }

    @Operation(summary = "Check if user has passed a quiz", description = "Checks if the current user has passed a specific quiz")
    @GetMapping("/quiz/{quizId}/passed")
    public ResponseEntity<ApiResponse<Boolean>> hasUserPassedQuiz(@PathVariable Long quizId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean passed = userQuizAttemptService.hasUserPassedQuiz(currentUser.getId(), quizId);

        return ResponseEntity.ok(ApiResponse.success(passed));
    }

    @Operation(summary = "Get user quiz statistics", description = "Returns statistics about the current user's quiz attempts")
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserQuizStatistics() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> statistics = userQuizAttemptService.getQuizAttemptStatisticsForUser(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @Operation(summary = "Get paginated quiz attempts", description = "Returns a paginated list of quiz attempts for the current user")
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<UserQuizAttempt>>> getPaginatedQuizAttempts(Pageable pageable) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Page<UserQuizAttempt> attempts = userQuizAttemptService.getPaginatedQuizAttemptsByUserId(
                currentUser.getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(attempts));
    }

    @Operation(summary = "Delete a quiz attempt", description = "Deletes a specific quiz attempt by the current user")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuizAttempt(@PathVariable Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserQuizAttempt attempt = userQuizAttemptService.getQuizAttemptById(id);

        // Check if the attempt belongs to the current user
        if (!attempt.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.<Void>error(403, "You do not have permission to delete this quiz attempt"));
        }

        boolean deleted = userQuizAttemptService.deleteQuizAttempt(id);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>error(500, "Failed to delete quiz attempt"));
        }

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .timestamp(LocalDateTime.now())
                        .status(200)
                        .message("Quiz attempt deleted successfully")
                        .build()
        );
    }
}