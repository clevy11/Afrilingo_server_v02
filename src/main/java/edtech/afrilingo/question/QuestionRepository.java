package edtech.afrilingo.question;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Find questions by quiz ID
     * @param quizId Quiz ID
     * @return List of questions
     */
    @EntityGraph(attributePaths = {"options"})
    List<Question> findByQuizId(Long quizId);

    /**
     * Find questions by quiz ID and question type
     * @param quizId Quiz ID
     * @param questionType Question type
     * @return List of questions
     */
    @EntityGraph(attributePaths = {"options"})
    List<Question> findByQuizIdAndQuestionType(Long quizId, QuestionType questionType);

    /**
     * Find questions by media URL containing keyword
     * @param keyword Keyword to search for
     * @return List of questions
     */
    List<Question> findByMediaUrlContaining(String keyword);

    /**
     * Find questions by quiz ID ordered by points (descending)
     * @param quizId Quiz ID
     * @return List of questions ordered by points
     */
    List<Question> findByQuizIdOrderByPointsDesc(Long quizId);

    /**
     * Count questions by quiz ID
     * @param quizId Quiz ID
     * @return Count of questions
     */
    int countByQuizId(Long quizId);

    /**
     * Calculate total points for a quiz
     * @param quizId Quiz ID
     * @return Total points
     */
    @Query("SELECT SUM(q.points) FROM Question q WHERE q.quiz.id = :quizId")
    Integer calculateTotalPoints(@Param("quizId") Long quizId);

    /**
     * Find questions containing a keyword in questionText
     * @param keyword Keyword to search for
     * @return List of questions
     */
    @Query("SELECT q FROM Question q WHERE q.questionText LIKE CONCAT('%', :keyword, '%')")
    List<Question> findByQuestionTextContaining(@Param("keyword") String keyword);

    @Query("SELECT q FROM Question q " +
           "WHERE q.certificationQuestion = true " +
           "AND q.id NOT IN :excludedIds " +
           "ORDER BY RANDOM()")
    List<Question> findRandomCertificationQuestionsByLanguage(
            @Param("languageCode") String languageCode, 
            @Param("testLevel") String testLevel, 
            @Param("excludedIds") List<Long> excludedIds, 
            Pageable pageable);

    @Query("SELECT q FROM Question q " +
           "WHERE q.certificationQuestion = true " +
           "AND q.questionType = :type " +
           "ORDER BY RANDOM()")
    List<Question> findRandomCertificationQuestionsByTypeAndLanguage(
            @Param("languageCode") String languageCode, 
            @Param("testLevel") String testLevel, 
            @Param("type") QuestionType type, 
            Pageable pageable);

    /**
     * DEBUG: Find all questions, ignoring all filters.
     */
    @Query("SELECT q FROM Question q")
    List<Question> findAllQuestionsForDebug(Pageable pageable);

    // ==================== CORRECTED CERTIFICATION METHODS ====================

    /**
     * Find certification questions by test level
     * Since your Question model doesn't have direct language relationship,
     * we'll filter by certification fields only
     */
    @Query("SELECT q FROM Question q " +
            "WHERE q.certificationQuestion = true " +
            "AND (q.certificationLevel = :testLevel OR q.certificationLevel IS NULL) " +
            "ORDER BY RANDOM()")
    List<Question> findCertificationQuestionsByLevel(
            @Param("testLevel") String testLevel,
            Pageable pageable);

    /**
     * Find all certification questions regardless of level
     */
    @Query("SELECT q FROM Question q " +
            "WHERE q.certificationQuestion = true " +
            "ORDER BY RANDOM()")
    List<Question> findAllCertificationQuestions(Pageable pageable);

    /**
     * Find certification questions by quiz IDs and level
     * This is useful if you organize quizzes by language
     */
    @Query("SELECT q FROM Question q " +
            "WHERE q.quiz.id IN :quizIds " +
            "AND q.certificationQuestion = true " +
            "AND (q.certificationLevel = :testLevel OR q.certificationLevel IS NULL) " +
            "ORDER BY RANDOM()")
    List<Question> findCertificationQuestionsByQuizIdsAndLevel(
            @Param("quizIds") List<Long> quizIds,
            @Param("testLevel") String testLevel,
            Pageable pageable);

    /**
     * Find questions by quiz IDs (useful for language-specific quizzes)
     */
    @Query("SELECT q FROM Question q " +
            "WHERE q.quiz.id IN :quizIds " +
            "ORDER BY RANDOM()")
    List<Question> findByQuizIds(@Param("quizIds") List<Long> quizIds, Pageable pageable);

    /**
     * Get random questions as fallback
     */
    @Query("SELECT q FROM Question q ORDER BY RANDOM()")
    List<Question> findRandomQuestions(Pageable pageable);

    /**
     * Find questions by certification level only
     */
    @Query("SELECT q FROM Question q " +
            "WHERE q.certificationLevel = :testLevel " +
            "ORDER BY RANDOM()")
    List<Question> findByCertificationLevel(@Param("testLevel") String testLevel, Pageable pageable);

    /**
     * Find questions by specific quiz titles (if you name quizzes by language)
     * This assumes your Quiz entity has a title field
     */
    @Query("SELECT q FROM Question q " +
            "WHERE q.quiz.title LIKE CONCAT('%', :languageKeyword, '%') " +
            "AND (q.certificationQuestion = true OR q.certificationQuestion IS NULL) " +
            "AND (q.certificationLevel = :testLevel OR q.certificationLevel IS NULL) " +
            "ORDER BY RANDOM()")
    List<Question> findQuestionsByQuizTitleContainingAndLevel(
            @Param("languageKeyword") String languageKeyword,
            @Param("testLevel") String testLevel,
            Pageable pageable);

    /**
     * Count certification questions by level
     */
    @Query("SELECT COUNT(q) FROM Question q " +
            "WHERE q.certificationQuestion = true " +
            "AND (q.certificationLevel = :testLevel OR q.certificationLevel IS NULL)")
    Long countCertificationQuestionsByLevel(@Param("testLevel") String testLevel);

    /**
     * Count all certification questions
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.certificationQuestion = true")
    Long countAllCertificationQuestions();
}