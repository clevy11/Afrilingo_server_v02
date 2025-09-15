package edtech.afrilingo.language;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @Operation(
            summary = "Get all languages",
            description = "Retrieves a list of all available languages in the system"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Languages found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Language>>> getAllLanguages() {
        List<Language> languages = languageService.getAllLanguages();
        return ResponseEntity.ok(ApiResponse.success(languages));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Language>> getLanguageById(@PathVariable Long id) {
        Language language = languageService.getLanguageById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "id", id));
        return ResponseEntity.ok(ApiResponse.success(language));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<Language>> getLanguageByCode(@PathVariable String code) {
        Language language = languageService.getLanguageByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Language", "code", code));
        return ResponseEntity.ok(ApiResponse.success(language));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Language>> createLanguage(@RequestBody Language language) {
        Language createdLanguage = languageService.createLanguage(language);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdLanguage, "Language created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Language>> updateLanguage(
            @PathVariable Long id,
            @RequestBody Language languageDetails
    ) {
        if (!languageService.existsById(id)) {
            throw new ResourceNotFoundException("Language", "id", id);
        }

        Language updatedLanguage = languageService.updateLanguage(id, languageDetails);
        return ResponseEntity.ok(ApiResponse.success(updatedLanguage, "Language updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLanguage(@PathVariable Long id) {
        if (!languageService.existsById(id)) {
            throw new ResourceNotFoundException("Language", "id", id);
        }

        languageService.deleteLanguage(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(200)
                        .message("Language deleted successfully")
                        .build()
        );
    }
}