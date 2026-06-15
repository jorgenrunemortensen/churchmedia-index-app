package dk.runerne.indexingserver.synchronization;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleSynchronizationService implements Synchronizer {

    @Override
    public void synchronizeCreated(UUID id) {
        System.out.println("Synchronizing created role with id: " + id);
    }

    @Override
    public void synchronizeUpdated(UUID id) {
        System.out.println("Synchronizing updated role with id: " + id);
    }

    @Override
    public void synchronizeDeleted(UUID id) {
        System.out.println("Synchronizing deleted role with id: " + id);
    }

}