package edtech.afrilingo.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public record UserDto(Long id, String firstName, String lastName, String email, Role role) {
        public static UserDto from(User u) {
            return new UserDto(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getRole());
        }
    }

    public record CreateUserRequest(String firstName, String lastName, String email, String password, Role role, Boolean enabled) {}

    // Get all users (JSON)
    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Also serve at base path to avoid HTML errors when client requests /api/v1/users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsersBase() {
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }



    // Create a new user (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        // Basic validation
        if (request == null ||
                isBlank(request.firstName()) ||
                isBlank(request.lastName()) ||
                isBlank(request.email()) ||
                isBlank(request.password()) ||
                request.role() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields: firstName, lastName, email, password, role");
        }

        // Check for existing email
        Optional<User> existing = userRepository.findByEmail(request.email());
        if (existing.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already in use");
        }

        boolean enabled = request.enabled() == null ? true : request.enabled();

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.from(saved));
    }

    // Enable/Disable a specific user by id
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    return ResponseEntity.ok(UserDto.from(user));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
