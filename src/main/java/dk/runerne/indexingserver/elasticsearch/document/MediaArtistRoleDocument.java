package dk.runerne.indexingserver.elasticsearch.document;

import java.time.OffsetDateTime;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch subdocument representing a {@code MediaArtistRole} entity.
 */
public record MediaArtistRoleDocument(
    @Field(type = FieldType.Keyword) String id,
    @Field(type = FieldType.Long) Long version,
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction) OffsetDateTime createdAt,
    @Field(type = FieldType.Keyword) String createdBy,
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction) OffsetDateTime updatedAt,
    @Field(type = FieldType.Keyword) String updatedBy,
    @Field(type = FieldType.Text) String name,
    @Field(type = FieldType.Text) String description
) {

}
