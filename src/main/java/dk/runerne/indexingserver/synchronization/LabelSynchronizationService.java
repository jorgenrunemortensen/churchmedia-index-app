package dk.runerne.indexingserver.synchronization;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LabelSynchronizationService implements Synchronizer {

    @Override
    public void synchronizeCreated(UUID id) {
        System.out.println("Synchronizing created label with id: " + id);
    }

    @Override
    public void synchronizeUpdated(UUID id) {
        System.out.println("Synchronizing updated label with id: " + id);
    }

    @Override
    public void synchronizeDeleted(UUID id) {
        System.out.println("Synchronizing deleted label with id: " + id);
    }

}