package edtech.afrilingo.userProgress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    
    /**
     * Find answers by quiz attempt ID
     * @param attemptId Quiz attempt ID
     * @return List of user answers
     */
    List<UserAnswer> findByAttemptId(Long attemptId);
    
    /**
     * Find answers by question ID
     * @param questionId Question ID
     * @return List of user answers
     */
    List<UserAnswer> findByQuestionId(Long questionId);
    
    /**
     * Find answers by option ID
     * @param optionId Option ID
     * @return List of user answers
     */
    List<UserAnswer> findByOptionId(Long optionId);
    
    /**
     * Find correct answers by attempt ID
     * @param attemptId Quiz attempt ID
     * @return List of correct user answers
     */
    List<UserAnswer> findByAttemptIdAndIsCorrectTrue(Long attemptId);
    
    /**
     * Count correct answers by attempt ID
     * @param attemptId Quiz attempt ID
     * @return Count of correct answers
     */
    int countByAttemptIdAndIsCorrectTrue(Long attemptId);
    
    /**
     * Count answers by attempt ID
     * @param attemptId Quiz attempt ID
     * @return Count of answers
     */
    int countByAttemptId(Long attemptId);
    
    /**
     * Delete answers by attempt ID
     * @param attemptId Quiz attempt ID
     */
    @Modifying
    @Query("DELETE FROM UserAnswer ua WHERE ua.attempt.id = :attemptId")
    void deleteByAttemptId(@Param("attemptId") Long attemptId);
    
    /**
     * Find most selected option for a question
     * @param questionId Question ID
     * @return Option ID and selection count
     */
    @Query("SELECT ua.option.id, COUNT(ua) FROM UserAnswer ua WHERE ua.question.id = :questionId GROUP BY ua.option.id ORDER BY COUNT(ua) DESC")
    List<Object[]> findMostSelectedOptionForQuestion(@Param("questionId") Long questionId);
    
    /**
     * Calculate correct answer rate for a question
     * @param questionId Question ID
     * @return Correct answer rate (percentage)
     */
    @Query("SELECT (COUNT(ua) * 100.0 / (SELECT COUNT(ua2) FROM UserAnswer ua2 WHERE ua2.question.id = :questionId)) " +
           "FROM UserAnswer ua WHERE ua.question.id = :questionId AND ua.isCorrect = true")
    Double calculateCorrectAnswerRateForQuestion(@Param("questionId") Long questionId);
}