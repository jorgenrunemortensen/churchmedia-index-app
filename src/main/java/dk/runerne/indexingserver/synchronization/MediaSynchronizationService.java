package dk.runerne.indexingserver.synchronization;

import dk.runerne.indexing.contentclient.model.Media;
import dk.runerne.indexingserver.elasticsearch.MediaIndexPort;
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocumentMapper;
import dk.runerne.indexingserver.integration.contentapp.ContentMediaService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Synchronizes Media entities from content-app to the Elasticsearch {@code media} index.
 *
 * <p>On create and update, the full Media document (including all related entities) is fetched
 * from content-app and indexed. On delete, the document is removed from the index.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MediaSynchronizationService implements Synchronizer {

    private final ContentMediaService contentMediaService;
    private final MediaIndexPort mediaIndexPort;
    private final MediaIndexDocumentMapper mapper;

    @Override
    public void synchronizeCreated(UUID mediaId) {
        indexMedia(mediaId);
        log.debug("Indexed created media {}", mediaId);
    }

    @Override
    public void synchronizeUpdated(UUID mediaId) {
        indexMedia(mediaId);
        log.debug("Indexed updated media {}", mediaId);
    }

    @Override
    public void synchronizeDeleted(UUID mediaId) {
        mediaIndexPort.delete(mediaId);
        log.debug("Deleted media {} from index", mediaId);
    }

    private void indexMedia(UUID mediaId) {
        final Media media = contentMediaService.getById(mediaId);
        mediaIndexPort.index(mapper.toDocument(media));
    }

}
