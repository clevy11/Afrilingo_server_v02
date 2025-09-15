package edtech.afrilingo.auth;

import edtech.afrilingo.config.JwtService;
import edtech.afrilingo.dto.GoogleTokenRequest;
import edtech.afrilingo.dto.GoogleUser;
import edtech.afrilingo.auth.GoogleTokenVerificationService;
import edtech.afrilingo.user.Role;
import edtech.afrilingo.user.User;
import edtech.afrilingo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class MobileAuthController {

    private final GoogleTokenVerificationService googleTokenService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/google/mobile")
    public ResponseEntity<AuthenticationResponse> authenticateWithGoogleToken(
            @RequestBody GoogleTokenRequest request) {
        try {
            log.info("Attempting to verify Google token for platform: {}", request.getPlatform());

            // Verify the Google ID token
            GoogleUser googleUser = googleTokenService.verifyToken(request.getIdToken());

            log.info("Google token verified for user: {}", googleUser.getEmail());

            // Find or create user
            User user = findOrCreateGoogleUser(googleUser);

            // Generate JWT tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Save the access token
            authenticationService.saveUserToken(user, accessToken);

            log.info("Successfully authenticated Google user: {}", user.getEmail());

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build());

        } catch (Exception e) {
            log.error("Failed to authenticate with Google token", e);
            return ResponseEntity.badRequest().build();
        }
    }

    private User findOrCreateGoogleUser(GoogleUser googleUser) {
        return userRepository.findByEmail(googleUser.getEmail())
                .orElseGet(() -> {
                    log.info("Creating new user from Google account: {}", googleUser.getEmail());

                    User newUser = User.builder()
                            .email(googleUser.getEmail())
                            .firstName(googleUser.getFirstName() != null ? googleUser.getFirstName() : "")
                            .lastName(googleUser.getLastName() != null ? googleUser.getLastName() : "")
                            .password("") // No password for OAuth users
                            .role(Role.ROLE_USER)
                            .build();

                    return userRepository.save(newUser);
                });
    }
}