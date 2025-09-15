package edtech.afrilingo.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import edtech.afrilingo.config.OAuth2Properties;
import edtech.afrilingo.dto.GoogleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class GoogleTokenVerificationService {

    private final OAuth2Properties oAuth2Properties;

    public GoogleUser verifyToken(String idTokenString) throws Exception {
        // Create verifier with both Android and iOS client IDs
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Arrays.asList(
                        oAuth2Properties.getAndroid().getClientId(),
                        oAuth2Properties.getIos().getClientId(),
                        oAuth2Properties.getBackend().getClientId()
                ))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            return GoogleUser.builder()
                    .email(payload.getEmail())
                    .firstName((String) payload.get("given_name"))
                    .lastName((String) payload.get("family_name"))
                    .picture((String) payload.get("picture"))
                    .emailVerified(payload.getEmailVerified())
                    .build();
        } else {
            throw new SecurityException("Invalid ID token");
        }
    }
}