package dk.runerne.indexingserver.integration.contentapp;

import dk.runerne.indexing.contentclient.api.MediaManagementApi;
import dk.runerne.indexing.contentclient.invoker.ApiClient;
import dk.runerne.indexing.contentclient.model.CursorPagedResponseMedia;
import dk.runerne.indexing.contentclient.model.Media;
import dk.runerne.indexing.contentclient.model.Sort;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Adapter for fetching Media entities from content-app via the generated REST client.
 */
@Service
@Slf4j
public class ContentMediaService {

    private static final int PAGE_SIZE = 100;

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

    /**
     * Fetches a single Media entity by its ID.
     *
     * @param id the Media ID
     * @return the Media entity
     */
    public Media getById(UUID id) {
        return getMediaApiWithAuth().getById1(id);
    }

    /**
     * Fetches all Media entities from content-app by paging through the cursor-based list endpoint.
     *
     * @return an ordered list of all Media entities
     */
    public List<Media> getAllMedia() {
        final List<Media> result = new ArrayList<>();
        String cursor = null;
        do {
            final CursorPagedResponseMedia page = getMediaApiWithAuth().list1(new Sort(), cursor, PAGE_SIZE);
            if (page.getItems() != null) {
                result.addAll(page.getItems());
            }
            cursor = page.getNextCursor();
            log.debug("Fetched {} media from content-app (nextCursor present: {})",
                    page.getItems() != null ? page.getItems().size() : 0, cursor != null);
        } while (cursor != null);
        return result;
    }

    private MediaManagementApi getMediaApiWithAuth() {
        final String token = tokenService.getAccessToken();
        apiClient.addDefaultHeader("Authorization", "Bearer " + token);
        return mediaManagementApi;
    }

}
