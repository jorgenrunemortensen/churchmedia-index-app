package dk.runerne.indexingserver.synchronization;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Synchronizes MediaGroup-related changes to the Elasticsearch {@code media} index.
 *
 * <p>Because media groups are embedded in Media documents, any change to a MediaGroup requires
 * reindexing all Media documents that reference that group.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MediaGroupSynchronizationService implements Synchronizer {

    private static final String ES_FIELD_PATH = "mediaGroups.id";

    private final MediaReindexService mediaReindexService;

    @Override
    public void synchronizeCreated(UUID id) {
        log.trace("MediaGroup {} created — no reindex needed as no existing Media can reference a new entity", id);
    }

    @Override
    public void synchronizeUpdated(UUID id) {
        log.debug("Reindexing media affected by updated media group {}", id);
        mediaReindexService.reindexAffectedMedia(ES_FIELD_PATH, id);
    }

    @Override
    public void synchronizeDeleted(UUID id) {
        log.debug("Reindexing media affected by deleted media group {}", id);
        mediaReindexService.reindexAffectedMedia(ES_FIELD_PATH, id);
    }

}
