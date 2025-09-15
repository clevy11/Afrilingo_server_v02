package edtech.afrilingo.course;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByLanguageId(Long languageId);
    List<Course> findByLanguageIdAndIsActiveTrue(Long languageId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Course findByIdWithPessimisticLock(@Param("id") Long id);

    // Shallow fetch to avoid N+1; prefetch language only
    @EntityGraph(attributePaths = {"language"})
    @Query("SELECT c FROM Course c")
    List<Course> findAllShallow();
}