package edtech.afrilingo.question;

import edtech.afrilingo.quiz.Quiz;
import edtech.afrilingo.quiz.QuizRepository;
import edtech.afrilingo.quiz.option.Option;
import edtech.afrilingo.quiz.option.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final OptionRepository optionRepository;

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    @Override
    public List<Question> getQuestionsByQuizIdAndType(Long quizId, QuestionType questionType) {
        return questionRepository.findByQuizIdAndQuestionType(quizId, questionType);
    }

    @Override
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    @Transactional
    public Question createQuestion(Question question) {
        // Validate question data
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            throw new IllegalArgumentException("Question text is required");
        }

        if (question.getQuestionType() == null) {
            throw new IllegalArgumentException("Question type is required");
        }

        if (question.getQuiz() == null || question.getQuiz().getId() == null) {
            throw new IllegalArgumentException("Question must be associated with a quiz");
        }

        // Verify quiz exists
        if (!quizRepository.existsById(question.getQuiz().getId())) {
            throw new IllegalArgumentException("Quiz with id " + question.getQuiz().getId() + " not found");
        }

        // Set points if not specified
        if (question.getPoints() <= 0) {
            question.setPoints(1); // Default 1 point per question
        }

        // Validate options based on question type
        validateOptionsForQuestionType(question);

        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question updateQuestion(Long id, Question questionDetails) {
        return questionRepository.findById(id)
                .map(existingQuestion -> {
                    // Update fields
                    if (questionDetails.getQuestionText() != null) {
                        existingQuestion.setQuestionText(questionDetails.getQuestionText());
                    }

                    if (questionDetails.getQuestionType() != null) {
                        existingQuestion.setQuestionType(questionDetails.getQuestionType());
                        // Re-validate options if question type changed
                        validateOptionsForQuestionType(existingQuestion);
                    }

                    if (questionDetails.getMediaUrl() != null) {
                        existingQuestion.setMediaUrl(questionDetails.getMediaUrl());
                    }

                    if (questionDetails.getPoints() > 0) {
                        existingQuestion.setPoints(questionDetails.getPoints());
                    }

                    if (questionDetails.getQuiz() != null && questionDetails.getQuiz().getId() != null) {
                        // Verify quiz exists
                        if (!quizRepository.existsById(questionDetails.getQuiz().getId())) {
                            throw new IllegalArgumentException("Quiz with id " +
                                    questionDetails.getQuiz().getId() + " not found");
                        }

                        // Get the quiz from the repository to ensure we have the full entity
                        Quiz quiz = quizRepository.findById(questionDetails.getQuiz().getId())
                                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

                        existingQuestion.setQuiz(quiz);
                    }

                    // Update certification fields
                    if (questionDetails.getCertificationQuestion() != null) {
                        existingQuestion.setCertificationQuestion(questionDetails.getCertificationQuestion());
                    }

                    if (questionDetails.getCertificationLevel() != null) {
                        existingQuestion.setCertificationLevel(questionDetails.getCertificationLevel());
                    }

                    return questionRepository.save(existingQuestion);
                })
                .orElseThrow(() -> new RuntimeException("Question not found with id " + id));
    }

    @Override
    @Transactional
    public boolean deleteQuestion(Long id) {
        return questionRepository.findById(id)
                .map(question -> {
                    // First delete all options associated with this question
                    optionRepository.deleteByQuestionId(id);

                    // Then delete the question
                    questionRepository.delete(question);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean existsById(Long id) {
        return questionRepository.existsById(id);
    }

    @Override
    public Page<Question> getQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Question addOptionToQuestion(Long questionId, Option option) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id " + questionId));

        // Set the question reference in the option
        option.setQuestion(question);

        // Save the option
        Option savedOption = optionRepository.save(option);

        // Add the option to the question's options list if it's not null
        if (question.getOptions() == null) {
            question.setOptions(new ArrayList<>());
        }
        question.getOptions().add(savedOption);

        // Validate options based on question type
        validateOptionsForQuestionType(question);

        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question removeOptionFromQuestion(Long questionId, Long optionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id " + questionId));

        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found with id " + optionId));

        // Check if the option belongs to this question
        if (!option.getQuestion().getId().equals(questionId)) {
            throw new IllegalArgumentException("Option does not belong to this question");
        }

        // Remove the option from the question's options list
        if (question.getOptions() != null) {
            question.getOptions().removeIf(o -> o.getId().equals(optionId));
        }

        // Delete the option
        optionRepository.delete(option);

        // Validate remaining options based on question type
        validateOptionsForQuestionType(question);

        return questionRepository.save(question);
    }

    @Override
    public List<Option> getOptionsForQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new RuntimeException("Question not found with id " + questionId);
        }

        return optionRepository.findByQuestionIdOrderById(questionId);
    }

    @Override
    public List<Option> getCorrectOptionsForQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new RuntimeException("Question not found with id " + questionId);
        }

        return optionRepository.findByQuestionIdAndIsCorrectTrue(questionId);
    }

    @Override
    public int calculateTotalPoints(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new RuntimeException("Quiz not found with id " + quizId);
        }

        Integer totalPoints = questionRepository.calculateTotalPoints(quizId);
        return totalPoints != null ? totalPoints : 0;
    }

    @Override
    public List<Question> searchQuestionsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        return questionRepository.findByQuestionTextContaining(keyword);
    }

    @Override
    public List<Question> getCertificationQuestions(String languageCode, String testLevel, int count) {
        log.info("Fetching {} certification questions for language '{}' and level '{}'", count, languageCode, testLevel);

        // DEBUG: Fetch all questions to check if data exists
        log.warn("DEBUG MODE: Fetching all questions, ignoring all filters.");
        List<Question> allQuestions;
        try {
            allQuestions = questionRepository.findAllQuestionsForDebug(PageRequest.of(0, count));
            log.info("DEBUG: Found {} questions in total.", allQuestions.size());
        } catch (Exception e) {
            log.error("DEBUG: Error fetching all questions: {}", e.getMessage(), e);
            return Collections.emptyList();
        }

        if (allQuestions.isEmpty()) {
            log.error("No questions found in the database at all. Please verify the 'questions' table is populated.");
        }

        // Shuffle the final list of questions
        Collections.shuffle(allQuestions);

        log.info("Successfully prepared {} questions for the certification test.", allQuestions.size());
        return allQuestions.stream().limit(count).collect(Collectors.toList());
    }

    // ==================== UPDATED CERTIFICATION METHODS ====================

    /**
     * Safely fetch questions by type with error handling and fallback
     */
    private List<Question> safeFetchQuestionsByType(String languageCode, String testLevel, QuestionType type, int count) {
        try {
            List<Question> questions = fetchRandomQuestionsByType(languageCode, testLevel, type, count);
            return questions != null ? questions : new ArrayList<>();
        } catch (Exception e) {
            log.warn("Error fetching {} questions: {}", type, e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Question> fetchRandomQuestionsByType(String languageCode, String testLevel, QuestionType type, int count) {
        return questionRepository.findRandomCertificationQuestionsByTypeAndLanguage(
                languageCode, testLevel, type, PageRequest.of(0, count));
    }

// ... (rest of the code remains the same)
    /**
     * Check if answer is correct
     */
    public boolean isAnswerCorrect(Long questionId, Long selectedOptionId) {
        try {
            Optional<Question> questionOpt = questionRepository.findById(questionId);
            if (questionOpt.isEmpty()) {
                log.warn("Question not found: {}", questionId);
                return false;
            }

            Optional<Option> selectedOption = optionRepository.findById(selectedOptionId);
            if (selectedOption.isEmpty()) {
                log.warn("Option not found: {}", selectedOptionId);
                return false;
            }

            boolean isCorrect = selectedOption.get().isCorrect();
            log.debug("Question {} option {} is correct: {}", questionId, selectedOptionId, isCorrect);

            return isCorrect;

        } catch (Exception e) {
            log.error("Error checking answer correctness: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get correct answer for a question
     */
    public Optional<Option> getCorrectAnswer(Long questionId) {
        try {
            return optionRepository.findByQuestionIdAndCorrectTrue(questionId);
        } catch (Exception e) {
            log.error("Error getting correct answer for question {}: {}", questionId, e.getMessage());
            return Optional.empty();
        }
    }



    /**
     * Helper method to validate options based on question type
     */
    private void validateOptionsForQuestionType(Question question) {
        // Skip validation if options are not yet set
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            return;
        }

        int correctOptionsCount = 0;
        for (Option option : question.getOptions()) {
            if (option.isCorrect()) {
                correctOptionsCount++;
            }
        }

        switch (question.getQuestionType()) {
            case MULTIPLE_CHOICE:
                // Multiple choice should have at least 2 options and at least 1 correct option
                if (question.getOptions().size() < 2) {
                    throw new IllegalArgumentException("Multiple choice questions must have at least 2 options");
                }
                if (correctOptionsCount == 0) {
                    throw new IllegalArgumentException("Multiple choice questions must have at least 1 correct option");
                }
                break;

            case TRUE_FALSE:
                // True/False should have exactly 2 options and exactly 1 correct option
                if (question.getOptions().size() != 2) {
                    throw new IllegalArgumentException("True/False questions must have exactly 2 options");
                }
                if (correctOptionsCount != 1) {
                    throw new IllegalArgumentException("True/False questions must have exactly 1 correct option");
                }
                break;

            case FILL_BLANK:
            case OPEN_ENDED:
                // No specific validation for options, as the answer is usually in the text
                break;
        }
    }

    // ==================== NEW CERTIFICATION HELPER METHODS ====================

    /**
     * Get questions from specific quiz IDs (useful for language-specific quizzes)
     */
    public List<Question> getQuestionsFromSpecificQuizzes(List<Long> quizIds, String testLevel, int count) {
        try {
            return questionRepository.findCertificationQuestionsByQuizIdsAndLevel(
                    quizIds, testLevel, PageRequest.of(0, count));
        } catch (Exception e) {
            log.error("Error getting questions from specific quizzes: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Count available certification questions by level
     */
    public long countCertificationQuestionsByLevel(String testLevel) {
        try {
            return questionRepository.countCertificationQuestionsByLevel(testLevel);
        } catch (Exception e) {
            log.error("Error counting certification questions: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Count all certification questions
     */
    public long countAllCertificationQuestions() {
        try {
            return questionRepository.countAllCertificationQuestions();
        } catch (Exception e) {
            log.error("Error counting all certification questions: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Mark questions as certification questions (admin function)
     */
    @Transactional
    public void markQuestionsAsCertification(List<Long> questionIds, String certificationLevel) {
        try {
            for (Long questionId : questionIds) {
                Optional<Question> questionOpt = questionRepository.findById(questionId);
                if (questionOpt.isPresent()) {
                    Question question = questionOpt.get();
                    question.setCertificationQuestion(true);
                    question.setCertificationLevel(certificationLevel);
                    questionRepository.save(question);
                }
            }
            log.info("Marked {} questions as certification level {}", questionIds.size(), certificationLevel);
        } catch (Exception e) {
            log.error("Error marking questions as certification: {}", e.getMessage());
            throw new RuntimeException("Failed to mark questions as certification", e);
        }
    }
}