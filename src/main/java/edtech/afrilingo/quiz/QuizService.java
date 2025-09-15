package edtech.afrilingo.quiz;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuizService {
    
    /**
     * Get all quizzes
     * @return List of all quizzes
     */
    List<Quiz> getAllQuizzes();
    
    /**
     * Get quizzes by lesson ID
     * @param lessonId Lesson ID
     * @return List of quizzes for the given lesson
     */
    List<Quiz> getQuizzesByLessonId(Long lessonId);
    
    /**
     * Get quiz by ID
     * @param id Quiz ID
     * @return Optional containing the quiz if found
     */
    Optional<Quiz> getQuizById(Long id);
    
    /**
     * Create a new quiz
     * @param quiz Quiz to create
     * @return Created quiz with generated ID
     */
    Quiz createQuiz(Quiz quiz);
    
    /**
     * Update an existing quiz
     * @param id Quiz ID
     * @param quizDetails Updated quiz details
     * @return Updated quiz
     */
    Quiz updateQuiz(Long id, Quiz quizDetails);
    
    /**
     * Delete a quiz
     * @param id Quiz ID
     * @return true if deleted successfully
     */
    boolean deleteQuiz(Long id);
    
    /**
     * Check if quiz exists by ID
     * @param id Quiz ID
     * @return true if quiz exists
     */
    boolean existsById(Long id);
    
    /**
     * Get paginated quizzes
     * @param pageable Pagination information
     * @return Page of quizzes
     */
    Page<Quiz> getQuizzes(Pageable pageable);
    
    /**
     * Add a question to a quiz
     * @param quizId Quiz ID
     * @param question Question to add
     * @return Updated quiz with the new question
     */
    Quiz addQuestionToQuiz(Long quizId, edtech.afrilingo.question.Question question);
    
    /**
     * Remove a question from a quiz
     * @param quizId Quiz ID
     * @param questionId Question ID
     * @return Updated quiz without the removed question
     */
    Quiz removeQuestionFromQuiz(Long quizId, Long questionId);
    
    /**
     * Get the average score for a quiz
     * @param quizId Quiz ID
     * @return The average score or 0 if no attempts
     */
    double getAverageScore(Long quizId);
    
    /**
     * Get the pass rate for a quiz
     * @param quizId Quiz ID
     * @return The pass rate (percentage) or 0 if no attempts
     */
    double getPassRate(Long quizId);
}