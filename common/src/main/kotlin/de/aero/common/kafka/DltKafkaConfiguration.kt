package de.aero.common.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.AfterRollbackProcessor
import org.springframework.kafka.listener.ConsumerRecordRecoverer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultAfterRollbackProcessor
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

//    @Bean
    fun kafkaListenerContainerFactory(
            dltRecoverer: ConsumerRecordRecoverer,
            kafkaTemplate: KafkaTemplate<*, *>,
    ): ConcurrentKafkaListenerContainerFactory<*, *>? {
        val factory = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        factory.setContainerCustomizer {
            it.containerProperties.eosMode = ContainerProperties.EOSMode.V2
            //it.containerProperties.transactionManager
            it.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
            it.containerProperties.isDeliveryAttemptHeader = true
        }
        factory.setAfterRollbackProcessor(
                DefaultAfterRollbackProcessor(
                        dltRecoverer,
                        FixedBackOff(0, 2),
                        kafkaTemplate,
                        true
                )
        )
        return factory
    }

    @Bean
    fun afterRollbackProcessor(dltRecoverer: ConsumerRecordRecoverer, kafkaTemplate: KafkaTemplate<*, *>): AfterRollbackProcessor<Any, Any> {
        return DefaultAfterRollbackProcessor(
                dltRecoverer,
                FixedBackOff(0, 2),
                kafkaTemplate,
                true
        )
    }
}