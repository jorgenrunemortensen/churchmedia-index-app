package dk.runerne.indexingserver.elasticsearch;

import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * Creates the {@code media} Elasticsearch index on startup if it does not already exist,
     * then applies the field mapping derived from {@link MediaIndexDocument} annotations.
     *
     * <p>The mapping is applied on every startup so that new fields added to the document class
     * are registered in Elasticsearch after redeployment. Existing field mappings are not affected
     * because Elasticsearch mappings are additive.</p>
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeIndex() {
        final IndexOperations indexOps = elasticsearchOperations.indexOps(MediaIndexDocument.class);
        try {
            final boolean created = indexOps.create();
            if (created) {
                log.info("Elasticsearch index 'media' created");
            } else {
                log.info("Elasticsearch index 'media' already exists");
            }
        } catch (UncategorizedElasticsearchException e) {
            if (e.getMessage() != null && e.getMessage().contains("resource_already_exists_exception")) {
                log.info("Elasticsearch index 'media' already exists");
            } else {
                log.warn("Unexpected Elasticsearch exception initialising index 'media': {}", e.getMessage());
            }
        } catch (DataAccessResourceFailureException e) {
            log.warn("Could not initialise Elasticsearch index 'media' — the server likely predates Elasticsearch 8.x which is required by the elasticsearch-java 9.x client: {}", e.getMessage());
            return;
        }
        applyMapping(indexOps);
    }

    private void applyMapping(IndexOperations indexOps) {
        try {
            indexOps.putMapping();
            log.info("Elasticsearch field mapping for index 'media' applied");
        } catch (Exception e) {
            log.warn("Could not apply Elasticsearch field mapping for index 'media': {}", e.getMessage());
        }
    }

}
