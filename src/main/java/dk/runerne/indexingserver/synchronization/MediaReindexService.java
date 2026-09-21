package dk.runerne.indexingserver.synchronization;

import dk.runerne.indexing.contentclient.model.Media;
import dk.runerne.indexingserver.elasticsearch.MediaIndexPort;
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocumentMapper;
import dk.runerne.indexingserver.integration.contentapp.ContentMediaService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Shared service used by all non-Media synchronizers to find and reindex the Media documents
 * in Elasticsearch that are affected by a change to a related entity.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MediaReindexService {

    private final MediaIndexPort mediaIndexPort;
    private final ContentMediaService contentMediaService;
    private final MediaIndexDocumentMapper mapper;

    /**
     * Finds all Media documents in Elasticsearch that reference the given entity at {@code esFieldPath},
     * then re-fetches each from content-app and re-indexes it.
     *
     * <p>If a Media document no longer exists in content-app (e.g. it was deleted concurrently),
     * that document is skipped and a warning is logged.</p>
     *
     * @param esFieldPath dotted ES field path where the entity ID is stored (e.g. {@code "labels.id"})
     * @param entityId    the ID of the changed or deleted related entity
     */
    public void reindexAffectedMedia(String esFieldPath, UUID entityId) {
        final List<UUID> affectedIds = mediaIndexPort.findMediaIdsByField(esFieldPath, entityId);
        if (affectedIds.isEmpty()) {
            log.trace("No media documents found referencing {} at field '{}' — skipping reindex", entityId, esFieldPath);
            return;
        }
        log.debug("Re-indexing {} media document(s) affected by change to entity {} at field '{}'",
                affectedIds.size(), entityId, esFieldPath);
        for (UUID mediaId : affectedIds) {
            reindexSingle(mediaId);
        }
    }

    private void reindexSingle(UUID mediaId) {
        try {
            final Media media = contentMediaService.getById(mediaId);
            mediaIndexPort.index(mapper.toDocument(media));
            log.debug("Re-indexed media {}", mediaId);
        } catch (HttpClientErrorException.NotFound _) {
            log.warn("Media {} not found in content-app during reindex — skipping", mediaId);
        }
    }

}
