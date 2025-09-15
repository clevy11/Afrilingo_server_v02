package edtech.afrilingo.certification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProctorEventResponseDTO {
    private Long id;
    private String eventType;        // raw enum name
    private String eventTypeName;    // human-friendly name (currently sames as enum or can be customized later)
    private String description;
    private LocalDateTime timestamp;
    private double confidenceScore;
    private boolean flagged;

    private Long sessionId;
    private Long userId;
    private String userName;         // FirstName LastName or email if names missing
}
