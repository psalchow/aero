package de.aero.ifis.kafka

import de.aero.ifis.jpa.Outbox
import de.aero.ifis.jpa.OutboxRepository
import de.aero.ifis.jpa.toStringifiedProducerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Component
@Transactional
class OutboxService(
        val outboxRepository: OutboxRepository
) {
    fun findByNotSent(): UUID? {
        return outboxRepository.findBySentOrderByCreatedAt(false)
                .firstOrNull()?.id
    }

    fun sendWithinTransaction(id: UUID, consumer: (ProducerRecord<String?, String?>) -> Unit) {
        val outbox = findByIdLocked(id)
        outbox?.let {
            consumer(it.toStringifiedProducerRecord())
            it.sent = true
        }
    }

    private fun findByIdLocked(id: UUID): Outbox? {
        val outbox = outboxRepository.findByIdLocked(id)
        return outbox.getOrNull()?.takeIf { !it.sent }
    }

    private fun findById(id: UUID): Outbox? {
        val outbox = outboxRepository.findById(id)
        return outbox.getOrNull()?.takeIf { !it.sent }
    }

}
