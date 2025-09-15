package edtech.afrilingo.certification;

import org.springframework.beans.factory.annotation.Value;

import edtech.afrilingo.certification.dto.CertificateResponseDTO;
import edtech.afrilingo.question.Question;
import edtech.afrilingo.question.QuestionService;
import edtech.afrilingo.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificationService {
    
    private final CertificationSessionRepository sessionRepository;
    private final CertificateRepository certificateRepository;
    private final ProctorEventRepository proctorEventRepository;
    private final CertificationQuestionResponseRepository responseRepository;
    private final QuestionService questionService;
    private final CertificatePDFService pdfService;
    
    @Value("${afrilingo.certificates.storage-path:/tmp/certificates}")
    private String certificateStoragePath;
    private final ProctorAnalysisService proctorAnalysisService;
    
    @Transactional
    public CertificationSession initiateCertificationSession(User user, String languageCode, String testLevel) {
        // Validate user eligibility
        validateUserEligibility(user, languageCode);
        
        // Create session
        CertificationSession session = CertificationSession.builder()
                .sessionId(generateSessionId())
                .user(user)
                .languageCode(languageCode)
                .testLevel(testLevel)
                .startTime(LocalDateTime.now())
                .completed(false)
                .passed(false)
                .cameraVerified(false)
                .environmentVerified(false)
                .suspiciousActivityCount(0)
                .build();
        
        return sessionRepository.save(session);
    }
    
    @Transactional
    public void verifyTestEnvironment(Long sessionId, boolean cameraVerified, boolean environmentVerified) {
        CertificationSession session = getSessionById(sessionId);
        session.setCameraVerified(cameraVerified);
        session.setEnvironmentVerified(environmentVerified);
        
        // Log environment setup event
        recordProctorEvent(session, ProctorEventType.SESSION_START, 
                "Test environment verified. Camera: " + cameraVerified + ", Environment: " + environmentVerified);
        
        sessionRepository.save(session);
    }
    
    public List<Question> getCertificationQuestions(Long sessionId) {
        CertificationSession session = getSessionById(sessionId);
        
        // Get questions based on language and level
        List<Question> questions = questionService.getCertificationQuestions(
                session.getLanguageCode(), session.getTestLevel(), 50); // 50 questions for certification
        
        session.setTotalQuestions(questions.size());
        sessionRepository.save(session);
        
        return questions;
    }
    
    @Transactional
    public void recordAnswer(Long sessionId, Long questionId, Long selectedOptionId, String textAnswer, Integer score, long timeSpentMs) {
        CertificationSession session = getSessionById(sessionId);
        Question question = questionService.getQuestionById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        boolean isCorrect = false;
        if (score != null) {
            // For AI-graded questions, correctness is based on the score (70% or above is correct)
            isCorrect = score >= 70;
        } else if (selectedOptionId != null) {
            // For multiple-choice questions
            isCorrect = questionService.isAnswerCorrect(questionId, selectedOptionId);
        }

        CertificationQuestionResponse response = CertificationQuestionResponse.builder()
                .session(session)
                .question(question)
                .answeredAt(LocalDateTime.now())
                .timeSpentMs(timeSpentMs)
                .correct(isCorrect)
                .textAnswer(textAnswer)
                .score(score)
                .build();

        responseRepository.save(response);

        // Update session statistics
        if (isCorrect) {
            session.setCorrectAnswers(session.getCorrectAnswers() + 1);
        }

        sessionRepository.save(session);
    }
    
    @Transactional
    public CertificateResponseDTO completeSessionAndGenerateCertificate(Long sessionId) {
        CertificationSession session = getSessionById(sessionId);
        
        // Validate session completion
        if (session.isCompleted()) {
            throw new RuntimeException("Session already completed");
        }
        
        // Calculate final score
        int finalScore = calculateFinalScore(session);
        boolean passed = finalScore > 5; // Above 50% pass threshold
        
        // Update session
        session.setCompleted(true);
        session.setPassed(passed);
        session.setFinalScore(finalScore);
        session.setEndTime(LocalDateTime.now());
        
        // Analyze proctoring data
        AnalysisResult proctorAnalysis = proctorAnalysisService.analyzeSession(session);
        
        // Generate certificate if passed and no major violations
        Certificate certificate = null;
        if (passed && proctorAnalysis.isCertifiable()) {
            certificate = generateCertificate(session, finalScore);
        }
        
        // Record completion event
        recordProctorEvent(session, ProctorEventType.SESSION_END, 
                "Session completed. Score: " + finalScore + "%, Passed: " + passed);
        
        sessionRepository.save(session);
        
        // Convert to DTO to avoid circular references
        if (certificate != null) {
            return CertificateResponseDTO.builder()
                    .certificateId(certificate.getCertificateId())
                    .languageTested(certificate.getLanguageTested())
                    .proficiencyLevel(certificate.getProficiencyLevel())
                    .finalScore(certificate.getFinalScore())
                    .completedAt(certificate.getCompletedAt())
                    .issuedAt(certificate.getIssuedAt())
                    .certificateUrl(certificate.getCertificateUrl())
                    .certificatePdfBase64(certificate.getCertificatePdfBase64())
                    .verified(certificate.isVerified())
                    .userName(session.getUser().getFirstName() + " " + session.getUser().getLastName())
                    .userEmail(session.getUser().getEmail())
                    .build();
        } else {
            return null;
        }
    }
    
    private Certificate generateCertificate(CertificationSession session, int finalScore) {
        String certificateId = generateCertificateId();
        String proficiencyLevel = determineProficiencyLevel(finalScore);
        
        Certificate certificate = Certificate.builder()
                .certificateId(certificateId)
                .user(session.getUser())
                .languageTested(session.getLanguageCode())
                .proficiencyLevel(proficiencyLevel)
                .finalScore(finalScore)
                .completedAt(session.getEndTime())
                .issuedAt(LocalDateTime.now())
                .verified(true)
                .session(session)
                .build();
        
        certificate = certificateRepository.save(certificate);
        
        // Generate PDF certificate
        try {
            String pdfUrl = pdfService.generateCertificatePDF(certificate);
            certificate.setCertificateUrl(pdfUrl);
        } catch (Exception e) {
            log.warn("Certificate PDF generation failed, but certificate record will be saved: {}", e.getMessage());
            certificate.setCertificateUrl("pdf-generation-failed");
        }
        
        return certificateRepository.save(certificate);
    }
    
    public void recordProctorEvent(CertificationSession session, ProctorEventType eventType, 
                                   String description, double confidenceScore) {
        ProctorEvent event = ProctorEvent.builder()
                .session(session)
                .eventType(eventType)
                .description(description)
                .timestamp(LocalDateTime.now())
                .confidenceScore(confidenceScore)
                .flagged(confidenceScore > 0.8) // Flag high-confidence suspicious events
                .build();
        
        proctorEventRepository.save(event);
        
        // If this event signals that the session was terminated, ensure the
        // certification session is marked completed and closed (no certificate).
        if (eventType == ProctorEventType.SESSION_TERMINATED) {
            if (!session.isCompleted()) {
                session.setCompleted(true);
                session.setPassed(false);
                session.setFinalScore(0);
                session.setEndTime(LocalDateTime.now());
                // Record a session end proctor event as well for consistency
                ProctorEvent endEvent = ProctorEvent.builder()
                        .session(session)
                        .eventType(ProctorEventType.SESSION_END)
                        .description("Session terminated: " + description)
                        .timestamp(LocalDateTime.now())
                        .confidenceScore(0.0)
                        .flagged(false)
                        .build();
                proctorEventRepository.save(endEvent);
                sessionRepository.save(session);
            }
            return;
        }

        // Update session suspicious activity count for flagged events
        if (event.isFlagged()) {
            session.setSuspiciousActivityCount(session.getSuspiciousActivityCount() + 1);
            sessionRepository.save(session);
        }
    }
    
    public void recordProctorEvent(CertificationSession session, ProctorEventType eventType, String description) {
        recordProctorEvent(session, eventType, description, 0.0);
    }
    
    // Helper methods
    private void validateUserEligibility(User user, String languageCode) {
        // Check if user has completed prerequisite
        // Check if user has any ongoing certification sessions
        Optional<CertificationSession> ongoingSession = sessionRepository
                .findByUserAndCompletedFalse(user);
        
        if (ongoingSession.isPresent()) {
            throw new RuntimeException("User has an ongoing certification session");
        }
    }
    
    public CertificationSession getSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Certification session not found"));
    }
    
    private int calculateFinalScore(CertificationSession session) {
        if (session.getTotalQuestions() == 0) return 0;
        
        // Calculate score based on points earned vs total possible points
        List<CertificationQuestionResponse> responses = responseRepository.findBySessionWithQuestionOrderByAnsweredAtAsc(session);
        
        int totalPossiblePoints = 0;
        int earnedPoints = 0;
        
        for (CertificationQuestionResponse response : responses) {
            Question question = response.getQuestion();
            
            // With JOIN FETCH, question should not be null, but keep safety check
            if (question != null) {
                totalPossiblePoints += question.getPoints();
                
                if (response.isCorrect()) {
                    earnedPoints += question.getPoints();
                }
            } else {
                // Fallback: assume 1 point if question is null (shouldn't happen with JOIN FETCH)
                totalPossiblePoints += 1;
                if (response.isCorrect()) {
                    earnedPoints += 1;
                }
                log.error("Question is null for response ID: {} despite JOIN FETCH", response.getId());
            }
        }
        
        if (totalPossiblePoints == 0) return 0;
        return (int) Math.round((double) earnedPoints / totalPossiblePoints * 100);
    }
    
    private String determineProficiencyLevel(int score) {
        if (score >= 85) return "ADVANCED";
        if (score >= 70) return "INTERMEDIATE";
        if (score > 50) return "BEGINNER";
        return "BEGINNER"; // Even if score <= 50, still assign BEGINNER level for certificate generation
    }
    
    private String generateSessionId() {
        return "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateCertificateId() {
        return "AFL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    public List<Certificate> getUserCertificates(User user) {
        return certificateRepository.findByUserOrderByIssuedAtDesc(user);
    }
    
    public Certificate getCertificateById(String certificateId) {
        return certificateRepository.findByCertificateId(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }
    
    @Transactional
    public void clearOngoingSessions() {
        List<CertificationSession> ongoingSessions = sessionRepository.findByCompletedFalse();
        sessionRepository.deleteAll(ongoingSessions);
        log.info("Cleared {} ongoing certification sessions", ongoingSessions.size());
    }
    
    /**
     * Returns the configured certificate storage path
     * @return The path where certificate PDFs are stored
     */
    public String getCertificateStoragePath() {
        return certificateStoragePath;
    }

    /**
     * Terminates a certification session without generating a certificate.
     * This is used when a user leaves/abandons the test or an admin/proctor forcibly ends it.
     * The session will be marked completed=false->true, passed=false, finalScore=0 and endTime set.
     * A SESSION_TERMINATED proctor event will be recorded with the provided reason.
     */
    @Transactional
    public void terminateSession(Long sessionId, String reason) {
        CertificationSession session = getSessionById(sessionId);
        if (session.isCompleted()) {
            throw new RuntimeException("Session already completed");
        }
        String desc = (reason == null || reason.isBlank()) ? "Session terminated by system" : reason.trim();
        // Record a termination event; service logic will mark the session completed without certificate
        recordProctorEvent(session, ProctorEventType.SESSION_TERMINATED, desc, 1.0);
    }

    // New: Retrieve proctor events for a specific user
    public List<ProctorEvent> getProctorEventsByUserId(Long userId) {
        return proctorEventRepository.findBySession_User_IdOrderByTimestampAsc(userId);
    }

    // New: Retrieve proctor events for a specific session
    public List<ProctorEvent> getProctorEventsBySessionId(Long sessionId) {
        return proctorEventRepository.findBySession_IdOrderByTimestampAsc(sessionId);
    }

    // New: Retrieve proctor events for all users
    public List<ProctorEvent> getAllProctorEvents() {
        return proctorEventRepository.findAllByOrderByTimestampAsc();
    }

    // New: Retrieve proctor events filtered by session and user
    public List<ProctorEvent> getProctorEventsBySessionIdAndUserId(Long sessionId, Long userId) {
        return proctorEventRepository.findBySession_IdAndSession_User_IdOrderByTimestampAsc(sessionId, userId);
    }

    // New: Retrieve all certificates across users, newest first
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAllByOrderByIssuedAtDesc();
    }
}