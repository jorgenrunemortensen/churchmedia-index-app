package dk.runerne.indexingserver.synchronization;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArtistSynchronizationService implements Synchronizer{

    @Override
    public void synchronizeCreated(UUID id) {
        System.out.println("Synchronizing created artist with id: " + id);
    }

    @Override
    public void synchronizeUpdated(UUID id) {
        System.out.println("Synchronizing updated artist with id: " + id);
    }

    @Override
    public void synchronizeDeleted(UUID id) {
        System.out.println("Synchronizing deleted artist with id: " + id);
    }

}