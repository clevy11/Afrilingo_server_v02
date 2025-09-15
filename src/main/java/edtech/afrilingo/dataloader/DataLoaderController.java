package edtech.afrilingo.dataloader;

import edtech.afrilingo.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/data-loader")
@RequiredArgsConstructor
@Tag(name = "Data Loader", description = "Endpoints for loading initial data into the system")
public class DataLoaderController {
    
    private final DataLoaderService dataLoaderService;
    private final DataHealthService dataHealthService;
    
    @Operation(
            summary = "Check data health",
            description = "Checks the integrity and completeness of all data in the system",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/health-check")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkDataHealth() {
        Map<String, Object> healthStatus = dataHealthService.checkDataIntegrity();
        return ResponseEntity.ok(ApiResponse.success(healthStatus));
    }
    
    @Operation(
            summary = "Repair data",
            description = "Automatically repairs any data inconsistencies found in the system",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/repair-data")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> repairData() {
        Map<String, Object> repairResults = dataHealthService.repairData();
        return ResponseEntity.ok(ApiResponse.success(repairResults));
    }
    
    @Operation(
            summary = "Load all data",
            description = "Loads all initial data into the system (languages, courses, lessons, etc.)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/load-all")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> loadAllData() {
        dataLoaderService.loadAllData();
        return ResponseEntity.ok(ApiResponse.success("All data loaded successfully"));
    }
    
    @Operation(
            summary = "Load languages",
            description = "Loads only language data into the system",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/load-languages")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> loadLanguages() {
        dataLoaderService.loadLanguages();
        return ResponseEntity.ok(ApiResponse.success("Languages loaded successfully"));
    }
    
    @Operation(
            summary = "Load courses",
            description = "Loads course data into the system",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/load-courses")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> loadCourses() {
        dataLoaderService.loadCourses();
        return ResponseEntity.ok(ApiResponse.success("Courses loaded successfully"));
    }
    
    @Operation(
            summary = "Load lessons",
            description = "Loads lesson data for existing courses",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/load-lessons")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> loadLessons() {
        dataLoaderService.loadLessons();
        return ResponseEntity.ok(ApiResponse.success("Lessons loaded successfully"));
    }
    
    @Operation(
            summary = "Load lesson content",
            description = "Loads content data for existing lessons",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/load-lesson-content")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> loadLessonContent() {
        dataLoaderService.loadLessonContent();
        return ResponseEntity.ok(ApiResponse.success("Lesson content loaded successfully"));
    }
    
    @Operation(
            summary = "Load quizzes",
            description = "Loads quiz data for existing lessons",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/load-quizzes")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> loadQuizzes() {
        dataLoaderService.loadQuizzes();
        return ResponseEntity.ok(ApiResponse.success("Quizzes loaded successfully"));
    }
    
    @Operation(
            summary = "Load sample users",
            description = "Loads sample user data into the system",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/load-users")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> loadUsers() {
        dataLoaderService.loadUsers();
        return ResponseEntity.ok(ApiResponse.success("Sample users loaded successfully"));
    }
    
    @Operation(
            summary = "Reset data",
            description = "Deletes all data from the system (use with caution)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/reset")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ApiResponse<String>> resetData() {
        dataLoaderService.resetAllData();
        return ResponseEntity.ok(ApiResponse.success("All data reset successfully"));
    }
}