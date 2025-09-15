package edtech.afrilingo.quiz;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @EntityGraph(attributePaths = {"lesson"})
    List<Quiz> findAllBy();
    /**
     * Find quizzes by lesson ID
     * @param lessonId Lesson ID
     * @return List of quizzes
     */
    @EntityGraph(attributePaths = {"lesson"})
    List<Quiz> findByLessonId(Long lessonId);

    /**
     * Find quizzes by minimum passing score
     * @param minPassingScore Minimum passing score
     * @return List of quizzes
     */
    List<Quiz> findByMinPassingScore(int minPassingScore);

    /**
     * Find quizzes by lesson ID and order by title
     * @param lessonId Lesson ID
     * @return List of quizzes ordered by title
     */
    @EntityGraph(attributePaths = {"lesson"})
    List<Quiz> findByLessonIdOrderByTitle(Long lessonId);

    /**
     * Count quizzes by lesson ID
     * @param lessonId Lesson ID
     * @return Count of quizzes
     */
    int countByLessonId(Long lessonId);

    /**
     * Calculate average score for a quiz from user attempts
     * @param quizId Quiz ID
     * @return Average score or null if no attempts
     */
    @Query("SELECT AVG(uqa.score) FROM UserQuizAttempt uqa WHERE uqa.quiz.id = :quizId")
    Double calculateAverageScore(@Param("quizId") Long quizId);

    /**
     * Calculate pass rate for a quiz from user attempts
     * @param quizId Quiz ID
     * @return Pass rate ratio (0-1)
     */
    @EntityGraph(attributePaths = {"lesson"})
    @Query("SELECT CASE WHEN COUNT(uqa2) > 0 THEN " +
            "(SELECT COUNT(uqa) FROM UserQuizAttempt uqa WHERE uqa.quiz.id = :quizId AND uqa.passed = true) / " +
            "CAST(COUNT(uqa2) AS float) ELSE 0 END " +
            "FROM UserQuizAttempt uqa2 WHERE uqa2.quiz.id = :quizId")
    Double calculatePassRate(@Param("quizId") Long quizId);
}