package dk.runerne.indexingserver.elasticsearch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
        if (!indexOps.exists()) {
            indexOps.create();
            log.info("Elasticsearch-index 'media' created");
        } else {
            log.info("Elasticsearch-index 'media' exists already");
        }
    }
}