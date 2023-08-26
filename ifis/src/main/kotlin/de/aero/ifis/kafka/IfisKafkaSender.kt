package de.aero.ifis.kafka

import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class IfisKafkaSender(
        val sender: OutboxStorage
) {
    val logger = LoggerFactory.getLogger(this::class.java)

    var failure: Boolean = false;

    @Scheduled(fixedDelay = 2L, timeUnit = TimeUnit.MINUTES)
    fun sendScheduled() {
        failure = !failure;
        val topic = "flights"
        val message = message(failure)

        val key = UUID.randomUUID().toString();
        logger.info("sending on topic ${topic}: ${message} (${key})")

        sender.sendViaOutbox(ProducerRecord<Any?, Any?>(topic, key, message))
    }

    fun message(
            failure: Boolean
    ): String {
        if (failure) {
            return "FAILURE"
        } else {
            return "MESSAGE"
        }
    }
}