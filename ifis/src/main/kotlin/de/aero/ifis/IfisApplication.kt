package de.aero.ifis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
		scanBasePackageClasses = [IfisApplication::class]
)
class IfisApplication

fun main(args: Array<String>) {
	runApplication<IfisApplication>(*args)
}
