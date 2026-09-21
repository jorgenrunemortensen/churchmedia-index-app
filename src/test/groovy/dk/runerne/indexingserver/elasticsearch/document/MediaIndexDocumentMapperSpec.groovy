package dk.runerne.indexingserver.elasticsearch.document

import dk.runerne.indexing.contentclient.model.Artist
import dk.runerne.indexing.contentclient.model.Label
import dk.runerne.indexing.contentclient.model.Media
import dk.runerne.indexing.contentclient.model.MediaArtist
import dk.runerne.indexing.contentclient.model.MediaArtistRole
import dk.runerne.indexing.contentclient.model.MediaFile
import dk.runerne.indexing.contentclient.model.MediaGroup
import dk.runerne.indexing.contentclient.model.Role
import spock.lang.Specification

import java.time.OffsetDateTime
import java.time.ZoneOffset

class MediaIndexDocumentMapperSpec extends Specification {

    MediaIndexDocumentMapper mapper = new MediaIndexDocumentMapperImpl()

    def "maps all scalar Media fields correctly"() {
        given:
        UUID mediaId = UUID.randomUUID()
        OffsetDateTime createdAt = OffsetDateTime.of(2024, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC)
        OffsetDateTime updatedAt = OffsetDateTime.of(2024, 9, 21, 14, 30, 0, 0, ZoneOffset.UTC)
        OffsetDateTime publishedAt = OffsetDateTime.of(2024, 2, 1, 12, 0, 0, 0, ZoneOffset.UTC)
        OffsetDateTime recordedAt = OffsetDateTime.of(2024, 1, 15, 9, 0, 0, 0, ZoneOffset.UTC)

        Media media = new Media()
            .id(mediaId)
            .version(3L)
            .createdAt(createdAt)
            .createdBy("user@runerne.dk")
            .updatedAt(updatedAt)
            .updatedBy("admin@runerne.dk")
            .publishedAt(publishedAt)
            .publishedBy("admin@runerne.dk")
            .name("Morgensang")
            .description("Morgensang fra gudstjenesten")
            .recordedAt(recordedAt)

        when:
        MediaIndexDocument doc = mapper.toDocument(media)

        then:
        doc.id() == mediaId.toString()
        doc.version() == 3L
        doc.createdAt() == createdAt
        doc.createdBy() == "user@runerne.dk"
        doc.updatedAt() == updatedAt
        doc.updatedBy() == "admin@runerne.dk"
        doc.publishedAt() == publishedAt
        doc.publishedBy() == "admin@runerne.dk"
        doc.name() == "Morgensang"
        doc.description() == "Morgensang fra gudstjenesten"
        doc.recordedAt() == recordedAt
    }

    def "maps MediaFile including storage enum to String"() {
        given:
        UUID fileId = UUID.randomUUID()
        UUID mediaFileId = UUID.randomUUID()

        Media media = new Media().mediaFile(
            new MediaFile()
                .id(mediaFileId)
                .version(1L)
                .storage(MediaFile.StorageEnum.FILE_SERVER)
                .fileId(fileId)
                .filename("morgensang.mp4")
                .mimeType("video/mp4")
        )

        when:
        MediaIndexDocument doc = mapper.toDocument(media)

        then:
        MediaFileDocument mf = doc.mediaFile()
        mf
        mf.id() == mediaFileId.toString()
        mf.version() == 1L
        mf.storage() == "FILE_SERVER"
        mf.fileId() == fileId.toString()
        mf.filename() == "morgensang.mp4"
        mf.mimeType() == "video/mp4"
    }

    def "maps mediaArtists Set to List with nested Artist and MediaArtistRole"() {
        given:
        UUID artistId = UUID.randomUUID()
        UUID roleId = UUID.randomUUID()
        UUID mediaArtistId = UUID.randomUUID()

        Artist artist = new Artist()
            .id(artistId)
            .version(2L)
            .name("Peter Hansen")
            .description("Organist")
        MediaArtistRole role = new MediaArtistRole()
            .id(roleId)
            .version(1L)
            .name("Organist")
            .description("Spiller orgel")
        MediaArtist mediaArtist = new MediaArtist()
            .id(mediaArtistId)
            .artist(artist)
            .mediaArtistRole(role)

        Media media = new Media().mediaArtists(new LinkedHashSet<>([mediaArtist]))

        when:
        MediaIndexDocument doc = mapper.toDocument(media)

        then:
        doc.mediaArtists().size() == 1
        MediaArtistDocument mad = doc.mediaArtists().get(0)
        mad.id() == mediaArtistId.toString()
        mad.artist().id() == artistId.toString()
        mad.artist().name() == "Peter Hansen"
        mad.mediaArtistRole().id() == roleId.toString()
        mad.mediaArtistRole().name() == "Organist"
    }

    def "maps mediaGroups Set to List with nested roles"() {
        given:
        UUID groupId = UUID.randomUUID()
        UUID roleId = UUID.randomUUID()

        Role role = new Role()
            .id(roleId)
            .version(1L)
            .name("Viewer")
            .authority("ROLE_VIEWER")
            .description("Kan se medier")
        MediaGroup group = new MediaGroup()
            .id(groupId)
            .version(1L)
            .name("Gudstjenester 2024")
            .description("Alle gudstjenester i 2024")
            .roles(new LinkedHashSet<>([role]))

        Media media = new Media().mediaGroups(new LinkedHashSet<>([group]))

        when:
        MediaIndexDocument doc = mapper.toDocument(media)

        then:
        doc.mediaGroups().size() == 1
        MediaGroupDocument mgd = doc.mediaGroups().get(0)
        mgd.id() == groupId.toString()
        mgd.name() == "Gudstjenester 2024"
        mgd.roles().size() == 1
        mgd.roles().get(0).id() == roleId.toString()
        mgd.roles().get(0).authority() == "ROLE_VIEWER"
    }

    def "maps labels Set to List"() {
        given:
        UUID labelId = UUID.randomUUID()

        Label label = new Label()
            .id(labelId)
            .version(1L)
            .name("Morgensang")
            .description("Morgensange fra gudstjenester")

        Media media = new Media().labels(new LinkedHashSet<>([label]))

        when:
        MediaIndexDocument doc = mapper.toDocument(media)

        then:
        doc.labels().size() == 1
        doc.labels().get(0).id() == labelId.toString()
        doc.labels().get(0).name() == "Morgensang"
    }

    def "handles null mediaFile gracefully"() {
        given:
        Media media = new Media().id(UUID.randomUUID())

        when:
        MediaIndexDocument doc = mapper.toDocument(media)

        then:
        !doc.mediaFile()
    }

    def "handles empty collections gracefully"() {
        given:
        Media media = new Media()
            .id(UUID.randomUUID())
            .mediaArtists(new LinkedHashSet<>())
            .mediaGroups(new LinkedHashSet<>())
            .labels(new LinkedHashSet<>())

        when:
        MediaIndexDocument doc = mapper.toDocument(media)

        then:
        doc.mediaArtists().isEmpty()
        doc.mediaGroups().isEmpty()
        doc.labels().isEmpty()
    }

}
