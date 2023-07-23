package de.aero.ifis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
		scanBasePackageClasses = [IfisApplication::class]
)
@EnableKafka
@EnableScheduling
class IfisApplication

fun main(args: Array<String>) {
	runApplication<IfisApplication>(*args)
}
