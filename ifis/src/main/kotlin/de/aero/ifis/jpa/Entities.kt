package de.aero.ifis.jpa

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeaders
import org.hibernate.annotations.Type
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.util.*

interface OutboxRepository : CrudRepository<Outbox, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000"))
    @Query("select o from Outbox o where o.id = :id")
    fun findByIdLocked(@Param("id") id: UUID?): Optional<Outbox>

    fun findBySentOrderByCreatedAt(sent: Boolean): List<Outbox>
}

@Entity
data class Outbox(
        @Id val id: UUID,
        val groupId: String,
        val topic: String,
        val partition: Int?,
        val timestamp: Long?,
        val key: ByteArray?,
        val value: ByteArray?,
        @Type(JsonBinaryType::class)
        val headers: Map<String, ByteArray>,
        val createdAt: Instant = Instant.now(),
        var sent: Boolean = false,
)

fun Outbox.toProducerRecord() = ProducerRecord<Any?, Any?>(
        topic, partition, timestamp, key, value,
        headers.entries.fold(RecordHeaders()) { rh, h -> rh.apply { add(h.key, h.value) } }
)

fun Outbox.toStringifiedProducerRecord() = ProducerRecord<String?, String?>(
        topic, partition, timestamp, key?.toString(Charsets.UTF_8), value?.toString(Charsets.UTF_8),
        headers.entries.fold(RecordHeaders()) { rh, h -> rh.apply { add(h.key, h.value) } }
)

fun ProducerRecord<*, *>.toOutbox(
        groupId: String
): Outbox {
    fun Any.toPersistance(): ByteArray {
        return when (this) {
            is ByteArray -> this
            is String -> toByteArray(Charsets.UTF_8)
            else -> throw IllegalArgumentException("cannot convert value")
        }
    }
    return Outbox(
            id = UUID.randomUUID(),
            groupId = groupId,
            topic = topic(),
            partition = partition(),
            timestamp = timestamp(),
            key = key()?.toPersistance(),
            value = value()?.toPersistance(),
            headers = headers().associate { it.key() to it.value() }
    )
}