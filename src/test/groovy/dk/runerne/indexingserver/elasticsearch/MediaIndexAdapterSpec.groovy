package dk.runerne.indexingserver.elasticsearch

import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocument
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import spock.lang.Specification

import java.util.stream.Stream

class MediaIndexAdapterSpec extends Specification {

    ElasticsearchOperations elasticsearchOperations = Mock()
    MediaIndexAdapter adapter = new MediaIndexAdapter(elasticsearchOperations)

    def "index saves the document via ElasticsearchOperations"() {
        given:
        MediaIndexDocument document = Mock()

        when:
        adapter.index(document)

        then:
        1 * elasticsearchOperations.save(document)
    }

    def "delete removes the document by string ID and index name"() {
        given:
        UUID mediaId = UUID.randomUUID()

        when:
        adapter.delete(mediaId)

        then:
        1 * elasticsearchOperations.delete(mediaId.toString(), { IndexCoordinates coords ->
            coords.indexNames == ["media"]
        })
    }

    def "findMediaIdsByField executes a criteria query and returns matching UUIDs"() {
        given:
        UUID entityId = UUID.randomUUID()
        UUID mediaId1 = UUID.randomUUID()
        UUID mediaId2 = UUID.randomUUID()

        SearchHit hit1 = Mock()
        hit1.getId() >> mediaId1.toString()
        SearchHit hit2 = Mock()
        hit2.getId() >> mediaId2.toString()

        SearchHits searchHits = Mock()
        searchHits.getTotalHits() >> 2L
        searchHits.stream() >> Stream.of(hit1, hit2)

        when:
        List<UUID> result = adapter.findMediaIdsByField("labels.id", entityId)

        then:
        1 * elasticsearchOperations.search(_ as CriteriaQuery, MediaIndexDocument) >> searchHits
        result == [mediaId1, mediaId2]
    }

    def "findMediaIdsByField returns empty list when no documents match"() {
        given:
        UUID entityId = UUID.randomUUID()

        SearchHits searchHits = Mock()
        searchHits.getTotalHits() >> 0L
        searchHits.stream() >> Stream.of()

        when:
        List<UUID> result = adapter.findMediaIdsByField("labels.id", entityId)

        then:
        1 * elasticsearchOperations.search(_ as CriteriaQuery, MediaIndexDocument) >> searchHits
        result.isEmpty()
    }

}
