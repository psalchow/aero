package de.aero.common.kafka

import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.header.internals.RecordHeaders
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer

class DltRecoverer(private val appName: String,
        template: KafkaOperations<*, *>,
) : DeadLetterPublishingRecoverer(
        template,
        { cr, e -> TopicPartition("dlt", cr.partition()) }
) {
    init {
        setHeadersFunction { record, _ ->
            RecordHeaders().apply {
                record.headers().forEach { add(it.key(), it.value()) }
                sequenceOf(
                        DltHeaders.APP_NAME.headerName to appName.toByteArray(Charsets.UTF_8)
                )
            }
        }
    }
}