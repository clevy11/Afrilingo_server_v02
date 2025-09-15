package edtech.afrilingo.userProgress;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserQuizAttemptRepository extends JpaRepository<UserQuizAttempt, Long> {

    /**
     * Find quiz attempts by user ID
     * @param userId User ID
     * @return List of quiz attempts
     */
    List<UserQuizAttempt> findByUserId(Long userId);

    /**
     * Find paginated quiz attempts by user ID
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of quiz attempts
     */
    Page<UserQuizAttempt> findByUserId(Long userId, Pageable pageable);

    /**
     * Find quiz attempts by user ID and quiz ID
     * @param userId User ID
     * @param quizId Quiz ID
     * @return List of quiz attempts
     */
    List<UserQuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId);

    /**
     * Find latest quiz attempt by user ID and quiz ID
     * @param userId User ID
     * @param quizId Quiz ID
     * @return Latest quiz attempt
     */
    @Query("SELECT uqa FROM UserQuizAttempt uqa WHERE uqa.user.id = :userId AND uqa.quiz.id = :quizId ORDER BY uqa.attemptedAt DESC")
    List<UserQuizAttempt> findLatestByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    /**
     * Find passed quiz attempts by user ID
     * @param userId User ID
     * @return List of passed quiz attempts
     */
    List<UserQuizAttempt> findByUserIdAndPassedTrue(Long userId);

    /**
     * Find quiz attempts by user ID and attempt date after a specific time
     * @param userId User ID
     * @param date Date to compare with
     * @return List of quiz attempts after the given date
     */
    List<UserQuizAttempt> findByUserIdAndAttemptedAtAfter(Long userId, LocalDateTime date);

    /**
     * Count passed quiz attempts by user ID
     * @param userId User ID
     * @return Count of passed quiz attempts
     */
    int countByUserIdAndPassedTrue(Long userId);

    /**
     * Calculate average score for a user
     * @param userId User ID
     * @return Average score
     */
    @Query("SELECT AVG(uqa.score) FROM UserQuizAttempt uqa WHERE uqa.user.id = :userId")
    Double calculateAverageScoreByUserId(@Param("userId") Long userId);
}