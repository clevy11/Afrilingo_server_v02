package edtech.afrilingo.lesson;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LessonService {
    
    /**
     * Get all lessons
     * @return List of all lessons
     */
    List<Lesson> getAllLessons();
    
    /**
     * Get paginated lessons
     * @param pageable Pagination information
     * @return Page of lessons
     */
    Page<Lesson> getLessons(Pageable pageable);
    
    /**
     * Get lessons by course ID
     * @param courseId Course ID
     * @return List of lessons for the given course
     */
    List<Lesson> getLessonsByCourseId(Long courseId);
    
    /**
     * Get lessons by course ID, ordered by orderIndex
     * @param courseId Course ID
     * @return Ordered list of lessons
     */
    List<Lesson> getLessonsByCourseIdOrdered(Long courseId);
    
    /**
     * Get lesson by ID
     * @param id Lesson ID
     * @return Optional containing the lesson if found
     */
    Optional<Lesson> getLessonById(Long id);
    
    /**
     * Get lessons by type
     * @param lessonType Lesson type
     * @return List of lessons of the given type
     */
    List<Lesson> getLessonsByType(LessonType lessonType);
    
    /**
     * Create a new lesson
     * @param lesson Lesson to create
     * @return Created lesson with generated ID
     */
    Lesson createLesson(Lesson lesson);
    
    /**
     * Update an existing lesson
     * @param id Lesson ID
     * @param lessonDetails Updated lesson details
     * @return Updated lesson
     */
    Lesson updateLesson(Long id, Lesson lessonDetails);
    
    /**
     * Delete a lesson
     * @param id Lesson ID
     * @return true if deleted successfully
     */
    boolean deleteLesson(Long id);
    
    /**
     * Reorder lessons in a course
     * @param courseId Course ID
     * @param lessonIds Ordered list of lesson IDs
     * @return Updated lessons
     */
    List<Lesson> reorderLessons(Long courseId, List<Long> lessonIds);
    
    /**
     * Check if lesson exists by ID
     * @param id Lesson ID
     * @return true if lesson exists
     */
    boolean existsById(Long id);
}