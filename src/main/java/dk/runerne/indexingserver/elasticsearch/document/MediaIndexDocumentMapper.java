package dk.runerne.indexingserver.elasticsearch.document;

import dk.runerne.indexing.contentclient.model.Media;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper that converts a content-app {@link Media} model (including all related entities)
 * into a denormalized {@link MediaIndexDocument} ready for indexing in Elasticsearch.
 *
 * <p>UUID fields are automatically converted to {@code String}.
 * Storage enum values are automatically converted to their {@code .name()} string representation.
 * {@code Set} collections are automatically converted to {@code List}.</p>
 */
@Mapper(componentModel = "spring")
public interface MediaIndexDocumentMapper {

    /**
     * Converts a {@link Media} entity to a {@link MediaIndexDocument}.
     *
     * @param media the source media entity from content-app
     * @return the denormalized Elasticsearch document
     */
    MediaIndexDocument toDocument(Media media);

}
