package dk.runerne.indexingserver.synchronization

import spock.lang.Specification

class LabelSynchronizationServiceSpec extends Specification {

    MediaReindexService mediaReindexService = Mock()
    LabelSynchronizationService service = new LabelSynchronizationService(mediaReindexService)

    def "synchronizeCreated is a no-op"() {
        when:
        service.synchronizeCreated(UUID.randomUUID())

        then:
        0 * mediaReindexService._
    }

    def "synchronizeUpdated reindexes affected media via the correct ES field path"() {
        given:
        UUID labelId = UUID.randomUUID()

        when:
        service.synchronizeUpdated(labelId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("labels.id", labelId)
    }

    def "synchronizeDeleted reindexes affected media via the correct ES field path"() {
        given:
        UUID labelId = UUID.randomUUID()

        when:
        service.synchronizeDeleted(labelId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("labels.id", labelId)
    }

}
