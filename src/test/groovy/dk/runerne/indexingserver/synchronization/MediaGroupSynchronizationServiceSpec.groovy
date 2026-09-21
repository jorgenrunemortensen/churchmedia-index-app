package dk.runerne.indexingserver.synchronization

import spock.lang.Specification

class MediaGroupSynchronizationServiceSpec extends Specification {

    MediaReindexService mediaReindexService = Mock()
    MediaGroupSynchronizationService service = new MediaGroupSynchronizationService(mediaReindexService)

    def "synchronizeCreated is a no-op"() {
        when:
        service.synchronizeCreated(UUID.randomUUID())

        then:
        0 * mediaReindexService._
    }

    def "synchronizeUpdated reindexes affected media via the correct ES field path"() {
        given:
        UUID groupId = UUID.randomUUID()

        when:
        service.synchronizeUpdated(groupId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("mediaGroups.id", groupId)
    }

    def "synchronizeDeleted reindexes affected media via the correct ES field path"() {
        given:
        UUID groupId = UUID.randomUUID()

        when:
        service.synchronizeDeleted(groupId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("mediaGroups.id", groupId)
    }

}
