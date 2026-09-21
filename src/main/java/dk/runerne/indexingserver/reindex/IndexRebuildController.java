package dk.runerne.indexingserver.reindex;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes an endpoint for triggering a full Elasticsearch index rebuild.
 */
@RestController
@RequestMapping("/api/v1/index")
@RequiredArgsConstructor
@Slf4j
public class IndexRebuildController {

    private final IndexRebuildService indexRebuildService;

    /**
     * Triggers a full rebuild of the Elasticsearch {@code media} index.
     *
     * <p>The operation is synchronous — the response is returned only after all documents
     * have been indexed. Returns {@code 200 OK} on success, {@code 500} on failure.</p>
     *
     * @return empty 200 response when the rebuild completes successfully
     */
    @PostMapping("/rebuild")
    public ResponseEntity<Void> rebuildIndex() {
        log.info("Full index rebuild triggered via REST endpoint");
        indexRebuildService.rebuildIndex();
        return ResponseEntity.ok().build();
    }

}
