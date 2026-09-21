package dk.runerne.indexingserver.elasticsearch;

import dk.runerne.indexingserver.elasticsearch.document.MediaIndexDocument;
import java.util.List;
import java.util.UUID;

/**
 * Port for indexing and querying {@link MediaIndexDocument} documents in Elasticsearch.
 */
public interface MediaIndexPort {

    /**
     * Creates or updates (upserts) a Media document in the Elasticsearch index.
     *
     * @param document the document to index
     */
    void index(MediaIndexDocument document);

    /**
     * Deletes the Media document with the given ID from the Elasticsearch index.
     *
     * @param mediaId the ID of the Media document to delete
     */
    void delete(UUID mediaId);

    /**
     * Returns the IDs of all Media documents where the given field path equals the given entity ID.
     *
     * <p>Uses a {@code TermQuery} on the dotted field path (e.g. {@code "labels.id"}).
     * Fields must be mapped as {@code keyword} in the ES index for exact-match lookups to work correctly.</p>
     *
     * <p>At most {@code 10 000} results are returned. A warning is logged if the total hit count
     * exceeds this limit, as only a subset of the affected documents will be reindexed.</p>
     *
     * @param fieldPath the dotted ES field path to query on (e.g. {@code "mediaArtists.artist.id"})
     * @param entityId  the entity ID to match
     * @return list of Media document IDs that reference the given entity
     */
    List<UUID> findMediaIdsByField(String fieldPath, UUID entityId);

}
