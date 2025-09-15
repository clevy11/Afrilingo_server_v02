package edtech.afrilingo.question;

import edtech.afrilingo.quiz.option.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    /**
     * Get all questions
     * @return List of all questions
     */
    List<Question> getAllQuestions();

    /**
     * Get questions by quiz ID
     * @param quizId Quiz ID
     * @return List of questions for the given quiz
     */
    List<Question> getQuestionsByQuizId(Long quizId);

    /**
     * Get questions by quiz ID and question type
     * @param quizId Quiz ID
     * @param questionType Question type
     * @return List of questions of the given type for the given quiz
     */
    List<Question> getQuestionsByQuizIdAndType(Long quizId, QuestionType questionType);

    /**
     * Get question by ID
     * @param id Question ID
     * @return Optional containing the question if found
     */
    Optional<Question> getQuestionById(Long id);

    /**
     * Create a new question
     * @param question Question to create
     * @return Created question with generated ID
     */
    Question createQuestion(Question question);

    /**
     * Update an existing question
     * @param id Question ID
     * @param questionDetails Updated question details
     * @return Updated question
     */
    Question updateQuestion(Long id, Question questionDetails);

    /**
     * Delete a question
     * @param id Question ID
     * @return true if deleted successfully
     */
    boolean deleteQuestion(Long id);

    /**
     * Check if question exists by ID
     * @param id Question ID
     * @return true if question exists
     */
    boolean existsById(Long id);

    /**
     * Get paginated questions
     * @param pageable Pagination information
     * @return Page of questions
     */
    Page<Question> getQuestions(Pageable pageable);

    /**
     * Add an option to a question
     * @param questionId Question ID
     * @param option Option to add
     * @return Updated question with the new option
     */
    Question addOptionToQuestion(Long questionId, Option option);

    /**
     * Remove an option from a question
     * @param questionId Question ID
     * @param optionId Option ID
     * @return Updated question without the removed option
     */
    Question removeOptionFromQuestion(Long questionId, Long optionId);

    /**
     * Get options for a question
     * @param questionId Question ID
     * @return List of options for the given question
     */
    List<Option> getOptionsForQuestion(Long questionId);

    /**
     * Get correct options for a question
     * @param questionId Question ID
     * @return List of correct options for the given question
     */
    List<Option> getCorrectOptionsForQuestion(Long questionId);

    /**
     * Calculate total points for a quiz
     * @param quizId Quiz ID
     * @return Total points
     */
    int calculateTotalPoints(Long quizId);

    /**
     * Search questions by keyword in question text
     * @param keyword Keyword to search for
     * @return List of matching questions
     */
    List<Question> searchQuestionsByKeyword(String keyword);
    List<Question> getCertificationQuestions(String languageCode, String testLevel, int count);
    public boolean isAnswerCorrect(Long questionId, Long selectedOptionId);
    public Optional<Option> getCorrectAnswer(Long questionId);
}