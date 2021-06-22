package main

import main.bot.TelegramBot
import main.service.TestResultService
import main.service.TestService
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@SpringBootApplication
class RunApplication

fun main(args: Array<String>) {
	val ctx = SpringApplication.run(RunApplication::class.java, *args)
}
