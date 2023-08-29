package de.aero.recovery.kafka

import de.aero.recovery.jpa.DeadLetterRepository
import de.aero.recovery.jpa.toDeadLetter
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@KafkaListener(topics = ["dlt"])
class DltKafkaListener(val deadLetterRepository: DeadLetterRepository) {

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @KafkaHandler(isDefault = true)
    @Transactional
    fun receive(record: ConsumerRecord<String, String?>) {
        logger.info("received dlt message on default handler: ${record.topic()}-${record.partition()}-${record.offset()}")
        deadLetterRepository.save(record.toDeadLetter())
    }

}
