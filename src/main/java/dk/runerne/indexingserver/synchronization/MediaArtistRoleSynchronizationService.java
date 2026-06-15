package dk.runerne.indexingserver.synchronization;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MediaArtistRoleSynchronizationService implements Synchronizer {

    @Override
    public void synchronizeCreated(UUID id) {
        System.out.println("Synchronizing created media artist role with id: " + id);
    }

    @Override
    public void synchronizeUpdated(UUID id) {
        System.out.println("Synchronizing updated media artist role with id: " + id);
    }

    @Override
    public void synchronizeDeleted(UUID id) {
        System.out.println("Synchronizing deleted media artist role with id: " + id);
    }

}