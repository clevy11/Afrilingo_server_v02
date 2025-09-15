package edtech.afrilingo.certification;

import lombok.Data;

@Data
public class ProctorEventRequest {
    private ProctorEventType eventType;
    private String description;
    private double confidenceScore;
    private String videoSnippetUrl;
}