package de.aero.display.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
@KafkaListener(topics = ["flights", "flights-display-recovery"])
class DisplayKafkaListener() {

    @KafkaHandler(isDefault = true)
    fun receive(record: ConsumerRecord<Any, Any?>) {
        println("received message on default handler: ${record.topic()}-${record.partition()}-${record.offset()}")
        if (record.topic().equals("flights")) {
            throw RuntimeException()
        }
    }

}
