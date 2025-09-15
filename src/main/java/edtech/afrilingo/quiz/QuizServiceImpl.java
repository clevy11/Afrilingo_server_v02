package edtech.afrilingo.quiz;

import edtech.afrilingo.lesson.Lesson;
import edtech.afrilingo.lesson.LessonService;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final LessonService lessonService;
    private final QuestionRepository questionRepository;
    public static void fun(){
        List<Integer> nums = new ArrayList<>();
        nums.stream().map(String::valueOf).collect(Collectors.joining(""));
//        nums.stream().map(String::valueOf).collect(Collectors.joining(""));
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        // Shallow fetch (prefetch lesson only) to avoid N+1 and heavy graphs
        return quizRepository.findAllBy();
    }

    @Override
    public List<Quiz> getQuizzesByLessonId(Long lessonId) {
        return quizRepository.findByLessonId(lessonId);
    }

    @Override
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    @Override
    @Transactional
    public Quiz createQuiz(Quiz quiz) {
        // Validate quiz data
        if (quiz.getTitle() == null || quiz.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Quiz title is required");
        }

        if (quiz.getLesson() == null || quiz.getLesson().getId() == null) {
            throw new IllegalArgumentException("Quiz must be associated with a lesson");
        }

        // Verify lesson exists
        if (!lessonService.existsById(quiz.getLesson().getId())) {
            throw new IllegalArgumentException("Lesson with id " + quiz.getLesson().getId() + " not found");
        }

        // Set default passing score if not specified
        if (quiz.getMinPassingScore() <= 0) {
            quiz.setMinPassingScore(70); // Default 70% passing score
        }

        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        return quizRepository.findById(id)
                .map(existingQuiz -> {
                    // Update fields
                    if (quizDetails.getTitle() != null) {
                        existingQuiz.setTitle(quizDetails.getTitle());
                    }

                    if (quizDetails.getDescription() != null) {
                        existingQuiz.setDescription(quizDetails.getDescription());
                    }

                    if (quizDetails.getMinPassingScore() > 0) {
                        existingQuiz.setMinPassingScore(quizDetails.getMinPassingScore());
                    }

                    if (quizDetails.getLesson() != null && quizDetails.getLesson().getId() != null) {
                        // Verify lesson exists
                        if (!lessonService.existsById(quizDetails.getLesson().getId())) {
                            throw new IllegalArgumentException("Lesson with id " +
                                    quizDetails.getLesson().getId() + " not found");
                        }
                        
                        // Get the lesson from the service to ensure we have the full entity
                        Lesson lesson = lessonService.getLessonById(quizDetails.getLesson().getId())
                                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
                        
                        existingQuiz.setLesson(lesson);
                    }

                    return quizRepository.save(existingQuiz);
                })
                .orElseThrow(() -> new RuntimeException("Quiz not found with id " + id));
    }

    @Override
    @Transactional
    public boolean deleteQuiz(Long id) {
        return quizRepository.findById(id)
                .map(quiz -> {
                    quizRepository.delete(quiz);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean existsById(Long id) {
        return quizRepository.existsById(id);
    }

    @Override
    public Page<Quiz> getQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Quiz addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id " + quizId));

        // Set the quiz reference in the question
        question.setQuiz(quiz);
        
        // Save the question
        Question savedQuestion = questionRepository.save(question);
        
        // Add the question to the quiz's questions list if it's not null
        if (quiz.getQuestions() == null) {
            quiz.setQuestions(new ArrayList<>());
        }
        quiz.getQuestions().add(savedQuestion);
        
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public Quiz removeQuestionFromQuiz(Long quizId, Long questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id " + quizId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id " + questionId));

        // Check if the question belongs to this quiz
        if (!question.getQuiz().getId().equals(quizId)) {
            throw new IllegalArgumentException("Question does not belong to this quiz");
        }

        // Remove the question from the quiz's questions list
        if (quiz.getQuestions() != null) {
            quiz.getQuestions().removeIf(q -> q.getId().equals(questionId));
        }
        
        // Delete the question
        questionRepository.delete(question);
        
        return quizRepository.save(quiz);
    }

    @Override
    public double getAverageScore(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new RuntimeException("Quiz not found with id " + quizId);
        }
        
        Double averageScore = quizRepository.calculateAverageScore(quizId);
        return averageScore != null ? averageScore : 0.0;
    }

    @Override
    public double getPassRate(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new RuntimeException("Quiz not found with id " + quizId);
        }
        
        Double passRate = quizRepository.calculatePassRate(quizId);
        return passRate != null ? passRate * 100 : 0.0; // Convert to percentage
    }
}