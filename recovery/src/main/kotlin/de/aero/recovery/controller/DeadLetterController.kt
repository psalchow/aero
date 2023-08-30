package de.aero.recovery.controller

import de.aero.common.kafka.DltHeaders
import de.aero.recovery.jpa.DeadLetter
import de.aero.recovery.jpa.DeadLetterRepository
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaOperations
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api", produces = [MediaType.APPLICATION_JSON_VALUE])
class DeadLetterController(
    private val deadLetterRepository: DeadLetterRepository,
    private val template: KafkaOperations<String, String>,
) {

    @GetMapping("/dl")
    fun getDeadLetters(@RequestParam recovered: Boolean?) = deadLetterRepository.findAll().filter { dl ->
        recovered?.let {
            when (it) {
                true -> dl.recoveredAt != null
                false -> dl.recoveredAt == null
            }
        } ?: true
    }.map { it.toDto() }

    @PostMapping("dl/{id}/recover")
    @Transactional
    fun recoverDeadLetter(@PathVariable id: UUID, @RequestBody newContent: String?) {
        deadLetterRepository.getReferenceById(id).also {
            template.send(it.toStringifiedProducerRecord(newContent))
        }.apply {
            recoveredAt = Instant.now()
        }
    }
}

data class DeadLetterListDto(
    val id: UUID,
    val topic: String,
    val appName: String,
    val consumerGroup: String,
    val key: String,
    val value: String?,
    val createdAt: Instant,
    val recoveredAt: Instant?,
)

fun DeadLetter.toDto(): DeadLetterListDto = DeadLetterListDto(
    id = id,
    topic = topic,
    appName = appName,
    consumerGroup = consumerGroup,
    key = String(key),
    value = value?.let { String(it) },
    createdAt = createdAt,
    recoveredAt = recoveredAt,
)

fun DeadLetter.toStringifiedProducerRecord(newContent: String?): ProducerRecord<String?, String?> {
    val recoveryTopicPostfix = "-$consumerGroup-recovery"
    val recoveryTopicName = topic.takeIf { it.endsWith(recoveryTopicPostfix) } ?: "$topic$recoveryTopicPostfix"
    return ProducerRecord<String?, String?>(recoveryTopicName,
        null,
        null,
        key.toString(Charsets.UTF_8),
        newContent ?: value?.toString(Charsets.UTF_8),
        headers?.entries?.filter { !it.key.startsWith("kafka_dlt-", true) && it.key != DltHeaders.APP_NAME.headerName }
            ?.map { RecordHeader(it.key, it.value) })
}