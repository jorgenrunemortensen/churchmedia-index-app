package dk.runerne.indexingserver.contenteventprocessing;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ContentEventListener {

    @RabbitListener(queues = RabbitMqConfiguration.CONTENT_EVENTS_QUEUE)
    public void onContentEvent(ContentEvent event) {
        System.out.println("Id: " + event.id());
        System.out.println("AggregateType: " + event.aggregateType());
        System.out.println("EventType: " + event.eventType());
        System.out.println("Sub-payload: " + event.subPayload());
    }

}