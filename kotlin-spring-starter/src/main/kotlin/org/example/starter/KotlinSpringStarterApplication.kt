package org.example.starter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSpringStarterApplication

fun main(args: Array<String>) {
	runApplication<KotlinSpringStarterApplication>(*args)
}
