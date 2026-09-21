package dk.runerne.indexingserver.elasticsearch.document;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch subdocument representing a {@code MediaArtist} join entity,
 * embedding both the associated {@link ArtistDocument} and {@link MediaArtistRoleDocument}.
 */
public record MediaArtistDocument(
    @Field(type = FieldType.Keyword) String id,
    @Field(type = FieldType.Object) ArtistDocument artist,
    @Field(type = FieldType.Object) MediaArtistRoleDocument mediaArtistRole
) {

}
