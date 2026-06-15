package dk.runerne.indexingserver.contenteventprocessing;

import dk.runerne.indexingserver.synchronization.ArtistSynchronizationService;
import dk.runerne.indexingserver.synchronization.LabelSynchronizationService;
import dk.runerne.indexingserver.synchronization.MediaArtistRoleSynchronizationService;
import dk.runerne.indexingserver.synchronization.MediaGroupSynchronizationService;
import dk.runerne.indexingserver.synchronization.RoleSynchronizationService;
import dk.runerne.indexingserver.synchronization.Synchronizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import dk.runerne.indexingserver.synchronization.MediaSynchronizationService;

import java.util.Map;

@Service
@Slf4j
public class ContentEventListener {

    private final Map<AggregateType, Synchronizer> SYNCHRONIZER_MAP;

    public ContentEventListener(
        MediaSynchronizationService mediaSynchronizationService,
        ArtistSynchronizationService artistSynchronizationService,
        RoleSynchronizationService roleSynchronizationService,
        MediaGroupSynchronizationService mediaGroupSynchronizationService,
        MediaArtistRoleSynchronizationService mediaArtistRoleSynchronizationService,
        LabelSynchronizationService labelSynchronizationService
    ) {
        SYNCHRONIZER_MAP = Map.of(
            AggregateType.MEDIA, mediaSynchronizationService,
            AggregateType.ARTIST, artistSynchronizationService,
            AggregateType.ROLE, roleSynchronizationService,
            AggregateType.MEDIA_GROUP, mediaGroupSynchronizationService,
            AggregateType.MEDIA_ARTIST_ROLE, mediaArtistRoleSynchronizationService,
            AggregateType.LABEL, labelSynchronizationService
        );
    }

    @RabbitListener(queues = RabbitMqConfiguration.CONTENT_EVENTS_QUEUE)
    public void onContentEvent(ContentEvent event) {
        var synchronizer = SYNCHRONIZER_MAP.get(event.aggregateType());
        if (synchronizer == null) {
            log.warn("No synchronizer found for aggregate type: " + event.aggregateType());
            return;
        }

        switch(event.eventType()) {
            case CREATED -> synchronizer.synchronizeCreated(event.id());
            case UPDATED -> synchronizer.synchronizeUpdated(event.id());
            case DELETED -> synchronizer.synchronizeDeleted(event.id());
            default -> log.warn("Unsupported event type: " + event.eventType());
        }
    }

}