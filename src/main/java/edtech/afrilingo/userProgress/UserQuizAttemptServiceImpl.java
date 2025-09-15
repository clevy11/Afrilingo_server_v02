package edtech.afrilingo.userProgress;

import edtech.afrilingo.exception.ResourceNotFoundException;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.question.QuestionRepository;
import edtech.afrilingo.quiz.Quiz;
import edtech.afrilingo.quiz.QuizRepository;
import edtech.afrilingo.quiz.option.Option;
import edtech.afrilingo.quiz.option.OptionRepository;
import edtech.afrilingo.user.User;
import edtech.afrilingo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuizAttemptServiceImpl implements UserQuizAttemptService {

    private final UserQuizAttemptRepository userQuizAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    @Override
    public List<UserQuizAttempt> getQuizAttemptsByUserId(Long userId) {
        return userQuizAttemptRepository.findByUserId(userId);
    }

    @Override
    public List<UserQuizAttempt> getQuizAttemptsByUserIdAndQuizId(Long userId, Long quizId) {
        return userQuizAttemptRepository.findByUserIdAndQuizId(userId, quizId);
    }

    @Override
    public UserQuizAttempt getLatestQuizAttemptByUserIdAndQuizId(Long userId, Long quizId) {
        List<UserQuizAttempt> attempts = userQuizAttemptRepository.findLatestByUserIdAndQuizId(userId, quizId);
        return attempts.isEmpty() ? null : attempts.get(0);
    }

    @Override
    @Transactional
    public UserQuizAttempt createQuizAttempt(Long userId, Long quizId, Map<Long, Long> answers) {
        // Validate that user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Validate that quiz exists
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        // Create quiz attempt
        UserQuizAttempt attempt = UserQuizAttempt.builder()
                .user(user)
                .quiz(quiz)
                .attemptedAt(LocalDateTime.now())
                .build();

        // Save the attempt to get the ID
        UserQuizAttempt savedAttempt = userQuizAttemptRepository.save(attempt);

        // Create user answers
        List<UserAnswer> userAnswers = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : answers.entrySet()) {
            Long questionId = entry.getKey();
            Long optionId = entry.getValue();

            // Validate that question exists and belongs to the quiz
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

            if (!question.getQuiz().getId().equals(quizId)) {
                throw new IllegalArgumentException("Question " + questionId + " does not belong to quiz " + quizId);
            }

            // Validate that option exists and belongs to the question
            Option option = optionRepository.findById(optionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Option", "id", optionId));

            if (!option.getQuestion().getId().equals(questionId)) {
                throw new IllegalArgumentException("Option " + optionId + " does not belong to question " + questionId);
            }

            // Create user answer
            UserAnswer userAnswer = UserAnswer.builder()
                    .attempt(savedAttempt)
                    .question(question)
                    .option(option)
                    .isCorrect(option.isCorrect()) // Mark as correct if the selected option is correct
                    .build();

            userAnswers.add(userAnswer);
        }

        // Save all user answers
        userAnswerRepository.saveAll(userAnswers);

        // Evaluate the attempt
        return evaluateQuizAttempt(savedAttempt.getId());
    }

    @Override
    @Transactional
    public UserQuizAttempt evaluateQuizAttempt(Long attemptId) {
        UserQuizAttempt attempt = userQuizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("UserQuizAttempt", "id", attemptId));

        // Get all answers for this attempt
        List<UserAnswer> answers = userAnswerRepository.findByAttemptId(attemptId);

        if (answers.isEmpty()) {
            attempt.setScore(0);
            attempt.setPassed(false);
            return userQuizAttemptRepository.save(attempt);
        }

        // Get the quiz
        Quiz quiz = attempt.getQuiz();

        // Get all questions for this quiz
        List<Question> questions = questionRepository.findByQuizId(quiz.getId());

        if (questions.isEmpty()) {
            attempt.setScore(0);
            attempt.setPassed(false);
            return userQuizAttemptRepository.save(attempt);
        }

        // Calculate total possible points
        int totalPossiblePoints = questions.stream()
                .mapToInt(Question::getPoints)
                .sum();

        if (totalPossiblePoints == 0) {
            attempt.setScore(0);
            attempt.setPassed(false);
            return userQuizAttemptRepository.save(attempt);
        }

        // Calculate score by summing points for correctly answered questions
        Map<Long, Boolean> questionCorrectMap = answers.stream()
                .collect(Collectors.toMap(
                        answer -> answer.getQuestion().getId(),
                        UserAnswer::isCorrect,
                        (existing, replacement) -> existing && replacement // For questions with multiple correct answers
                ));

        int earnedPoints = 0;
        for (Question question : questions) {
            Boolean isCorrect = questionCorrectMap.get(question.getId());
            if (isCorrect != null && isCorrect) {
                earnedPoints += question.getPoints();
            }
        }

        // Calculate score as percentage
        int score = (int) Math.round((double) earnedPoints / totalPossiblePoints * 100);

        // Check if passed
        boolean passed = score >= quiz.getMinPassingScore();

        // Update attempt
        attempt.setScore(score);
        attempt.setPassed(passed);

        return userQuizAttemptRepository.save(attempt);
    }

    @Override
    public List<UserAnswer> getUserAnswersForAttempt(Long attemptId) {
        if (!userQuizAttemptRepository.existsById(attemptId)) {
            throw new ResourceNotFoundException("UserQuizAttempt", "id", attemptId);
        }

        return userAnswerRepository.findByAttemptId(attemptId);
    }

    @Override
    public List<UserQuizAttempt> getPassedQuizAttemptsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return userQuizAttemptRepository.findByUserIdAndPassedTrue(userId);
    }

    @Override
    public double calculateAverageScoreByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Double averageScore = userQuizAttemptRepository.calculateAverageScoreByUserId(userId);
        return averageScore != null ? averageScore : 0.0;
    }

    @Override
    public double calculatePassRateByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        List<UserQuizAttempt> attempts = userQuizAttemptRepository.findByUserId(userId);
        if (attempts.isEmpty()) {
            return 0.0;
        }

        long passedCount = attempts.stream().filter(UserQuizAttempt::isPassed).count();
        return (double) passedCount / attempts.size() * 100.0;
    }

    @Override
    public UserQuizAttempt getQuizAttemptById(Long attemptId) {
        return userQuizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("UserQuizAttempt", "id", attemptId));
    }

    @Override
    public boolean hasUserPassedQuiz(Long userId, Long quizId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz", "id", quizId);
        }

        List<UserQuizAttempt> attempts = userQuizAttemptRepository.findByUserIdAndQuizId(userId, quizId);
        return attempts.stream().anyMatch(UserQuizAttempt::isPassed);
    }

    @Override
    public Page<UserQuizAttempt> getPaginatedQuizAttemptsByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return userQuizAttemptRepository.findByUserId(userId, pageable);
    }

    @Override
    public Map<String, Object> getQuizAttemptStatisticsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        Map<String, Object> statistics = new HashMap<>();

        // Get all attempts for this user
        List<UserQuizAttempt> attempts = userQuizAttemptRepository.findByUserId(userId);

        if (attempts.isEmpty()) {
            statistics.put("totalAttempts", 0);
            statistics.put("passedAttempts", 0);
            statistics.put("failedAttempts", 0);
            statistics.put("averageScore", 0.0);
            statistics.put("passRate", 0.0);
            statistics.put("totalQuizzesAttempted", 0);
            statistics.put("totalQuizzesPassed", 0);
            return statistics;
        }

        // Calculate statistics
        int totalAttempts = attempts.size();
        long passedAttempts = attempts.stream().filter(UserQuizAttempt::isPassed).count();
        long failedAttempts = totalAttempts - passedAttempts;

        double averageScore = attempts.stream()
                .mapToInt(UserQuizAttempt::getScore)
                .average()
                .orElse(0.0);

        double passRate = (double) passedAttempts / totalAttempts * 100.0;

        // Count unique quizzes attempted
        Set<Long> quizzesAttempted = attempts.stream()
                .map(attempt -> attempt.getQuiz().getId())
                .collect(Collectors.toSet());

        // Count unique quizzes passed
        Set<Long> quizzesPassed = attempts.stream()
                .filter(UserQuizAttempt::isPassed)
                .map(attempt -> attempt.getQuiz().getId())
                .collect(Collectors.toSet());

        statistics.put("totalAttempts", totalAttempts);
        statistics.put("passedAttempts", passedAttempts);
        statistics.put("failedAttempts", failedAttempts);
        statistics.put("averageScore", averageScore);
        statistics.put("passRate", passRate);
        statistics.put("totalQuizzesAttempted", quizzesAttempted.size());
        statistics.put("totalQuizzesPassed", quizzesPassed.size());

        return statistics;
    }

    @Override
    @Transactional
    public boolean deleteQuizAttempt(Long attemptId) {
        return userQuizAttemptRepository.findById(attemptId)
                .map(attempt -> {
                    // Delete all associated user answers first
                    userAnswerRepository.deleteByAttemptId(attemptId);

                    // Then delete the attempt
                    userQuizAttemptRepository.delete(attempt);
                    return true;
                })
                .orElse(false);
    }
}