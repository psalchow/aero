package de.aero.ifis.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaOperations
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.util.*

@Component
class OutboxSender(
        val template: KafkaOperations<String, String>,
        val outboxService: OutboxService,
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    private fun sendToKafka(
            record: ProducerRecord<String?, String?>
    ) {
        logger.info("send message with key {}", record.key())
        val result = template.send(record);
        result.get() // TODO: andere funktion nehmen
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Async
    fun sendAfterCommit(outboxEvent: OutboxEvent) {
        sendWithinTransaction(outboxEvent.id)
    }

    @Scheduled(fixedRate = 60_000)
    fun sendScheduled() {
        logger.info("send scheduled")
        do {
            val findByNotSent = outboxService.findByNotSent()
            findByNotSent?.let { sendWithinTransaction(it) }
        } while(findByNotSent != null)
        logger.info("send scheduled finished")
    }

    private fun sendWithinTransaction(id: UUID) {
        outboxService.sendWithinTransaction(id, { sendToKafka(it) })
    }
}
