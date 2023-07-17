package de.aero.ifis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IfisApplication

fun main(args: Array<String>) {
	runApplication<IfisApplication>(*args)
}
