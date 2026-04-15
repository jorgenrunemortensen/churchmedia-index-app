package dk.runerne.indexingserver.contenteventprocessing;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    public static final String CONTENT_EVENTS_EXCHANGE = "content.events";
    public static final String CONTENT_EVENTS_QUEUE = "indexing.content.events.queue";
    public static final String ROUTING_KEY = "*.*";

    @Bean
    public TopicExchange contentEventsExchange() {
        return new TopicExchange(CONTENT_EVENTS_EXCHANGE, true, false);
    }

    @Bean
    public Queue indexingQueue() {
        return new Queue(CONTENT_EVENTS_QUEUE, true);
    }

    @Bean
    public Binding indexingBinding(Queue indexingQueue, TopicExchange contentEventsExchange) {
        return BindingBuilder
                   .bind(indexingQueue)
                   .to(contentEventsExchange)
                   .with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}