package de.aero.ifis.kafka

import de.aero.ifis.jpa.OutboxRepository
import de.aero.ifis.jpa.toOutbox
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class OutboxStorage(
        val outboxRepository: OutboxRepository,
        val applicationEventPublisher: ApplicationEventPublisher
) {
    fun sendViaOutbox(record: ProducerRecord<Any?, Any?>) {
        val result = outboxRepository.save(record.toOutbox("any"))
        applicationEventPublisher.publishEvent(OutboxEvent(result.id))
    }
}