package dk.runerne.indexingserver.synchronization;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MediaGroupSynchronizationService implements Synchronizer {

    @Override
    public void synchronizeCreated(UUID id) {
        System.out.println("Synchronizing created media group with id: " + id);
    }

    @Override
    public void synchronizeUpdated(UUID id) {
        System.out.println("Synchronizing updated media group with id: " + id);
    }

    @Override
    public void synchronizeDeleted(UUID id) {
        System.out.println("Synchronizing deleted media group with id: " + id);
    }

}