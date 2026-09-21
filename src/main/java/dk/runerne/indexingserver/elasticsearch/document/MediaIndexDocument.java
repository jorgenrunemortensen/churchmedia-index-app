package dk.runerne.indexingserver.elasticsearch.document;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Denormalized Elasticsearch document for a Media entity.
 *
 * <p>The document ID equals the Media entity's UUID from content-app.
 * All related entities are embedded directly in the document to avoid joins at query time.</p>
 *
 * <p>Related entities (mediaArtists, mediaGroups, labels) are stored as {@code object} arrays
 * rather than {@code nested} arrays. This allows simple {@code TermQuery} on dotted field paths
 * (e.g. {@code "labels.id"}). If cross-field correlation within a single array element is ever needed,
 * the mapping must be changed to {@code nested} type and {@code NestedQuery} used instead.</p>
 */
@Document(indexName = "media")
public record MediaIndexDocument(
    @Id String id,
    @Field(type = FieldType.Long) Long version,
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction) OffsetDateTime createdAt,
    @Field(type = FieldType.Keyword) String createdBy,
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction) OffsetDateTime updatedAt,
    @Field(type = FieldType.Keyword) String updatedBy,
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction) OffsetDateTime publishedAt,
    @Field(type = FieldType.Keyword) String publishedBy,
    @Field(type = FieldType.Text) String name,
    @Field(type = FieldType.Text) String description,
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction) OffsetDateTime recordedAt,
    @Field(type = FieldType.Object) MediaFileDocument mediaFile,
    @Field(type = FieldType.Object) List<MediaArtistDocument> mediaArtists,
    @Field(type = FieldType.Object) List<MediaGroupDocument> mediaGroups,
    @Field(type = FieldType.Object) List<LabelDocument> labels
) {

}
