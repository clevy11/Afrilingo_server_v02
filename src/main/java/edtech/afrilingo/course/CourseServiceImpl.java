package edtech.afrilingo.course;

import edtech.afrilingo.language.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import static edtech.afrilingo.config.CacheConfig.COURSES_CACHE;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final LanguageService languageService;

    @Override
    @Cacheable(cacheNames = COURSES_CACHE)
    public List<Course> getAllCourses() {
        // Use shallow fetch (only language pre-fetched) to avoid N+1 and large graphs
        return courseRepository.findAllShallow();
    }

    @Override
    public List<Course> getCoursesByLanguageId(Long languageId) {
        return courseRepository.findByLanguageId(languageId);
    }

    @Override
    public List<Course> getActiveCoursesByLanguageId(Long languageId) {
        return courseRepository.findByLanguageIdAndIsActiveTrue(languageId);
    }

    @Override
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = COURSES_CACHE, allEntries = true)
    public Course createCourse(Course course) {
        // Validate course data
        if (course.getTitle() == null || course.getLanguage() == null || course.getLanguage().getId() == null) {
            throw new IllegalArgumentException("Course title and language are required");
        }

        // Verify language exists
        if (!languageService.existsById(course.getLanguage().getId())) {
            throw new IllegalArgumentException("Language with id " + course.getLanguage().getId() + " not found");
        }

        // Set as active by default if not explicitly set to inactive
        if (!course.isActive()) {
            course.setActive(true);
        }

        // Create a new course instance to avoid any potential issues with the input object
        Course newCourse = Course.builder()
                .title(course.getTitle())
                .description(course.getDescription())
                .level(course.getLevel())
                .image(course.getImage())
                .isActive(course.isActive())
                .language(course.getLanguage())
                .build();

        try {
            return courseRepository.save(newCourse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create course: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = COURSES_CACHE, allEntries = true)
    public Course updateCourse(Long id, Course courseDetails) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    // Update fields
                    if (courseDetails.getTitle() != null) {
                        existingCourse.setTitle(courseDetails.getTitle());
                    }

                    if (courseDetails.getDescription() != null) {
                        existingCourse.setDescription(courseDetails.getDescription());
                    }

                    if (courseDetails.getLevel() != null) {
                        existingCourse.setLevel(courseDetails.getLevel());
                    }

                    if (courseDetails.getImage() != null) {
                        existingCourse.setImage(courseDetails.getImage());
                    }

                    // For boolean, we can directly set the value from the update request
                    existingCourse.setActive(courseDetails.isActive());

                    if (courseDetails.getLanguage() != null && courseDetails.getLanguage().getId() != null) {
                        // Verify language exists
                        if (!languageService.existsById(courseDetails.getLanguage().getId())) {
                            throw new IllegalArgumentException("Language with id " +
                                    courseDetails.getLanguage().getId() + " not found");
                        }
                        existingCourse.setLanguage(courseDetails.getLanguage());
                    }

                    return courseRepository.save(existingCourse);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with id " + id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = COURSES_CACHE, allEntries = true)
    public boolean deleteCourse(Long id) {
        return courseRepository.findById(id)
                .map(course -> {
                    courseRepository.delete(course);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = COURSES_CACHE, allEntries = true)
    public Course setActivationStatus(Long id, boolean active) {
        return courseRepository.findById(id)
                .map(course -> {
                    course.setActive(active);
                    return courseRepository.save(course);
                })
                .orElseThrow(() -> new RuntimeException("Course not found with id " + id));
    }

    @Override
    public boolean existsById(Long id) {
        return courseRepository.existsById(id);
    }
}