package de.aero.recovery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication(
		scanBasePackageClasses = [RecoveryApplication::class]
)
@EnableKafka
class RecoveryApplication

fun main(args: Array<String>) {
	runApplication<RecoveryApplication>(*args)
}
