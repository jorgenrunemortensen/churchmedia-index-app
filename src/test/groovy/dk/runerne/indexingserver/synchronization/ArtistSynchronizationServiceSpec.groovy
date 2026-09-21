package dk.runerne.indexingserver.synchronization

import spock.lang.Specification

class ArtistSynchronizationServiceSpec extends Specification {

    MediaReindexService mediaReindexService = Mock()
    ArtistSynchronizationService service = new ArtistSynchronizationService(mediaReindexService)

    def "synchronizeCreated is a no-op"() {
        when:
        service.synchronizeCreated(UUID.randomUUID())

        then:
        0 * mediaReindexService._
    }

    def "synchronizeUpdated reindexes affected media via the correct ES field path"() {
        given:
        UUID artistId = UUID.randomUUID()

        when:
        service.synchronizeUpdated(artistId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("mediaArtists.artist.id", artistId)
    }

    def "synchronizeDeleted reindexes affected media via the correct ES field path"() {
        given:
        UUID artistId = UUID.randomUUID()

        when:
        service.synchronizeDeleted(artistId)

        then:
        1 * mediaReindexService.reindexAffectedMedia("mediaArtists.artist.id", artistId)
    }

}
