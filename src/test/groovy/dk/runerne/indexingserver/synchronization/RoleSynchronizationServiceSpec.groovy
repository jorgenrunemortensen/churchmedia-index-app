package dk.runerne.indexingserver.synchronization

import spock.lang.Specification

class RoleSynchronizationServiceSpec extends Specification {

    MediaReindexService mediaReindexService = Mock()
    RoleSynchronizationService service = new RoleSynchronizationService(mediaReindexService)

    def "synchronizeCreated is a no-op"() {
        when:
        service.synchronizeCreated(UUID.randomUUID())

        then:
        0 * mediaReindexService._
    }

    def "synchronizeUpdated reindexes affected media via the correct ES field path"() {
        given:
        UUID roleId = UUID.randomUUID()

        when:
        service.synchronizeUpdated(roleId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("mediaGroups.roles.id", roleId)
    }

    def "synchronizeDeleted reindexes affected media via the correct ES field path"() {
        given:
        UUID roleId = UUID.randomUUID()

        when:
        service.synchronizeDeleted(roleId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("mediaGroups.roles.id", roleId)
    }

}
