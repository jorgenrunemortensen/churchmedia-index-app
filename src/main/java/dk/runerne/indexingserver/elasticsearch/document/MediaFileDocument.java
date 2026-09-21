package dk.runerne.indexingserver.elasticsearch.document;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch subdocument representing a {@code MediaFile} entity.
 */
public record MediaFileDocument(
    @Field(type = FieldType.Keyword) String id,
    @Field(type = FieldType.Long) Long version,
    @Field(type = FieldType.Keyword) String storage,
    @Field(type = FieldType.Keyword) String fileId,
    @Field(type = FieldType.Text) String filename,
    @Field(type = FieldType.Keyword) String mimeType
) {

}
