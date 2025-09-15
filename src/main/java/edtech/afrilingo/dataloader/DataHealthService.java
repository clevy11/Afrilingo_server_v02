package edtech.afrilingo.dataloader;

import edtech.afrilingo.course.CourseRepository;
import edtech.afrilingo.language.LanguageRepository;
import edtech.afrilingo.lesson.LessonRepository;
import edtech.afrilingo.lesson.content.LessonContentRepository;
import edtech.afrilingo.question.QuestionRepository;
import edtech.afrilingo.quiz.QuizRepository;
import edtech.afrilingo.quiz.option.OptionRepository;
import edtech.afrilingo.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for checking data integrity and repairing any inconsistencies in the database.
 * This service works in conjunction with DataLoaderService to ensure data is properly maintained.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataHealthService {

    private final LanguageRepository languageRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonContentRepository lessonContentRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;
    private final org.springframework.context.ApplicationContext applicationContext;

    private DataLoaderService getDataLoaderService() {
        return applicationContext.getBean(DataLoaderService.class);
    }

    /**
     * Checks the integrity of all data in the system.
     * @return A map containing the health status of each data category
     */
    public Map<String, Object> checkDataIntegrity() {
        Map<String, Object> healthStatus = new HashMap<>();
        
        // Check each data category
        healthStatus.put("languages", checkLanguages());
        healthStatus.put("courses", checkCourses());
        healthStatus.put("lessons", checkLessons());
        healthStatus.put("lessonContents", checkLessonContents());
        healthStatus.put("quizzes", checkQuizzes());
        healthStatus.put("questions", checkQuestions());
        healthStatus.put("options", checkOptions());
        healthStatus.put("users", checkUsers());
        
        // Calculate overall health status
        boolean overallHealth = healthStatus.values().stream()
                .filter(value -> value instanceof Map)
                .map(value -> (Map<String, Object>) value)
                .allMatch(map -> (boolean) map.get("healthy"));
        
        healthStatus.put("overallHealth", overallHealth);
        
        return healthStatus;
    }
    
    /**
     * Repairs any data inconsistencies found in the system.
     * @return A map containing the repair results for each data category
     */
    @Transactional
    public Map<String, Object> repairData() {
        Map<String, Object> repairResults = new HashMap<>();
        Map<String, Object> healthStatus = checkDataIntegrity();
        
        // Only repair categories that are not healthy
        if (!isHealthy(healthStatus, "languages")) {
            getDataLoaderService().loadLanguages();
            repairResults.put("languages", "repaired");
        }
        
        if (!isHealthy(healthStatus, "courses")) {
            getDataLoaderService().loadCourses();
            repairResults.put("courses", "repaired");
        }
        
        if (!isHealthy(healthStatus, "lessons")) {
            getDataLoaderService().loadLessons();
            repairResults.put("lessons", "repaired");
        }
        
        if (!isHealthy(healthStatus, "lessonContents")) {
            getDataLoaderService().loadLessonContent();
            repairResults.put("lessonContents", "repaired");
        }
        
        if (!isHealthy(healthStatus, "quizzes") || !isHealthy(healthStatus, "questions") || !isHealthy(healthStatus, "options")) {
            getDataLoaderService().loadQuizzes();
            repairResults.put("quizzes", "repaired");
            repairResults.put("questions", "repaired");
            repairResults.put("options", "repaired");
        }
        
        if (!isHealthy(healthStatus, "users")) {
            getDataLoaderService().loadUsers();
            repairResults.put("users", "repaired");
        }
        
        // If nothing was repaired, indicate that
        if (repairResults.isEmpty()) {
            repairResults.put("status", "No repairs needed");
        } else {
            repairResults.put("status", "Repairs completed");
        }
        
        return repairResults;
    }
    
    private boolean isHealthy(Map<String, Object> healthStatus, String category) {
        if (healthStatus.containsKey(category) && healthStatus.get(category) instanceof Map) {
            Map<String, Object> categoryStatus = (Map<String, Object>) healthStatus.get(category);
            return (boolean) categoryStatus.get("healthy");
        }
        return false;
    }
    
    private Map<String, Object> checkLanguages() {
        Map<String, Object> status = new HashMap<>();
        long count = languageRepository.count();
        status.put("count", count);
        // Align with seeded data: currently we seed 2 languages (RW, SW)
        status.put("healthy", count >= 2);
        status.put("message", count >= 2 ? "Languages data is healthy" : "Missing language data");
        return status;
    }
    
    private Map<String, Object> checkCourses() {
        Map<String, Object> status = new HashMap<>();
        long count = courseRepository.count();
        status.put("count", count);
        // Updated to reflect current seeding: 7 Kinyarwanda (Beginner + 6 specialized) + 1 Kiswahili (Beginner) = 8
        status.put("healthy", count >= 8);
        status.put("message", count >= 8 ? "Courses data is healthy" : "Missing course data");
        return status;
    }
    
    private Map<String, Object> checkLessons() {
        Map<String, Object> status = new HashMap<>();
        long count = lessonRepository.count();
        status.put("count", count);
        
        // Each course should have multiple lessons
        boolean healthy = count > 0 && count >= courseRepository.count() * 5;
        status.put("healthy", healthy);
        status.put("message", healthy ? "Lessons data is healthy" : "Missing lesson data");
        return status;
    }
    
    private Map<String, Object> checkLessonContents() {
        Map<String, Object> status = new HashMap<>();
        long count = lessonContentRepository.count();
        status.put("count", count);
        
        // Each lesson should have at least one content
        boolean healthy = count > 0 && count >= lessonRepository.count();
        status.put("healthy", healthy);
        status.put("message", healthy ? "Lesson content data is healthy" : "Missing lesson content data");
        return status;
    }
    
    private Map<String, Object> checkQuizzes() {
        Map<String, Object> status = new HashMap<>();
        long count = quizRepository.count();
        status.put("count", count);
        
        // Each lesson should have a quiz
        boolean healthy = count > 0 && count >= lessonRepository.count();
        status.put("healthy", healthy);
        status.put("message", healthy ? "Quiz data is healthy" : "Missing quiz data");
        return status;
    }
    
    private Map<String, Object> checkQuestions() {
        Map<String, Object> status = new HashMap<>();
        long count = questionRepository.count();
        status.put("count", count);
        
        // Each quiz should have multiple questions
        boolean healthy = count > 0 && count >= quizRepository.count() * 3;
        status.put("healthy", healthy);
        status.put("message", healthy ? "Question data is healthy" : "Missing question data");
        return status;
    }
    
    private Map<String, Object> checkOptions() {
        Map<String, Object> status = new HashMap<>();
        long count = optionRepository.count();
        status.put("count", count);
        
        // Each question should have multiple options
        boolean healthy = count > 0 && count >= questionRepository.count() * 3;
        status.put("healthy", healthy);
        status.put("message", healthy ? "Option data is healthy" : "Missing option data");
        return status;
    }
    
    private Map<String, Object> checkUsers() {
        Map<String, Object> status = new HashMap<>();
        long count = userRepository.count();
        status.put("count", count);
        status.put("healthy", count >= 1); // At least one admin user should be present
        status.put("message", count >= 1 ? "User data is healthy" : "Missing user data");
        return status;
    }
}
