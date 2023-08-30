package de.aero.recovery.jpa

import de.aero.common.kafka.DltHeaders
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.hibernate.annotations.Type
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*

interface DeadLetterRepository : JpaRepository<DeadLetter, UUID>

@Entity
data class DeadLetter(
    @Id val id: UUID,
    val topic: String,
    val appName: String,
    val consumerGroup: String,
    val key: ByteArray,
    @Type(JsonBinaryType::class) val headers: Map<String, ByteArray>?,
    val value: ByteArray?,
    val createdAt: Instant = Instant.now(),
    var recoveredAt: Instant? = null,
)

fun ConsumerRecord<*, *>.toDeadLetter(): DeadLetter {
    fun Any.toPersistance(): ByteArray {
        return when (this) {
            is ByteArray -> this
            is String -> toByteArray(Charsets.UTF_8)
            else -> throw IllegalArgumentException("cannot convert value")
        }
    }
    return DeadLetter(
        id = UUID.randomUUID(),
        topic = String(headers().lastHeader(DltHeaders.ORIGINAL_TOPIC.headerName).value()),
        appName = String(headers().lastHeader(DltHeaders.APP_NAME.headerName).value()),
        consumerGroup = String(headers().lastHeader(DltHeaders.ORIGINAL_CONSUMER_GROUP.headerName).value()),
        key = key().toPersistance(),
        headers = headers().associate { it.key() to it.value() },
        value = value()?.toPersistance(),
    )
}