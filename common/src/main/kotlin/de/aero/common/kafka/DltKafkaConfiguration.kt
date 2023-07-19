package de.aero.common.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.listener.ConsumerRecordRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff


@Configuration
class DltKafkaConfiguration
    (
    @Value("\${spring.application.name}") private val appName: String,
) {

    @Bean
    fun dltRecoverer(kafkaTemplate: KafkaTemplate<*, *>): DltRecoverer {
        return DltRecoverer(appName, kafkaTemplate)
    }

    @Bean
    fun errorHandler(dltRecoverer: ConsumerRecordRecoverer): CommonErrorHandler {
        return DefaultErrorHandler(
            dltRecoverer,
            FixedBackOff(0, 2)
        )
    }
}