package edtech.afrilingo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUser {
    private String email;
    private String firstName;
    private String lastName;
    private String picture;
    private Boolean emailVerified;
}