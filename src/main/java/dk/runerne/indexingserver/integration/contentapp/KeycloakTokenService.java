package dk.runerne.indexingserver.integration.contentapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class KeycloakTokenService {

    private final RestClient restClient = RestClient.create();

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUri;

    @Value("${spring.security.oauth2.client.registration.content-app.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.content-app.client-secret}")
    private String clientSecret;

    public String getAccessToken() {

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        Map<String, Object> response = restClient.post()
                                           .uri(tokenUri)
                                           .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                           .body(form)
                                           .retrieve()
                                           .body(Map.class);

        if (response == null || response.get("access_token") == null) {
            throw new IllegalStateException("No access_token returned from Keycloak");
        }

        return response.get("access_token").toString();
    }

}