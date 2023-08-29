package de.aero.recovery.controller

import de.aero.recovery.jpa.DeadLetter
import de.aero.recovery.jpa.DeadLetterRepository
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/api", produces = [MediaType.APPLICATION_JSON_VALUE])
class DeadLetterController(
    private val deadLetterRepository: DeadLetterRepository
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
}

fun DeadLetter.toDto(): DeadLetterDto = DeadLetterDto(
    id = id,
    topic = topic,
    appName = appName,
    consumerGroup = consumerGroup,
    key = String(key),
    headers = headers?.mapValues { e -> String(e.value) },
    value = value?.let { String(it) },
    createdAt = createdAt,
    recoveredAt = recoveredAt,
)

data class DeadLetterDto(
    val id: UUID,
    val topic: String,
    val appName: String,
    val consumerGroup: String,
    val key: String,
    val headers: Map<String, String>?,
    val value: String?,
    val createdAt: Instant,
    val recoveredAt: Instant?,
)