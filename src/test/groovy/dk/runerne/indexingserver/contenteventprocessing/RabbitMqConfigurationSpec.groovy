package dk.runerne.indexingserver.contenteventprocessing

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import spock.lang.Specification

class RabbitMqConfigurationSpec extends Specification {

    RabbitMqConfiguration configuration = new RabbitMqConfiguration()

    def "indexing queue is durable and dead-letters to the dead letter exchange"() {
        when:
        Queue queue = configuration.indexingQueue()

        then:
        queue.name == RabbitMqConfiguration.CONTENT_EVENTS_QUEUE
        queue.durable
        queue.arguments["x-dead-letter-exchange"] == RabbitMqConfiguration.CONTENT_EVENTS_DEAD_LETTER_EXCHANGE
        queue.arguments["x-dead-letter-routing-key"] == RabbitMqConfiguration.CONTENT_EVENTS_QUEUE
    }

    def "dead letter queue is bound to the dead letter exchange with the dead letter routing key"() {
        given:
        Queue deadLetterQueue = configuration.contentEventsDeadLetterQueue()
        DirectExchange deadLetterExchange = configuration.contentEventsDeadLetterExchange()

        when:
        Binding binding = configuration.contentEventsDeadLetterBinding(deadLetterQueue, deadLetterExchange)

        then:
        deadLetterQueue.name == RabbitMqConfiguration.CONTENT_EVENTS_DEAD_LETTER_QUEUE
        deadLetterQueue.durable
        deadLetterExchange.name == RabbitMqConfiguration.CONTENT_EVENTS_DEAD_LETTER_EXCHANGE
        deadLetterExchange.durable
        binding.destination == RabbitMqConfiguration.CONTENT_EVENTS_DEAD_LETTER_QUEUE
        binding.exchange == RabbitMqConfiguration.CONTENT_EVENTS_DEAD_LETTER_EXCHANGE
        binding.routingKey == RabbitMqConfiguration.CONTENT_EVENTS_QUEUE
    }

}
