package dk.runerne.indexingserver.reindex

import dk.runerne.indexing.contentclient.model.Media
import dk.runerne.indexingserver.elasticsearch.MediaIndexPort
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocument
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocumentMapper
import dk.runerne.indexingserver.integration.contentapp.ContentMediaService
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.IndexOperations
import spock.lang.Specification

class IndexRebuildServiceSpec extends Specification {

    ElasticsearchOperations elasticsearchOperations = Mock()
    IndexOperations indexOps = Mock()
    ContentMediaService contentMediaService = Mock()
    MediaIndexPort mediaIndexPort = Mock()
    MediaIndexDocumentMapper mapper = Mock()
    IndexRebuildService service = new IndexRebuildService(elasticsearchOperations, contentMediaService, mediaIndexPort, mapper)

    def setup() {
        elasticsearchOperations.indexOps(MediaIndexDocument.class) >> indexOps
    }

    def "rebuildIndex deletes, creates and puts mapping before indexing"() {
        given:
        contentMediaService.getAllMedia() >> []

        when:
        service.rebuildIndex()

        then:
        1 * indexOps.delete()

        then:
        1 * indexOps.create()

        then:
        1 * indexOps.putMapping()
    }

    def "rebuildIndex indexes all media fetched from content-app"() {
        given:
        UUID id1 = UUID.randomUUID()
        UUID id2 = UUID.randomUUID()
        Media media1 = new Media().id(id1)
        Media media2 = new Media().id(id2)
        MediaIndexDocument doc1 = Mock()
        MediaIndexDocument doc2 = Mock()

        when:
        service.rebuildIndex()

        then:
        contentMediaService.getAllMedia() >> [media1, media2]
        mapper.toDocument(media1) >> doc1
        mapper.toDocument(media2) >> doc2
        1 * mediaIndexPort.index(doc1)
        1 * mediaIndexPort.index(doc2)
    }

    def "rebuildIndex still deletes and recreates index even when content-app returns no media"() {
        given:
        contentMediaService.getAllMedia() >> []

        when:
        service.rebuildIndex()

        then:
        1 * indexOps.delete()
        1 * indexOps.create()
        1 * indexOps.putMapping()
        0 * mediaIndexPort.index(_)
    }

}
