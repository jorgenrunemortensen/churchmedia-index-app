package dk.runerne.indexingserver.synchronization;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Synchronizes Role-related changes to the Elasticsearch {@code media} index.
 *
 * <p>Because roles are embedded within media groups inside Media documents, any change to a Role
 * requires reindexing all Media documents that reference that role through their media groups.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleSynchronizationService implements Synchronizer {

    private static final String ES_FIELD_PATH = "mediaGroups.roles.id";

    private final MediaReindexService mediaReindexService;

    @Override
    public void synchronizeCreated(UUID id) {
        log.trace("Role {} created — no reindex needed as no existing Media can reference a new entity", id);
    }

    @Override
    public void synchronizeUpdated(UUID id) {
        log.debug("Reindexing media affected by updated role {}", id);
        mediaReindexService.reindexAffectedMedia(ES_FIELD_PATH, id);
    }

    @Override
    public void synchronizeDeleted(UUID id) {
        log.debug("Reindexing media affected by deleted role {}", id);
        mediaReindexService.reindexAffectedMedia(ES_FIELD_PATH, id);
    }

}
