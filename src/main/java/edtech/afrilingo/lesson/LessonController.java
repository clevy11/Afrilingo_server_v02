package edtech.afrilingo.lesson;

import edtech.afrilingo.lesson.content.LessonContent;
import edtech.afrilingo.lesson.content.LessonContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final  LessonService lessonService;
    private final LessonContentService lessonContentService;

    @GetMapping
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Lesson>> getPaginatedLessons(Pageable pageable) {
        return ResponseEntity.ok(lessonService.getLessons(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
        return lessonService.getLessonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/contents")
    public ResponseEntity<List<LessonContent>> getLessonContents(@PathVariable Long id) {
        // Always succeed with empty list if none, to keep frontend simple
        List<LessonContent> contents = lessonContentService.getLessonContentsByLessonId(id);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Lesson>> getLessonsByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
    }

    @GetMapping("/course/{courseId}/ordered")
    public ResponseEntity<List<Lesson>> getLessonsByCourseIdOrdered(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseIdOrdered(courseId));
    }

    @GetMapping("/type/{lessonType}")
    public ResponseEntity<List<Lesson>> getLessonsByType(
            @PathVariable LessonType lessonType
    ) {
        return ResponseEntity.ok(lessonService.getLessonsByType(lessonType));
    }

    @PostMapping
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        try {
            Lesson createdLesson = lessonService.createLesson(lesson);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLesson);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable Long id,
            @RequestBody Lesson lessonDetails
    ) {
        try {
            if (!lessonService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            Lesson updatedLesson = lessonService.updateLesson(id, lessonDetails);
            return ResponseEntity.ok(updatedLesson);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/course/{courseId}/reorder")
    public ResponseEntity<List<Lesson>> reorderLessons(
            @PathVariable Long courseId,
            @RequestBody List<Long> lessonIds
    ) {
        try {
            List<Lesson> reorderedLessons = lessonService.reorderLessons(courseId, lessonIds);
            return ResponseEntity.ok(reorderedLessons);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        if (!lessonService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        boolean deleted = lessonService.deleteLesson(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.internalServerError().build();
    }
}