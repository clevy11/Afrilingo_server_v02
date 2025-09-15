package edtech.afrilingo.lesson;

import edtech.afrilingo.course.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import static edtech.afrilingo.config.CacheConfig.LESSONS_CACHE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseService courseService;

    @Override
    @Cacheable(cacheNames = LESSONS_CACHE)
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public Page<Lesson> getLessons(Pageable pageable) {
        return lessonRepository.findAll(pageable);
    }

    @Override
    public List<Lesson> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findByCourseId(courseId)
                .stream()
                .collect(Collectors.toMap(
                        Lesson::getTitle,      // use title as the key
                        lesson -> lesson,      // value is the lesson itself
                        (existing, duplicate) -> existing // keep the first one if duplicate
                ))
                .values()
                .stream()
                .toList();
    }


    @Override
    public List<Lesson> getLessonsByCourseIdOrdered(Long courseId) {
        return lessonRepository.findByCourseIdOrderByOrderIndex(courseId);
    }

    @Override
    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    @Override
    public List<Lesson> getLessonsByType(LessonType lessonType) {
        return lessonRepository.findByType(lessonType);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = LESSONS_CACHE, allEntries = true)
    public Lesson createLesson(Lesson lesson) {
        // Validate lesson data
        if (lesson.getTitle() == null || lesson.getCourse() == null || lesson.getCourse().getId() == null) {
            throw new IllegalArgumentException("Lesson title and course are required");
        }

        // Verify course exists
        if (!courseService.existsById(lesson.getCourse().getId())) {
            throw new IllegalArgumentException("Course with id " + lesson.getCourse().getId() + " not found");
        }

        // Set required to true by default if not explicitly set to false
        // Note: Since it's a primitive boolean, it defaults to false if not set
        if (!lesson.isRequired()) {
            lesson.setRequired(true);
        }

        // Set order index if it's the default value (0)
        if (lesson.getOrderIndex() == 0) {
            // Get the count of existing lessons in the course and set order index to that value
            int lessonCount = lessonRepository.countByCourseId(lesson.getCourse().getId());
            lesson.setOrderIndex(lessonCount);
        }

        return lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = LESSONS_CACHE, allEntries = true)
    public Lesson updateLesson(Long id, Lesson lessonDetails) {
        return lessonRepository.findById(id)
                .map(existingLesson -> {
                    // Update fields
                    if (lessonDetails.getTitle() != null) {
                        existingLesson.setTitle(lessonDetails.getTitle());
                    }

                    if (lessonDetails.getDescription() != null) {
                        existingLesson.setDescription(lessonDetails.getDescription());
                    }

                    if (lessonDetails.getType() != null) {
                        existingLesson.setType(lessonDetails.getType());
                    }

                    // For primitive int, we need to be careful about the default value (0)
                    // Only update if it's explicitly set to a non-zero value
                    if (lessonDetails.getOrderIndex() != 0) {
                        existingLesson.setOrderIndex(lessonDetails.getOrderIndex());
                    }

                    // For boolean, we can directly set the value
                    // This assumes that if lessonDetails is being provided,
                    // the required status is meant to be updated
                    existingLesson.setRequired(lessonDetails.isRequired());

                    if (lessonDetails.getCourse() != null && lessonDetails.getCourse().getId() != null) {
                        // Verify course exists
                        if (!courseService.existsById(lessonDetails.getCourse().getId())) {
                            throw new IllegalArgumentException("Course with id " +
                                    lessonDetails.getCourse().getId() + " not found");
                        }
                        existingLesson.setCourse(lessonDetails.getCourse());
                    }

                    return lessonRepository.save(existingLesson);
                })
                .orElseThrow(() -> new RuntimeException("Lesson not found with id " + id));
    }

    @Override
    @Transactional
    public boolean deleteLesson(Long id) {
        return lessonRepository.findById(id)
                .map(lesson -> {
                    lessonRepository.delete(lesson);
                    // Reorder remaining lessons
                    reorderAfterDelete(lesson.getCourse().getId(), lesson.getOrderIndex());
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public List<Lesson> reorderLessons(Long courseId, List<Long> lessonIds) {
        // Verify course exists
        if (!courseService.existsById(courseId)) {
            throw new IllegalArgumentException("Course with id " + courseId + " not found");
        }

        // Verify all lessons exist and belong to the course
        List<Lesson> lessons = lessonRepository.findAllById(lessonIds);

        if (lessons.size() != lessonIds.size()) {
            throw new IllegalArgumentException("Some lesson IDs are invalid");
        }

        for (Lesson lesson : lessons) {
            if (!lesson.getCourse().getId().equals(courseId)) {
                throw new IllegalArgumentException("Lesson with id " + lesson.getId() +
                        " does not belong to course with id " + courseId);
            }
        }

        // Update order indexes
        List<Lesson> updatedLessons = new ArrayList<>();
        for (int i = 0; i < lessonIds.size(); i++) {
            Long lessonId = lessonIds.get(i);
            for (Lesson lesson : lessons) {
                if (lesson.getId().equals(lessonId)) {
                    lesson.setOrderIndex(i);
                    updatedLessons.add(lessonRepository.save(lesson));
                    break;
                }
            }
        }

        return updatedLessons;
    }

    @Override
    public boolean existsById(Long id) {
        return lessonRepository.existsById(id);
    }

    /**
     * Helper method to reorder lessons after a deletion
     * @param courseId Course ID
     * @param deletedIndex Index of the deleted lesson
     */
    private void reorderAfterDelete(Long courseId, int deletedIndex) {
        List<Lesson> lessonsToUpdate = lessonRepository.findByCourseIdAndOrderIndexGreaterThan(
                courseId, deletedIndex);

        lessonsToUpdate.forEach(lesson -> {
            lesson.setOrderIndex(lesson.getOrderIndex() - 1);
            lessonRepository.save(lesson);
        });
    }
}