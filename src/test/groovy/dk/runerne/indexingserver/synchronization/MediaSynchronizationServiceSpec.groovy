package dk.runerne.indexingserver.synchronization

import dk.runerne.indexing.contentclient.model.Media
import dk.runerne.indexingserver.elasticsearch.MediaIndexPort
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocument
import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocumentMapper
import dk.runerne.indexingserver.integration.contentapp.ContentMediaService
import spock.lang.Specification

class MediaSynchronizationServiceSpec extends Specification {

    ContentMediaService contentMediaService = Mock()
    MediaIndexPort mediaIndexPort = Mock()
    MediaIndexDocumentMapper mapper = Mock()
    MediaSynchronizationService service = new MediaSynchronizationService(contentMediaService, mediaIndexPort, mapper)

    def "synchronizeCreated fetches media, maps and indexes it"() {
        given:
        UUID mediaId = UUID.randomUUID()
        Media media = new Media().id(mediaId)
        MediaIndexDocument document = Mock()

        when:
        service.synchronizeCreated(mediaId)

        then:
        1 * contentMediaService.getById(mediaId) >> media
        1 * mapper.toDocument(media) >> document
        1 * mediaIndexPort.index(document)
    }

    def "synchronizeUpdated fetches media, maps and indexes it"() {
        given:
        UUID mediaId = UUID.randomUUID()
        Media media = new Media().id(mediaId)
        MediaIndexDocument document = Mock()

        when:
        service.synchronizeUpdated(mediaId)

        then:
        1 * contentMediaService.getById(mediaId) >> media
        1 * mapper.toDocument(media) >> document
        1 * mediaIndexPort.index(document)
    }

    def "synchronizeDeleted deletes document from index by ID"() {
        given:
        UUID mediaId = UUID.randomUUID()

        when:
        service.synchronizeDeleted(mediaId)

        then:
        1 * mediaIndexPort.delete(mediaId)
        0 * contentMediaService._
    }

}
