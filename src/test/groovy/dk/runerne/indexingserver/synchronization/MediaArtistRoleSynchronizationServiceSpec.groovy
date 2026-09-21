package dk.runerne.indexingserver.synchronization

import spock.lang.Specification

class MediaArtistRoleSynchronizationServiceSpec extends Specification {

    MediaReindexService mediaReindexService = Mock()
    MediaArtistRoleSynchronizationService service = new MediaArtistRoleSynchronizationService(mediaReindexService)

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
        1 * mediaReindexService.reindexAffectedMedia("mediaArtists.mediaArtistRole.id", roleId)
    }

    def "synchronizeDeleted reindexes affected media via the correct ES field path"() {
        given:
        UUID roleId = UUID.randomUUID()

        when:
        service.synchronizeDeleted(roleId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("mediaArtists.mediaArtistRole.id", roleId)
    }

}
