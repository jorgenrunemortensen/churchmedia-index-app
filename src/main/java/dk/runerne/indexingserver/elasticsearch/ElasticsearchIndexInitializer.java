package dk.runerne.indexingserver.elasticsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.elasticsearch.UncategorizedElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeIndex() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of("media"));
        try {
            boolean created = indexOps.create();
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
        }
    }

}
