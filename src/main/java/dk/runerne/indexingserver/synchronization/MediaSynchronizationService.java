package dk.runerne.indexingserver.synchronization;

import dk.runerne.indexingserver.integration.contentapp.ContentMediaService;
import org.springframework.stereotype.Service;

import java.util.UUID;

import dk.runerne.indexing.contentclient.model.Media;

@Service
public class MediaSynchronizationService implements Synchronizer {

    private final ContentMediaService contentMediaService;

    public MediaSynchronizationService(ContentMediaService contentMediaService) {
        this.contentMediaService = contentMediaService;
    }

    public void synchronizeCreated(UUID mediaId) {
        System.out.println("Synchronizing created media with id: " + mediaId);
        Media media =  contentMediaService.getById(mediaId);
        System.out.println("Retrieved media details: " + media);
    }

    public void synchronizeUpdated(UUID mediaId) {
        System.out.println("Synchronizing updated media with id: " + mediaId);
    }

    public void synchronizeDeleted(UUID mediaId) {
        System.out.println("Synchronizing deleted media with id: " + mediaId);
    }

}