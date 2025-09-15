package edtech.afrilingo.certification;

import edtech.afrilingo.certification.dto.CertificateResponseDTO;
import edtech.afrilingo.dto.ApiResponse;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/certification")
@RequiredArgsConstructor
@Tag(name = "Certification", description = "Automated Language Certification System")
public class CertificationController {
    
    private final CertificationService certificationService;
    
    @Operation(summary = "Initiate certification session", description = "Start a new certification test session")
    @PostMapping("/sessions/initiate")
    public ResponseEntity<ApiResponse<CertificationSession>> initiateCertification(
            @RequestParam String languageCode,
            @RequestParam String testLevel) {
        
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        try {
            CertificationSession session = certificationService.initiateCertificationSession(
                    currentUser, languageCode, testLevel);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(session, "Certification session initiated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Verify test environment", description = "Verify camera and environment setup")
    @PostMapping("/sessions/{sessionId}/verify-environment")
    public ResponseEntity<ApiResponse<String>> verifyEnvironment(
            @PathVariable Long sessionId,
            @RequestParam boolean cameraVerified,
            @RequestParam boolean environmentVerified) {
        
        try {
            certificationService.verifyTestEnvironment(sessionId, cameraVerified, environmentVerified);
            return ResponseEntity.ok(ApiResponse.success("Environment verified successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Get certification session", description = "Get session details by ID")
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<CertificationSession>> getSession(@PathVariable Long sessionId) {
        try {
            CertificationSession session = certificationService.getSessionById(sessionId);
            return ResponseEntity.ok(ApiResponse.success(session));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Get certification questions", description = "Retrieve questions for certification test")
    @GetMapping("/sessions/{sessionId}/questions")
    public ResponseEntity<ApiResponse<List<Question>>> getCertificationQuestions(@PathVariable Long sessionId) {
        try {
            List<Question> questions = certificationService.getCertificationQuestions(sessionId);
            return ResponseEntity.ok(ApiResponse.success(questions));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Submit answer", description = "Submit answer for a certification question")
    @PostMapping("/sessions/{sessionId}/answers")
    public ResponseEntity<ApiResponse<String>> submitAnswer(
            @PathVariable Long sessionId,
            @RequestBody AnswerSubmissionRequest request) {
        
        try {
            certificationService.recordAnswer(sessionId, request.getQuestionId(), 
                    request.getSelectedOptionId(), request.getTextAnswer(), request.getScore(), request.getTimeSpentMs());
            
            return ResponseEntity.ok(ApiResponse.success("Answer recorded successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Complete certification", description = "Complete certification and generate certificate")
    @PostMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<ApiResponse<CertificateResponseDTO>> completeCertification(@PathVariable Long sessionId) {
        try {
            CertificateResponseDTO certificate = certificationService.completeSessionAndGenerateCertificate(sessionId);
            
            if (certificate != null) {
                return ResponseEntity.ok(ApiResponse.success(certificate, "Certification completed successfully"));
            } else {
                return ResponseEntity.ok(ApiResponse.success(null, "Certification completed but certificate not issued due to policy violations"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "Complete certification and download PDF", description = "Completes the session, generates the certificate PDF and returns the file directly for download")
    @PostMapping(value = "/sessions/{sessionId}/complete/download")
    public ResponseEntity<Resource> completeCertificationAndDownload(@PathVariable Long sessionId) {
        try {
            CertificateResponseDTO certificate = certificationService.completeSessionAndGenerateCertificate(sessionId);
            if (certificate == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .header("Content-Type", "application/json")
                        .body(null);
            }

            // Derive the expected filename used by the PDF generator
            String fileName = "certificate_" + certificate.getCertificateId() + ".pdf";
            String storagePath = certificationService.getCertificateStoragePath();
            Path base = Paths.get(storagePath).toAbsolutePath().normalize();
            Path path = base.resolve(fileName).normalize();

            // Prevent path traversal
            if (!path.startsWith(base)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            if (!Files.exists(path)) {
                // As a fallback, try to infer from the URL if present
                if (certificate.getCertificateUrl() != null) {
                    String url = certificate.getCertificateUrl();
                    int idx = url.lastIndexOf('/')
;
                    if (idx != -1) {
                        String fromUrl = url.substring(idx + 1);
                        Path alt = base.resolve(fromUrl).normalize();
                        if (!alt.startsWith(base)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                        if (Files.exists(alt)) {
                            path = alt;
                            fileName = fromUrl;
                        }
                    }
                }
            }

            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource resource = new FileSystemResource(path.toFile());
            long size = Files.size(path);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Type", "application/pdf")
                    .contentLength(size)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Terminate certification session", description = "Terminate a session without generating a certificate (e.g., user left the test)")
    @PostMapping("/sessions/{sessionId}/terminate")
    public ResponseEntity<ApiResponse<String>> terminateSession(
            @PathVariable Long sessionId,
            @RequestParam(required = false) String reason) {
        try {
            certificationService.terminateSession(sessionId, reason);
            return ResponseEntity.ok(ApiResponse.success("Session terminated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Record proctoring event", description = "Record suspicious activity during test")
    @PostMapping("/sessions/{sessionId}/proctor-events")
    public ResponseEntity<ApiResponse<String>> recordProctorEvent(
            @PathVariable Long sessionId,
            @RequestBody ProctorEventRequest request) {
        
        try {
            CertificationSession session = certificationService.getSessionById(sessionId);
            certificationService.recordProctorEvent(session, request.getEventType(), 
                    request.getDescription(), request.getConfidenceScore());
            
            return ResponseEntity.ok(ApiResponse.success("Proctor event recorded"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // New: Retrieve proctor events for a specific session
    @Operation(summary = "Get proctor events for session", description = "Retrieve all proctor events for a certification session")
    @GetMapping("/sessions/{sessionId}/proctor-events")
    public ResponseEntity<ApiResponse<List<ProctorEvent>>> getProctorEventsForSession(@PathVariable Long sessionId) {
        try {
            List<ProctorEvent> events = certificationService.getProctorEventsBySessionId(sessionId);
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    // New: Retrieve proctor events for a specific user
    @Operation(summary = "Get proctor events for user", description = "Retrieve all proctor events across certification sessions for a specific user")
    @GetMapping("/users/{userId}/proctor-events")
    public ResponseEntity<ApiResponse<List<ProctorEvent>>> getProctorEventsForUser(@PathVariable Long userId) {
        try {
            List<ProctorEvent> events = certificationService.getProctorEventsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    // New: Retrieve proctor events across all users, optionally filtered by userId, and include user names
    @Operation(summary = "Get proctor events for all users", description = "Retrieve all proctor events across all certification sessions and users. Optionally filter by userId, and include user names.")
    @GetMapping("/proctor-events")
    public ResponseEntity<ApiResponse<List<ProctorEventResponseDTO>>> getAllProctorEvents(@RequestParam(value = "userId", required = false) Long userId) {
        try {
            List<ProctorEvent> events = (userId != null)
                    ? certificationService.getProctorEventsByUserId(userId)
                    : certificationService.getAllProctorEvents();

            List<ProctorEventResponseDTO> payload = events.stream()
                    .map(this::toProctorEventResponseDTO)
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(payload));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    private ProctorEventResponseDTO toProctorEventResponseDTO(ProctorEvent event) {
        CertificationSession session = event.getSession();
        Long sessionId = (session != null) ? session.getId() : null;
        edtech.afrilingo.user.User user = (session != null) ? session.getUser() : null;
        Long uId = (user != null) ? user.getId() : null;
        String firstName = (user != null) ? user.getFirstName() : null;
        String lastName = (user != null) ? user.getLastName() : null;
        String email = (user != null) ? user.getEmail() : null;
        String userName;
        if (firstName != null && !firstName.isBlank()) {
            userName = (lastName != null && !lastName.isBlank()) ? (firstName + " " + lastName) : firstName;
        } else {
            userName = email; // fallback to email/username if no names
        }

        String eventTypeRaw = (event.getEventType() != null) ? event.getEventType().name() : null;
        String eventTypeName = eventTypeRaw; // placeholder for a human-friendly label if needed later

        return ProctorEventResponseDTO.builder()
                .id(event.getId())
                .eventType(eventTypeRaw)
                .eventTypeName(eventTypeName)
                .description(event.getDescription())
                .timestamp(event.getTimestamp())
                .confidenceScore(event.getConfidenceScore())
                .flagged(event.isFlagged())
                .sessionId(sessionId)
                .userId(uId)
                .userName(userName)
                .build();
    }

    private CertificateResponseDTO toCertificateResponseDTO(Certificate cert) {
        if (cert == null) return null;
        edtech.afrilingo.user.User user = cert.getUser();
        String firstName = (user != null) ? user.getFirstName() : null;
        String lastName = (user != null) ? user.getLastName() : null;
        String email = (user != null) ? user.getEmail() : null;
        String userName;
        if (firstName != null && !firstName.isBlank()) {
            userName = (lastName != null && !lastName.isBlank()) ? (firstName + " " + lastName) : firstName;
        } else {
            userName = email;
        }
        return CertificateResponseDTO.builder()
                .certificateId(cert.getCertificateId())
                .languageTested(cert.getLanguageTested())
                .proficiencyLevel(cert.getProficiencyLevel())
                .finalScore(cert.getFinalScore())
                .completedAt(cert.getCompletedAt())
                .issuedAt(cert.getIssuedAt())
                .certificateUrl(cert.getCertificateUrl())
                .verified(cert.isVerified())
                .userName(userName)
                .userEmail(email)
                .build();
    }

    // New: Retrieve proctor events for a specific user within a session (public)
    @Operation(summary = "Get proctor events for user in session", description = "Retrieve proctor events for a specific user within a specific session")
    @GetMapping("/sessions/{sessionId}/users/{userId}/proctor-events")
    public ResponseEntity<ApiResponse<List<ProctorEvent>>> getProctorEventsForUserInSession(@PathVariable Long sessionId, @PathVariable Long userId) {
        try {
            List<ProctorEvent> events = certificationService.getProctorEventsBySessionIdAndUserId(sessionId, userId);
            return ResponseEntity.ok(ApiResponse.success(events));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Get user certificates", description = "Get all certificates for current user")
    @GetMapping("/certificates")
    public ResponseEntity<ApiResponse<List<Certificate>>> getUserCertificates() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        List<Certificate> certificates = certificationService.getUserCertificates(currentUser);
        return ResponseEntity.ok(ApiResponse.success(certificates));
    }

    // New: Retrieve all certificates across all users
    @Operation(summary = "Get all certificates", description = "Retrieve all issued certificates across all users including user names")
    @GetMapping("/certificates/all")
    public ResponseEntity<ApiResponse<List<CertificateResponseDTO>>> getAllCertificates() {
        List<Certificate> certificates = certificationService.getAllCertificates();
        List<CertificateResponseDTO> payload = certificates.stream()
                .map(this::toCertificateResponseDTO)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(payload));
    }
    
    @Operation(summary = "Verify certificate", description = "Verify certificate by ID")
    @GetMapping("/certificates/{certificateId}/verify")
    public ResponseEntity<ApiResponse<Certificate>> verifyCertificate(@PathVariable String certificateId) {
        try {
            Certificate certificate = certificationService.getCertificateById(certificateId);
            return ResponseEntity.ok(ApiResponse.success(certificate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Clear ongoing sessions", description = "Clear ongoing certification sessions for testing (ADMIN only)")
    @DeleteMapping("/sessions/clear")
    public ResponseEntity<ApiResponse<String>> clearOngoingSessions() {
        try {
            certificationService.clearOngoingSessions();
            return ResponseEntity.ok(ApiResponse.success("All ongoing sessions cleared successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "Download certificate", description = "Download certificate PDF file")
    @GetMapping(value = "/certificates/download/{fileName}", produces = "application/pdf")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable String fileName) {
        try {
            // Get the configured certificate storage path from the PDF service
            String storagePath = certificationService.getCertificateStoragePath();
            Path base = Paths.get(storagePath).toAbsolutePath().normalize();
            Path path = base.resolve(fileName).normalize();
            
            // Prevent path traversal
            if (!path.startsWith(base)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            // Log the file path for debugging
            System.out.println("Attempting to download certificate from path: " + path.toString());
            
            if (!Files.exists(path)) {
                System.out.println("Certificate file not found at path: " + path.toString());
                return ResponseEntity.notFound().build();
            }
            
            // Create resource from file
            Resource resource = new FileSystemResource(path.toFile());
            long size = Files.size(path);
            
            // Set headers for file download
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Type", "application/pdf")
                    .contentLength(size)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(resource);
                    
        } catch (Exception e) {
            System.out.println("Error downloading certificate: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Helper endpoint to check if a certificate file exists
    @Operation(summary = "Check certificate file", description = "Check if certificate PDF file exists")
    @GetMapping("/certificates/check/{fileName}")
    public ResponseEntity<ApiResponse<String>> checkCertificateFile(@PathVariable String fileName) {
        try {
            // Get the configured certificate storage path from the PDF service
            String storagePath = certificationService.getCertificateStoragePath();
            String filePath = storagePath + "/" + fileName;
            Path path = Paths.get(filePath);
            
            if (Files.exists(path)) {
                long fileSize = Files.size(path);
                return ResponseEntity.ok(ApiResponse.success(
                    "Certificate file exists with size: " + fileSize + " bytes"));
            } else {
                return ResponseEntity.ok(ApiResponse.error(404, "Certificate file not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(500, "Error checking certificate: " + e.getMessage()));
        }
    }




}