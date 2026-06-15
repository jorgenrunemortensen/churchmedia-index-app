package dk.runerne.indexingserver.integration.contentapp;

import dk.runerne.indexing.contentclient.api.MediaManagementApi;
import dk.runerne.indexing.contentclient.invoker.ApiClient;
import dk.runerne.indexing.contentclient.model.Media;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContentMediaService {

    private final MediaManagementApi mediaManagementApi;
    private final ApiClient apiClient;
    private final KeycloakTokenService tokenService;

    public ContentMediaService(
        MediaManagementApi mediaManagementApi,
        KeycloakTokenService tokenService
    ) {
        this.mediaManagementApi = mediaManagementApi;
        this.apiClient = this.mediaManagementApi.getApiClient();
        this.tokenService = tokenService;
    }

    public Media getById(UUID id) {
        return getMediaApiWithAuth().getById1(id);
    }

    private MediaManagementApi getMediaApiWithAuth() {
        var token = tokenService.getAccessToken();
        apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        return mediaManagementApi;
    }

}