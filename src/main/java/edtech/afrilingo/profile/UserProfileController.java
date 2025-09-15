package edtech.afrilingo.profile;

import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.exception.ResourceNotFoundException;
import edtech.afrilingo.language.Language;
import edtech.afrilingo.language.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import edtech.afrilingo.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for user profile management")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final LanguageService languageService;

    @Operation(summary = "Get current user's profile", description = "Returns the profile of the currently authenticated user")
    @GetMapping
    public ResponseEntity<ApiResponse<UserProfile>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserProfile userProfile = userProfileService.getUserProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", currentUser.getId()));

        return ResponseEntity.ok(ApiResponse.success(userProfile));
    }

    @Operation(summary = "Check if user has profile", description = "Checks if the current user has already set up a profile")
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkUserProfileExists() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        boolean hasProfile = userProfileService.hasUserProfile(currentUser.getId());

        return ResponseEntity.ok(ApiResponse.success(hasProfile));
    }

    @Operation(summary = "Create or update user profile", description = "Creates a new profile or updates existing profile for the current user")
    @PostMapping
    public ResponseEntity<ApiResponse<UserProfile>> createOrUpdateUserProfile(@RequestBody UserProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserProfile savedProfile = userProfileService.createOrUpdateUserProfile(currentUser.getId(), request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedProfile, "User profile created/updated successfully"));
    }

    @Operation(summary = "Update languages to learn", description = "Updates the list of languages the user wants to learn")
    @PutMapping("/languages")
    public ResponseEntity<ApiResponse<UserProfile>> updateLanguagesToLearn(@RequestBody List<Long> languageIds) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserProfile updatedProfile = userProfileService.updateLanguagesToLearn(currentUser.getId(), languageIds);

        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Languages to learn updated successfully"));
    }

    @Operation(summary = "Update learning preferences", description = "Updates the user's learning preferences")
    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<UserProfile>> updateLearningPreferences(
            @RequestParam(required = false, defaultValue = "false") boolean dailyReminders,
            @RequestParam(required = false, defaultValue = "15") int dailyGoalMinutes,
            @RequestParam(required = false) String preferredLearningTime) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserProfile updatedProfile = userProfileService.updateLearningPreferences(
                currentUser.getId(), dailyReminders, dailyGoalMinutes, preferredLearningTime);

        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Learning preferences updated successfully"));
    }

    @Operation(summary = "Update profile picture", description = "Updates the user's profile picture")
    @PutMapping("/picture")
    public ResponseEntity<ApiResponse<UserProfile>> updateProfilePicture(@RequestBody Map<String, String> body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        String profilePicture = body != null ? body.get("profilePicture") : null;
        UserProfile updatedProfile = userProfileService.updateProfilePicture(currentUser.getId(), profilePicture);

        return ResponseEntity.ok(ApiResponse.success(updatedProfile, "Profile picture updated successfully"));
    }

    @Operation(summary = "Get user's name", description = "Returns the first name and last name of the currently authenticated user")
    @GetMapping("/name")
    public ResponseEntity<ApiResponse<Map<String, String>>> getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Create a simple map with just the name data to avoid circular references
        Map<String, String> nameData = new HashMap<>();
        nameData.put("firstName", currentUser.getFirstName());
        nameData.put("lastName", currentUser.getLastName());
        nameData.put("email", currentUser.getEmail());

        return ResponseEntity.ok(ApiResponse.success(nameData));
    }

    @Operation(summary = "Delete user profile", description = "Deletes the current user's profile")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        UserProfile userProfile = userProfileService.getUserProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile", "userId", currentUser.getId()));

        userProfileService.deleteUserProfile(userProfile.getId());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(200)
                        .message("User profile deleted successfully")
                        .build()
        );
    }
}