package edtech.afrilingo.lesson.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LessonContentService {

    /**
     * Get all lesson contents
     * @return List of all lesson contents
     */
    List<LessonContent> getAllLessonContents();

    /**
     * Get lesson contents by lesson ID
     * @param lessonId Lesson ID
     * @return List of lesson contents for the given lesson
     */
    List<LessonContent> getLessonContentsByLessonId(Long lessonId);

    /**
     * Get lesson contents by content type
     * @param contentType Content type
     * @return List of lesson contents of the given type
     */
    List<LessonContent> getLessonContentsByType(ContentType contentType);

    /**
     * Get lesson content by ID
     * @param id Lesson content ID
     * @return Optional containing the lesson content if found
     */
    Optional<LessonContent> getLessonContentById(Long id);

    /**
     * Create a new lesson content
     * @param lessonContent Lesson content to create
     * @return Created lesson content with generated ID
     */
    LessonContent createLessonContent(LessonContent lessonContent);

    /**
     * Update an existing lesson content
     * @param id Lesson content ID
     * @param lessonContentDetails Updated lesson content details
     * @return Updated lesson content
     */
    LessonContent updateLessonContent(Long id, LessonContent lessonContentDetails);

    /**
     * Delete a lesson content
     * @param id Lesson content ID
     * @return true if deleted successfully
     */
    boolean deleteLessonContent(Long id);

    /**
     * Check if lesson content exists by ID
     * @param id Lesson content ID
     * @return true if lesson content exists
     */
    boolean existsById(Long id);

    /**
     * Get paginated lesson contents
     * @param pageable Pagination information
     * @return Page of lesson contents
     */
    Page<LessonContent> getLessonContents(Pageable pageable);

    /**
     * Get paginated lesson contents by lesson ID
     * @param lessonId Lesson ID
     * @param pageable Pagination information
     * @return Page of lesson contents
     */
    Page<LessonContent> getLessonContentsByLessonId(Long lessonId, Pageable pageable);

    /**
     * Bulk create lesson contents
     * @param lessonContents List of lesson contents to create
     * @return List of created lesson contents
     */
    List<LessonContent> bulkCreateLessonContents(List<LessonContent> lessonContents);

    /**
     * Count lesson contents by content type
     * @return Map of content type to count
     */
    Map<ContentType, Long> countByContentType();

    /**
     * Search lesson contents by keyword in content data
     * @param keyword Keyword to search for
     * @return List of matching lesson contents
     */
    List<LessonContent> searchByKeyword(String keyword);

    /**
     * Copy all contents from one lesson to another
     * @param sourceLessonId Source lesson ID
     * @param targetLessonId Target lesson ID
     * @return Number of contents copied
     */
    int copyContentsToLesson(Long sourceLessonId, Long targetLessonId);

    /**
     * Delete all contents for a lesson
     * @param lessonId Lesson ID
     * @return Number of contents deleted
     */
    int deleteAllByLessonId(Long lessonId);
}