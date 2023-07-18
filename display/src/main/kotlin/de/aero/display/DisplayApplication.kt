package de.aero.display

import de.aero.common.kafka.DltKafkaConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication(
        scanBasePackageClasses = [DisplayApplication::class]
)
@Import(value = [DltKafkaConfiguration::class])
@EnableKafka
class DisplayApplication

fun main(args: Array<String>) {
    runApplication<DisplayApplication>(*args)
}
