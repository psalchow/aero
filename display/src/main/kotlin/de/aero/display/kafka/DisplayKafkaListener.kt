package de.aero.display.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@KafkaListener(topics = ["flights", "flights-display-recovery"])
class DisplayKafkaListener() {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @KafkaHandler(isDefault = true)
    fun receive(record: ConsumerRecord<String, String?>) {
        logger.info("received message on default handler: ${record.topic()}-${record.partition()}-${record.offset()}")
        if (record.value().equals("FAILURE")) {
            throw RuntimeException()
        }
    }

}
