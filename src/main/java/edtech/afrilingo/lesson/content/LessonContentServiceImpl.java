package edtech.afrilingo.lesson.content;

import edtech.afrilingo.lesson.Lesson;
import edtech.afrilingo.lesson.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonContentServiceImpl implements LessonContentService {

    private final LessonContentRepository lessonContentRepository;
    private final LessonService lessonService;

    @Override
    public List<LessonContent> getAllLessonContents() {
        return lessonContentRepository.findAll();
    }

    @Override
    public List<LessonContent> getLessonContentsByLessonId(Long lessonId) {
        return lessonContentRepository.findByLesson_Id(lessonId);
    }

    @Override
    public List<LessonContent> getLessonContentsByType(ContentType contentType) {
        return lessonContentRepository.findByContentType(contentType);
    }

    @Override
    public Optional<LessonContent> getLessonContentById(Long id) {
        return lessonContentRepository.findById(id);
    }

    @Override
    @Transactional
    public LessonContent createLessonContent(LessonContent lessonContent) {
        // Validate lesson content data
        if (lessonContent.getContentType() == null ||
                lessonContent.getLesson() == null ||
                lessonContent.getLesson().getId() == null) {
            throw new IllegalArgumentException("Content type and lesson are required");
        }

        // Verify lesson exists
        if (!lessonService.existsById(lessonContent.getLesson().getId())) {
            throw new IllegalArgumentException("Lesson with id " + lessonContent.getLesson().getId() + " not found");
        }

        // Validate based on content type
        validateContentByType(lessonContent);

        return lessonContentRepository.save(lessonContent);
    }

    @Override
    @Transactional
    public LessonContent updateLessonContent(Long id, LessonContent lessonContentDetails) {
        return lessonContentRepository.findById(id)
                .map(existingContent -> {
                    // Update fields
                    if (lessonContentDetails.getContentType() != null) {
                        existingContent.setContentType(lessonContentDetails.getContentType());
                    }

                    if (lessonContentDetails.getContentData() != null) {
                        existingContent.setContentData(lessonContentDetails.getContentData());
                    }

                    if (lessonContentDetails.getMediaUrl() != null) {
                        existingContent.setMediaUrl(lessonContentDetails.getMediaUrl());
                    }

                    if (lessonContentDetails.getLesson() != null && lessonContentDetails.getLesson().getId() != null) {
                        // Verify lesson exists
                        if (!lessonService.existsById(lessonContentDetails.getLesson().getId())) {
                            throw new IllegalArgumentException("Lesson with id " +
                                    lessonContentDetails.getLesson().getId() + " not found");
                        }
                        existingContent.setLesson(lessonContentDetails.getLesson());
                    }

                    // Validate updated content
                    validateContentByType(existingContent);

                    return lessonContentRepository.save(existingContent);
                })
                .orElseThrow(() -> new RuntimeException("Lesson content not found with id " + id));
    }

    @Override
    @Transactional
    public boolean deleteLessonContent(Long id) {
        return lessonContentRepository.findById(id)
                .map(lessonContent -> {
                    lessonContentRepository.delete(lessonContent);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean existsById(Long id) {
        return lessonContentRepository.existsById(id);
    }

    @Override
    public Page<LessonContent> getLessonContents(Pageable pageable) {
        return lessonContentRepository.findAll(pageable);
    }

    @Override
    public Page<LessonContent> getLessonContentsByLessonId(Long lessonId, Pageable pageable) {
        return lessonContentRepository.findByLesson_Id(lessonId, pageable);
    }

    @Override
    @Transactional
    public List<LessonContent> bulkCreateLessonContents(List<LessonContent> lessonContents) {
        // Validate all contents before saving
        lessonContents.forEach(this::validateLessonContent);

        return lessonContentRepository.saveAll(lessonContents);
    }

    @Override
    public Map<ContentType, Long> countByContentType() {
        List<Object[]> results = lessonContentRepository.countByContentType();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (ContentType) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Override
    public List<LessonContent> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be empty");
        }

        return lessonContentRepository.findByContentDataContainingKeyword(keyword);
    }

    @Override
    @Transactional
    public int copyContentsToLesson(Long sourceLessonId, Long targetLessonId) {
        // Verify both lessons exist
        if (!lessonService.existsById(sourceLessonId)) {
            throw new IllegalArgumentException("Source lesson with id " + sourceLessonId + " not found");
        }

        if (!lessonService.existsById(targetLessonId)) {
            throw new IllegalArgumentException("Target lesson with id " + targetLessonId + " not found");
        }

        // Get source contents
        List<LessonContent> sourceContents = lessonContentRepository.findByLesson_Id(sourceLessonId);

        if (sourceContents.isEmpty()) {
            return 0;
        }

        // Get target lesson
        Lesson targetLesson = lessonService.getLessonById(targetLessonId)
                .orElseThrow(() -> new IllegalArgumentException("Target lesson not found"));

        // Create new contents for target lesson
        List<LessonContent> newContents = sourceContents.stream()
                .map(source -> LessonContent.builder()
                        .contentType(source.getContentType())
                        .contentData(source.getContentData())
                        .mediaUrl(source.getMediaUrl())
                        .lesson(targetLesson)
                        .build())
                .collect(Collectors.toList());

        lessonContentRepository.saveAll(newContents);

        return newContents.size();
    }

    @Override
    @Transactional
    public int deleteAllByLessonId(Long lessonId) {
        // Verify lesson exists
        if (!lessonService.existsById(lessonId)) {
            throw new IllegalArgumentException("Lesson with id " + lessonId + " not found");
        }

        List<LessonContent> contentsToDelete = lessonContentRepository.findByLesson_Id(lessonId);
        int count = contentsToDelete.size();

        lessonContentRepository.deleteByLesson_Id(lessonId);

        return count;
    }

    /**
     * Helper method to validate a lesson content before saving
     * @param lessonContent Lesson content to validate
     */
    private void validateLessonContent(LessonContent lessonContent) {
        // Validate basic requirements
        if (lessonContent.getContentType() == null ||
                lessonContent.getLesson() == null ||
                lessonContent.getLesson().getId() == null) {
            throw new IllegalArgumentException("Content type and lesson are required");
        }

        // Verify lesson exists
        if (!lessonService.existsById(lessonContent.getLesson().getId())) {
            throw new IllegalArgumentException("Lesson with id " + lessonContent.getLesson().getId() + " not found");
        }

        // Validate based on content type
        validateContentByType(lessonContent);
    }

    /**
     * Helper method to validate content based on type
     * @param lessonContent Lesson content to validate
     */
    private void validateContentByType(LessonContent lessonContent) {
        switch (lessonContent.getContentType()) {
            case TEXT:
                if (lessonContent.getContentData() == null || lessonContent.getContentData().trim().isEmpty()) {
                    throw new IllegalArgumentException("Text content requires content data");
                }
                break;
            case AUDIO:
                if (lessonContent.getMediaUrl() == null || lessonContent.getMediaUrl().trim().isEmpty()) {
                    throw new IllegalArgumentException("Audio content requires media URL");
                }
                break;
            case IMAGE:
                if (lessonContent.getMediaUrl() == null || lessonContent.getMediaUrl().trim().isEmpty()) {
                    throw new IllegalArgumentException("Image content requires media URL");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported content type: " + lessonContent.getContentType());
        }
    }
}