package edtech.afrilingo.lesson;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    @EntityGraph(attributePaths = {"course"})
    List<Lesson> findByCourseId(Long courseId);
    @EntityGraph(attributePaths = {"course"})
    List<Lesson> findByCourseIdOrderByOrderIndex(Long courseId);
    List<Lesson> findByType(LessonType type);
    List<Lesson> findByCourseIdAndOrderIndexGreaterThan(Long courseId, Integer orderIndex);
    int countByCourseId(Long courseId);
    boolean existsByCourseIdAndTitle(Long courseId, String title);
}