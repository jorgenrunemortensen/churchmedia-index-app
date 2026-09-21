package dk.runerne.indexingserver.reindex

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class IndexRebuildControllerSpec extends Specification {

    IndexRebuildService indexRebuildService = Mock()
    IndexRebuildController controller = new IndexRebuildController(indexRebuildService)

    def "rebuildIndex delegates to service and returns 200"() {
        when:
        ResponseEntity<Void> response = controller.rebuildIndex()

        then:
        response.statusCode == HttpStatus.OK
        1 * indexRebuildService.rebuildIndex()
    }

}
