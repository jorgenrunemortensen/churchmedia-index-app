package dk.runerne.indexingserver.synchronization

import dk.runerne.indexing.contentclient.model.Media
import dk.runerne.indexingserver.elasticsearch.MediaIndexPort
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocument
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocumentMapper
import dk.runerne.indexingserver.integration.contentapp.ContentMediaService
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Specification

class MediaReindexServiceSpec extends Specification {

    MediaIndexPort mediaIndexPort = Mock()
    ContentMediaService contentMediaService = Mock()
    MediaIndexDocumentMapper mapper = Mock()
    MediaReindexService service = new MediaReindexService(mediaIndexPort, contentMediaService, mapper)

    def "reindexAffectedMedia fetches and re-indexes all matching media documents"() {
        given:
        UUID entityId = UUID.randomUUID()
        UUID mediaId1 = UUID.randomUUID()
        UUID mediaId2 = UUID.randomUUID()
        Media media1 = new Media().id(mediaId1)
        Media media2 = new Media().id(mediaId2)
        MediaIndexDocument doc1 = Mock()
        MediaIndexDocument doc2 = Mock()

        when:
        service.reindexAffectedMedia("labels.id", entityId)

        then:
        1 * mediaIndexPort.findMediaIdsByField("labels.id", entityId) >> [mediaId1, mediaId2]
        1 * contentMediaService.getById(mediaId1) >> media1
        1 * mapper.toDocument(media1) >> doc1
        1 * mediaIndexPort.index(doc1)
        1 * contentMediaService.getById(mediaId2) >> media2
        1 * mapper.toDocument(media2) >> doc2
        1 * mediaIndexPort.index(doc2)
    }

    def "reindexAffectedMedia does nothing when no media documents are found"() {
        given:
        UUID entityId = UUID.randomUUID()

        when:
        service.reindexAffectedMedia("labels.id", entityId)

        then:
        1 * mediaIndexPort.findMediaIdsByField("labels.id", entityId) >> []
        0 * contentMediaService._
        0 * mapper._
    }

    def "reindexAffectedMedia skips media that no longer exists in content-app"() {
        given:
        UUID entityId = UUID.randomUUID()
        UUID missingMediaId = UUID.randomUUID()
        UUID presentMediaId = UUID.randomUUID()
        Media presentMedia = new Media().id(presentMediaId)
        MediaIndexDocument doc = Mock()

        when:
        service.reindexAffectedMedia("labels.id", entityId)

        then:
        1 * mediaIndexPort.findMediaIdsByField("labels.id", entityId) >> [missingMediaId, presentMediaId]
        1 * contentMediaService.getById(missingMediaId) >> { throw new HttpClientErrorException.NotFound("not found", null, null, null, null) }
        1 * contentMediaService.getById(presentMediaId) >> presentMedia
        1 * mapper.toDocument(presentMedia) >> doc
        1 * mediaIndexPort.index(doc)
    }

}
