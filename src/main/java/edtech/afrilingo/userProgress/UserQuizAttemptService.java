package edtech.afrilingo.userProgress;

import edtech.afrilingo.question.Question;
import edtech.afrilingo.quiz.option.Option;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQuizAttemptService {
    
    /**
     * Get all quiz attempts by user ID
     * @param userId User ID
     * @return List of quiz attempts
     */
    List<UserQuizAttempt> getQuizAttemptsByUserId(Long userId);
    
    /**
     * Get quiz attempts by user ID and quiz ID
     * @param userId User ID
     * @param quizId Quiz ID
     * @return List of quiz attempts
     */
    List<UserQuizAttempt> getQuizAttemptsByUserIdAndQuizId(Long userId, Long quizId);
    
    /**
     * Get latest quiz attempt by user ID and quiz ID
     * @param userId User ID
     * @param quizId Quiz ID
     * @return Latest quiz attempt or null if none exists
     */
    UserQuizAttempt getLatestQuizAttemptByUserIdAndQuizId(Long userId, Long quizId);
    
    /**
     * Create a new quiz attempt
     * @param userId User ID
     * @param quizId Quiz ID
     * @param answers Map of question IDs to selected option IDs
     * @return Created quiz attempt
     */
    UserQuizAttempt createQuizAttempt(Long userId, Long quizId, Map<Long, Long> answers);
    
    /**
     * Evaluate a quiz attempt
     * @param attemptId Quiz attempt ID
     * @return Evaluated quiz attempt
     */
    UserQuizAttempt evaluateQuizAttempt(Long attemptId);
    
    /**
     * Get all user answers for a quiz attempt
     * @param attemptId Quiz attempt ID
     * @return List of user answers
     */
    List<UserAnswer> getUserAnswersForAttempt(Long attemptId);
    
    /**
     * Get passed quiz attempts by user ID
     * @param userId User ID
     * @return List of passed quiz attempts
     */
    List<UserQuizAttempt> getPassedQuizAttemptsByUserId(Long userId);
    
    /**
     * Calculate average score for a user
     * @param userId User ID
     * @return Average score or 0 if no attempts
     */
    double calculateAverageScoreByUserId(Long userId);
    
    /**
     * Calculate pass rate for a user
     * @param userId User ID
     * @return Pass rate (percentage) or 0 if no attempts
     */
    double calculatePassRateByUserId(Long userId);
    
    /**
     * Get quiz attempt by ID
     * @param attemptId Quiz attempt ID
     * @return Quiz attempt if found
     */
    UserQuizAttempt getQuizAttemptById(Long attemptId);
    
    /**
     * Check if a user has passed a quiz
     * @param userId User ID
     * @param quizId Quiz ID
     * @return true if the user has passed the quiz
     */
    boolean hasUserPassedQuiz(Long userId, Long quizId);
    
    /**
     * Get paginated quiz attempts by user ID
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of quiz attempts
     */
    Page<UserQuizAttempt> getPaginatedQuizAttemptsByUserId(Long userId, Pageable pageable);
    
    /**
     * Get quiz attempt statistics for a user
     * @param userId User ID
     * @return Map of statistics
     */
    Map<String, Object> getQuizAttemptStatisticsForUser(Long userId);
    
    /**
     * Delete a quiz attempt
     * @param attemptId Quiz attempt ID
     * @return true if deleted successfully
     */
    boolean deleteQuizAttempt(Long attemptId);
}