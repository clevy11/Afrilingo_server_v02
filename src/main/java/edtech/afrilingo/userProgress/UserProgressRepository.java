package edtech.afrilingo.userProgress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    /**
     * Find user progress by user ID
     * @param userId User ID
     * @return List of user progress
     */
    List<UserProgress> findByUserId(Long userId);
    
    /**
     * Find user progress by user ID and lesson ID
     * @param userId User ID
     * @param lessonId Lesson ID
     * @return Optional containing the user progress if found
     */
    Optional<UserProgress> findByUserIdAndLessonId(Long userId, Long lessonId);
    
    /**
     * Find completed lessons by user ID
     * @param userId User ID
     * @return List of user progress for completed lessons
     */
    List<UserProgress> findByUserIdAndCompletedTrue(Long userId);
    
    /**
     * Find user progress by user ID and completion date after a specific time
     * @param userId User ID
     * @param date Date to compare with
     * @return List of user progress completed after the given date
     */
    List<UserProgress> findByUserIdAndCompletedAtAfter(Long userId, LocalDateTime date);
    
    /**
     * Count completed lessons by user ID
     * @param userId User ID
     * @return Count of completed lessons
     */
    int countByUserIdAndCompletedTrue(Long userId);
    
    /**
     * Find user progress by user ID and lesson IDs
     * @param userId User ID
     * @param lessonIds List of lesson IDs
     * @return List of user progress
     */
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId AND up.lesson.id IN :lessonIds")
    List<UserProgress> findByUserIdAndLessonIdIn(@Param("userId") Long userId, @Param("lessonIds") List<Long> lessonIds);
}