package dk.runerne.indexingserver.synchronization;

import java.util.UUID;

public interface Synchronizer {

    void synchronizeCreated(UUID id);
    void synchronizeUpdated(UUID id);
    void synchronizeDeleted(UUID id);

}