package edtech.afrilingo.certification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIInvigilationService {
    
    private final RestTemplate restTemplate;
    private final ProctorEventRepository proctorEventRepository;
    
    @Value("${afrilingo.ai.vision-api-url:https://api.openai.com/v1/chat/completions}")
    private String visionApiUrl;
    
    @Value("${afrilingo.ai.api-key:}")
    private String aiApiKey;
    
    public void analyzeVideoFrame(Long sessionId, String base64Image, CertificationSession session) {
        try {
            // Prepare the API request for AI analysis
            Map<String, Object> request = buildVisionAnalysisRequest(base64Image);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiApiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            // Call AI service
            ResponseEntity<Map> response = restTemplate.exchange(
                visionApiUrl, HttpMethod.POST, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                processAIResponse(response.getBody(), session);
            }
            
        } catch (Exception e) {
            log.error("Error analyzing video frame for session {}: {}", sessionId, e.getMessage());
            // Continue without AI analysis if service fails
        }
    }
    
    private Map<String, Object> buildVisionAnalysisRequest(String base64Image) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-4-vision-preview");
        request.put("max_tokens", 300);
        
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        
        Map<String, Object> content = new HashMap<>();
        content.put("type", "text");
        content.put("text", """
            Analyze this webcam image for a certification exam and detect:
            1. Number of people visible (should be exactly 1)
            2. Eye gaze direction (should be looking at screen)
            3. Presence of phones, books, or notes
            4. Suspicious hand movements
            5. Overall environment appropriateness
            
            Respond in JSON format:
            {
                "face_count": number,
                "looking_at_screen": boolean,
                "prohibited_objects": ["list", "of", "objects"],
                "suspicious_activity": boolean,
                "confidence_score": 0.0-1.0,
                "description": "brief description"
            }
            """);
        
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        imageUrl.put("url", "data:image/jpeg;base64," + base64Image);
        imageContent.put("image_url", imageUrl);
        
        message.put("content", new Object[]{content, imageContent});
        request.put("messages", new Object[]{message});
        
        return request;
    }
    
    private void processAIResponse(Map<String, Object> response, CertificationSession session) {
        try {
            // Extract analysis results from AI response
            Map<String, Object> choices = (Map<String, Object>) ((Object[]) response.get("choices"))[0];
            Map<String, Object> message = (Map<String, Object>) choices.get("message");
            String content = (String) message.get("content");
            
            // Parse the JSON response (simplified - in production, use proper JSON parsing)
            if (content.contains("\"face_count\": 0")) {
                recordProctorEvent(session, ProctorEventType.FACE_NOT_DETECTED, 
                    "No face detected in frame", 0.9);
            }
            
            if (content.contains("\"face_count\": 2") || content.contains("\"face_count\": 3")) {
                recordProctorEvent(session, ProctorEventType.MULTIPLE_FACES_DETECTED, 
                    "Multiple faces detected", 0.95);
            }
            
            if (content.contains("\"looking_at_screen\": false")) {
                recordProctorEvent(session, ProctorEventType.EXTENDED_LOOK_AWAY,
                    "User not looking at screen", 0.8);
            }
            
            if (content.contains("\"prohibited_objects\"") && !content.contains("[]")) {
                recordProctorEvent(session, ProctorEventType.PROHIBITED_OBJECT_DETECTED, 
                    "Prohibited objects detected", 0.85);
            }
            
            if (content.contains("\"suspicious_activity\": true")) {
                recordProctorEvent(session, ProctorEventType.SUSPICIOUS_MOVEMENT, 
                    "Suspicious activity detected", 0.8);
            }
            
        } catch (Exception e) {
            log.error("Error processing AI response: {}", e.getMessage());
        }
    }
    
    private void recordProctorEvent(CertificationSession session, ProctorEventType eventType, 
                                   String description, double confidence) {
        ProctorEvent event = ProctorEvent.builder()
                .session(session)
                .eventType(eventType)
                .description(description)
                .confidenceScore(confidence)
                .flagged(confidence > 0.75)
                .build();
        
        proctorEventRepository.save(event);
    }
}
