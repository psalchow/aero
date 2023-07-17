package de.aero.recovery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RecoveryApplication

fun main(args: Array<String>) {
	runApplication<RecoveryApplication>(*args)
}
