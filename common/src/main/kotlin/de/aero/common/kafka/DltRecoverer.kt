package de.aero.common.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.header.Headers
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer

class DltRecoverer(
    private val appName: String,
    template: KafkaOperations<*, *>,
) : DeadLetterPublishingRecoverer(
    template,
    { cr, e -> TopicPartition("dlt", cr.partition()) }
) {
    override fun createProducerRecord(
        record: ConsumerRecord<*, *>,
        topicPartition: TopicPartition,
        headers: Headers,
        key: ByteArray?,
        value: ByteArray?
    ): ProducerRecord<Any, Any> = super.createProducerRecord(record, topicPartition, headers, key, value).apply {
        record.headers().forEach { headers().add(it.key(), it.value()) }
        headers().add(DltHeaders.APP_NAME.headerName, appName.toByteArray(Charsets.UTF_8))
    }
}