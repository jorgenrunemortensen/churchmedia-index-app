package dk.runerne.indexingserver.elasticsearch.document;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch subdocument representing a {@code Role} entity nested inside a {@code MediaGroup}.
 */
public record RoleDocument(
    @Field(type = FieldType.Keyword) String id,
    @Field(type = FieldType.Long) Long version,
    @Field(type = FieldType.Keyword) String name,
    @Field(type = FieldType.Keyword) String authority,
    @Field(type = FieldType.Text) String description
) {

}
