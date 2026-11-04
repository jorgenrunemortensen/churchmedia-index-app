package dk.runerne.indexingserver.contenteventprocessing;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ topology for receiving content events from content-app.
 *
 * <p>Messages that fail processing (after the listener retries configured in {@code application.yml} are exhausted)
 * are rejected without requeue and routed via the dead letter exchange to {@link #CONTENT_EVENTS_DEAD_LETTER_QUEUE}, where they can be inspected
 * and, if needed, moved back manually.</p>
 */
@Configuration
public class RabbitMqConfiguration {

    /** Topic exchange that content-app publishes content events to. */
    public static final String CONTENT_EVENTS_EXCHANGE = "content.events";

    /** Queue that indexing-app consumes content events from. */
    public static final String CONTENT_EVENTS_QUEUE = "indexing.content.events.queue";

    /** Routing key pattern binding all content events to {@link #CONTENT_EVENTS_QUEUE}. */
    public static final String ROUTING_KEY = "*.*";

    /** Exchange that rejected content events are dead-lettered to. */
    public static final String CONTENT_EVENTS_DEAD_LETTER_EXCHANGE = "indexing.content.events.dlx";

    /** Queue holding content events that could not be processed. */
    public static final String CONTENT_EVENTS_DEAD_LETTER_QUEUE = "indexing.content.events.dlq";

    private static final String DEAD_LETTER_ROUTING_KEY = CONTENT_EVENTS_QUEUE;

    /**
     * Declares the exchange that content-app publishes events to.
     *
     * @return the durable topic exchange
     */
    @Bean
    public TopicExchange contentEventsExchange() {
        return new TopicExchange(CONTENT_EVENTS_EXCHANGE, true, false);
    }

    /**
     * Declares the indexing queue, configured to dead-letter rejected messages.
     *
     * @return the durable indexing queue
     */
    @Bean
    public Queue indexingQueue() {
        return QueueBuilder.durable(CONTENT_EVENTS_QUEUE)
                   .deadLetterExchange(CONTENT_EVENTS_DEAD_LETTER_EXCHANGE)
                   .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY)
                   .build();
    }

    /**
     * Binds the indexing queue to the content events exchange.
     *
     * @param indexingQueue         the indexing queue
     * @param contentEventsExchange the content events exchange
     * @return the binding
     */
    @Bean
    public Binding indexingBinding(Queue indexingQueue, TopicExchange contentEventsExchange) {
        return BindingBuilder
                   .bind(indexingQueue)
                   .to(contentEventsExchange)
                   .with(ROUTING_KEY);
    }

    /**
     * Declares the dead letter exchange for rejected content events.
     *
     * @return the durable direct exchange
     */
    @Bean
    public DirectExchange contentEventsDeadLetterExchange() {
        return new DirectExchange(CONTENT_EVENTS_DEAD_LETTER_EXCHANGE, true, false);
    }

    /**
     * Declares the dead letter queue for rejected content events.
     *
     * @return the durable dead letter queue
     */
    @Bean
    public Queue contentEventsDeadLetterQueue() {
        return QueueBuilder.durable(CONTENT_EVENTS_DEAD_LETTER_QUEUE).build();
    }

    /**
     * Binds the dead letter queue to the dead letter exchange.
     *
     * @param contentEventsDeadLetterQueue    the dead letter queue
     * @param contentEventsDeadLetterExchange the dead letter exchange
     * @return the binding
     */
    @Bean
    public Binding contentEventsDeadLetterBinding(Queue contentEventsDeadLetterQueue, DirectExchange contentEventsDeadLetterExchange) {
        return BindingBuilder
                   .bind(contentEventsDeadLetterQueue)
                   .to(contentEventsDeadLetterExchange)
                   .with(DEAD_LETTER_ROUTING_KEY);
    }

    /**
     * Message converter deserialising JSON content events.
     *
     * @return the Jackson JSON message converter
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
