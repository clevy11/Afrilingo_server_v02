package edtech.afrilingo.lesson.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonContentRepository extends JpaRepository<LessonContent, Long> {
    /**
     * Find all lesson contents by lesson ID
     * @param lessonId Lesson ID
     * @return List of lesson contents
     */
    List<LessonContent> findByLesson_Id(Long lessonId);

    /**
     * Find all lesson contents by content type
     * @param contentType Content type
     * @return List of lesson contents
     */
    List<LessonContent> findByContentType(ContentType contentType);

    /**
     * Find all lesson contents by lesson ID and content type
     * @param lessonId Lesson ID
     * @param contentType Content type
     * @return List of lesson contents
     */
    List<LessonContent> findByLesson_IdAndContentType(Long lessonId, ContentType contentType);

    /**
     * Delete all lesson contents by lesson ID
     * @param lessonId Lesson ID
     */
    void deleteByLesson_Id(Long lessonId);

    /**
     * Count lesson contents by lesson ID
     * @param lessonId Lesson ID
     * @return Count of lesson contents
     */
    int countByLesson_Id(Long lessonId);

    /**
     * Find lesson contents by lesson ID with pagination
     * @param lessonId Lesson ID
     * @param pageable Pagination information
     * @return Page of lesson contents
     */
    Page<LessonContent> findByLesson_Id(Long lessonId, Pageable pageable);

    /**
     * Search for lesson contents containing the given keyword in contentData
     * @param keyword Keyword to search for
     * @return List of matching lesson contents
     */
    @Query("SELECT lc FROM LessonContent lc WHERE lc.contentData LIKE %:keyword%")
    List<LessonContent> findByContentDataContainingKeyword(@Param("keyword") String keyword);

    /**
     * Count lesson contents by content type
     * @return Count of lesson contents by content type
     */
    @Query("SELECT lc.contentType, COUNT(lc) FROM LessonContent lc GROUP BY lc.contentType")
    List<Object[]> countByContentType();

    /**
     * Get content types used in a lesson
     * @param lessonId Lesson ID
     * @return List of content types
     */
    @Query("SELECT DISTINCT lc.contentType FROM LessonContent lc WHERE lc.lesson.id = :lessonId")
    List<ContentType> findContentTypesByLessonId(@Param("lessonId") Long lessonId);
}