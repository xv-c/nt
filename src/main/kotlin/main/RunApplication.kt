package main

import main.service.UserService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RunApplication

fun main(args: Array<String>) {
	val ctx = runApplication<RunApplication>(*args)
	val userService = ctx.getBean(UserService::class.java)
}
