package dk.runerne.indexingserver.reindex;

import dk.runerne.indexing.contentclient.model.Media;
import dk.runerne.indexingserver.elasticsearch.MediaIndexPort;
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocument;
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocumentMapper;
import dk.runerne.indexingserver.integration.contentapp.ContentMediaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

/**
 * Service that performs a full rebuild of the Elasticsearch {@code media} index.
 *
 * <p>The rebuild deletes the existing index, recreates it with the correct field mapping,
 * then fetches all Media entities from content-app and indexes them.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndexRebuildService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ContentMediaService contentMediaService;
    private final MediaIndexPort mediaIndexPort;
    private final MediaIndexDocumentMapper mapper;

    /**
     * Deletes the existing {@code media} index, recreates it with the current field mapping,
     * and re-indexes all Media entities fetched from content-app.
     */
    public void rebuildIndex() {
        deleteAndRecreateIndex();
        indexAllMedia();
    }

    private void deleteAndRecreateIndex() {
        final IndexOperations indexOps = elasticsearchOperations.indexOps(MediaIndexDocument.class);
        log.info("Deleting Elasticsearch index 'media'");
        indexOps.delete();
        log.info("Creating Elasticsearch index 'media'");
        indexOps.create();
        log.info("Applying field mapping to Elasticsearch index 'media'");
        indexOps.putMapping();
    }

    private void indexAllMedia() {
        final List<Media> allMedia = contentMediaService.getAllMedia();
        log.info("Indexing {} media documents into Elasticsearch", allMedia.size());
        for (Media media : allMedia) {
            mediaIndexPort.index(mapper.toDocument(media));
        }
        log.info("Full index rebuild complete — {} documents indexed", allMedia.size());
    }

}
