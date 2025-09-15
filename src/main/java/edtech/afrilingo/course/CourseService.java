package edtech.afrilingo.course;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CourseService {
    
    /**
     * Get all courses
     * @return List of all courses
     */
    List<Course> getAllCourses();
    
    /**
     * Get courses by language ID
     * @param languageId Language ID
     * @return List of courses for the given language
     */
    List<Course> getCoursesByLanguageId(Long languageId);
    
    /**
     * Get active courses by language ID
     * @param languageId Language ID
     * @return List of active courses for the given language
     */
    List<Course> getActiveCoursesByLanguageId(Long languageId);
    
    /**
     * Get course by ID
     * @param id Course ID
     * @return Optional containing the course if found
     */
    Optional<Course> getCourseById(Long id);
    
    /**
     * Create a new course
     * @param course Course to create
     * @return Created course with generated ID
     */
    Course createCourse(Course course);
    
    /**
     * Update an existing course
     * @param id Course ID
     * @param courseDetails Updated course details
     * @return Updated course
     */
    Course updateCourse(Long id, Course courseDetails);
    
    /**
     * Delete a course
     * @param id Course ID
     * @return true if deleted successfully
     */
    boolean deleteCourse(Long id);
    
    /**
     * Activate or deactivate a course
     * @param id Course ID
     * @param active Active status
     * @return Updated course
     */
    Course setActivationStatus(Long id, boolean active);
    
    /**
     * Check if course exists by ID
     * @param id Course ID
     * @return true if course exists
     */
    boolean existsById(Long id);
}