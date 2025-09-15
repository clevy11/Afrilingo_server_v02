package edtech.afrilingo.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenDTO {
    private String token;
    private String deviceType; // "ANDROID" or "IOS"
    private String deviceId;
    private String appVersion;
}