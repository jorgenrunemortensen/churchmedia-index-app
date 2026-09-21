package dk.runerne.indexingserver.synchronization;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Synchronizes Artist-related changes to the Elasticsearch {@code media} index.
 *
 * <p>Because artists are embedded in Media documents, any change to an Artist requires
 * reindexing all Media documents that reference that artist.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistSynchronizationService implements Synchronizer {

    private static final String ES_FIELD_PATH = "mediaArtists.artist.id";

    private final MediaReindexService mediaReindexService;

    @Override
    public void synchronizeCreated(UUID id) {
        log.trace("Artist {} created — no reindex needed as no existing Media can reference a new entity", id);
    }

    @Override
    public void synchronizeUpdated(UUID id) {
        log.debug("Reindexing media affected by updated artist {}", id);
        mediaReindexService.reindexAffectedMedia(ES_FIELD_PATH, id);
    }

    @Override
    public void synchronizeDeleted(UUID id) {
        log.debug("Reindexing media affected by deleted artist {}", id);
        mediaReindexService.reindexAffectedMedia(ES_FIELD_PATH, id);
    }

}
