package de.aero.display

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication(
        scanBasePackageClasses = [DisplayApplication::class]
)
@EnableKafka
class DisplayApplication

fun main(args: Array<String>) {
    runApplication<DisplayApplication>(*args)
}
