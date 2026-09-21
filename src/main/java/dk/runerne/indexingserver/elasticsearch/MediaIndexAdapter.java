package dk.runerne.indexingserver.elasticsearch;

import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocument;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

/**
 * Adapter implementing {@link MediaIndexPort} using Spring Data Elasticsearch.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MediaIndexAdapter implements MediaIndexPort {

    private static final int MAX_SEARCH_RESULTS = 10_000;

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void index(MediaIndexDocument document) {
        elasticsearchOperations.save(document);
    }

    @Override
    public void delete(UUID mediaId) {
        elasticsearchOperations.delete(mediaId.toString(), IndexCoordinates.of("media"));
    }

    @Override
    public List<UUID> findMediaIdsByField(String fieldPath, UUID entityId) {
        final CriteriaQuery query = new CriteriaQuery(Criteria.where(fieldPath).is(entityId.toString()));
        query.setMaxResults(MAX_SEARCH_RESULTS);
        final SearchHits<MediaIndexDocument> hits = elasticsearchOperations.search(query, MediaIndexDocument.class);
        if (hits.getTotalHits() > MAX_SEARCH_RESULTS) {
            log.warn("Found {} media documents referencing {} at field '{}' — only the first {} will be reindexed",
                    hits.getTotalHits(), entityId, fieldPath, MAX_SEARCH_RESULTS);
        }
        return hits.stream()
                .map(hit -> UUID.fromString(hit.getId()))
                .toList();
    }

}
