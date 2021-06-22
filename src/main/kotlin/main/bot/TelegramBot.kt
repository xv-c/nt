package main.bot

import main.exception.RestException
import main.model.test.result.TestResult
import main.model.test.result.TestResultAnswer
import main.model.test.test.Test
import main.model.test.test.TestQuestion
import main.service.TestResultService
import main.service.TestService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession


class Answer(var textAnswer: String?, var singleAnswer: Int?, var answers: MutableSet<Int>?)
class Progress(var key: String, var answered: Int, var answers: MutableList<Answer>, var waitingMessageId: Long?, var waitingMessage: Boolean)

val progressMap: HashMap<Long, Progress> = hashMapOf()


@Component
final class TelegramBot(
    private val testService: TestService,
    private val resultService: TestResultService
) : TelegramLongPollingBot() {

    init {
        TelegramBotsApi(DefaultBotSession::class.java)
            .registerBot(this)
    }

    object Commands {
        const val start = "/start"
        const val find = "/find"
    }

    object Callbacks {
        const val select = "/select"
        const val deselect = "/deselect"
        const val answer = "/answer"
    }

    private fun getTest(chatId: Long, key: String): Test {
        try {
            return testService.getTestForRespondent(null, key)
        } catch (e: RestException) {
            if (e.message == "Для данного опроса необходимо авторизоваться")
                message(chatId, "Данный опрос можно пройти только в приложении", null)
            else
                message(chatId, e.message!!, null)
            throw e
        }
    }

    private fun startTest(chatId: Long, test: Test) {
        val newProgress = Progress(test.key, 0, mutableListOf(), null, false)
        progressMap[chatId] = newProgress

        var message = "*Название:* ${test.name}\n*Описание:* ${test.description}\n\n"
        message += prepareQuestionMessage(0, test.questions[0])
        val keyboard = prepareTestKeyboard(test, 0, newProgress)
        message(chatId, message, keyboard)
    }

    private fun finishProgress(chatId: Long, test: Test) {
        message(chatId, "Ваш ответ записан!", null)

        val progress = progressMap.remove(chatId)!!

        val result = TestResult()
        result.test = test
        result.answers = mutableListOf()

        test.questions.forEachIndexed { index, question ->
            val progressAnswer = progress.answers[index]
            val answer = TestResultAnswer()
            answer.question = question

            when (question.type) {
                TestQuestion.QuestionType.TEXT -> {
                    answer.answer = progressAnswer.textAnswer
                }
                TestQuestion.QuestionType.MULTI -> {
                    answer.answers = progressAnswer.answers!!.map { question.variants!![it] }.toMutableList()
                }
                TestQuestion.QuestionType.ONE -> {
                    answer.answers = mutableListOf(question.variants!![progressAnswer.singleAnswer!!])
                }
            }

            result.answers.add(answer)
        }

        resultService.save(result)
    }

    private fun editNext(chatId: Long, messageId: Int, progress: Progress){
        val test = getTest(chatId, progress.key)

        if (progress.answered == test.questions.size) {
            edit(chatId, messageId, "*Forms*", null)
            finishProgress(chatId, test)
            return
        }

        val message = prepareQuestionMessage(progress.answered, test.questions[progress.answered])
        val keyboard = prepareTestKeyboard(test, progress.answered, progress)
        edit(chatId, messageId, message, keyboard)
    }

    private fun sendNext(chatId: Long, progress: Progress) {
        val test = getTest(chatId, progress.key)

        if (progress.answered == test.questions.size) {
            finishProgress(chatId, test)
            return
        }

        val message = prepareQuestionMessage(progress.answered, test.questions[progress.answered])
        val keyboard = prepareTestKeyboard(test, progress.answered, progress)
        message(chatId, message, keyboard)
    }

    private fun prepareQuestionMessage(index: Int, question: TestQuestion): String {
        return "№${index+1}. *${question.question}*" +
                if (question.type == TestQuestion.QuestionType.TEXT)
                    "\n\nОтправьте ваш ответ текстовым сообщением"
                else if (question.type == TestQuestion.QuestionType.MULTI)
                    "\n\nНеобходимо выбрать хотя бы один вариант ответа"
                else
                    ""
    }

    private fun prepareTestKeyboard(test: Test, i: Int, progress: Progress): InlineKeyboardMarkup? {
        val question = test.questions[i]
        if (question.type == TestQuestion.QuestionType.TEXT) {
            progress.waitingMessage = true
            return null
        }

        val keyboard = mutableListOf<MutableList<InlineKeyboardButton>>()

        question.variants!!.forEachIndexed { index, variant ->
            val emoji: String
            val callback: String

            if (question.type == TestQuestion.QuestionType.ONE) {
                emoji = "⚪️"
                callback = "${Callbacks.answer}:::${index}"
            } else {
                if ((progress.answers.size - 1) == i &&
                    progress.answers[i].answers!!.contains(index)
                ) {
                    emoji = "☑️"
                    callback = "${Callbacks.deselect}:::${index}"
                } else {
                    emoji = "⬜️"
                    callback = "${Callbacks.select}:::${index}"
                }
            }

            keyboard.add(
                mutableListOf(
                    InlineKeyboardButton.builder()
                        .callbackData(callback)
                        .text("${emoji}${variant.value}")
                        .build()
                )
            )
        }

        if (question.type == TestQuestion.QuestionType.MULTI)
            keyboard.add(
                mutableListOf(
                    InlineKeyboardButton.builder()
                        .callbackData(Callbacks.answer)
                        .text("Следующий вопрос➡️")
                        .build()
                )
            )
        return InlineKeyboardMarkup(keyboard)
    }

    fun receiveCallback(update: Update) {
        val chatId = update.callbackQuery.message.chatId
        val progress = progressMap[chatId]!!
        progress.waitingMessageId = update.callbackQuery.message.messageId.toLong()
        val data = update.callbackQuery.data.split(":::")
        val messageId = update.callbackQuery.message.messageId

        when (data[0]) {
            Callbacks.select -> {
                if (progress.answers.isEmpty() ||
                    progress.answers.size - 1 < progress.answered
                )
                    progress.answers.add(Answer(null, null, mutableSetOf()))

                val selects = progress.answers.last()
                selects.answers!!.add(data[1].toInt())
            }
            Callbacks.deselect -> {
                val selects = progress.answers.last()
                selects.answers!!.remove(data[1].toInt())
            }
            Callbacks.answer -> {
                if (data.size == 2) {
                    progress.answers.add(Answer(null, data[1].toInt(), null))
                    progress.answered++
                } else {
                    if (progress.answers.size - 1 == progress.answered &&
                        progress.answers.last().answers!!.size > 0)
                        progress.answered++
                }
            }
        }
        editNext(chatId, messageId, progress)
    }

    fun receiveChat(update: Update) {
        val chatId = update.message.chatId
        val message = update.message.text ?: return

        val progress = progressMap[chatId]

        if (progress != null && progress.waitingMessage) {
            progress.answers.add(Answer(message, null, null))
            progress.answered++
            edit(chatId, progress.waitingMessageId!!.toInt(), "*Forms*", null)
            sendNext(chatId, progress)
        }

        val data = message.split(" ")

        when (data[0]) {
            Commands.start -> {
                message(chatId, "Пример поиска опроса: ${Commands.find} ключ-опроса", null)
            }
            Commands.find -> {
                if (data.size == 1){
                    message(chatId, "Пример поиска опроса: ${Commands.find} ключ-опроса", null)
                    return
                }

                val key = data[1]
                val test = getTest(chatId, key)
                startTest(chatId, test)
            }
        }
    }

    fun message(chatId: Long, msg: String, keys: InlineKeyboardMarkup?) {
        val sendMessage = SendMessage()
        sendMessage.enableMarkdown(true)
        sendMessage.chatId = chatId.toString()
        sendMessage.text = msg
        if (keys != null)
            sendMessage.replyMarkup = keys
        try {
            execute(sendMessage)
        } catch (e: Exception) {
            try {
                sendMessage.text += "..."
                execute(sendMessage)
            } catch (e: Exception) {
                println("ERROR : Не удалось отправить сообщение\n${e.message}")
            }
        }
    }

    fun edit(chatId: Long, messageId: Int, msg: String, keys: InlineKeyboardMarkup?) {
        val editMessage = EditMessageText()
        editMessage.enableMarkdown(true)
        editMessage.chatId = chatId.toString()
        editMessage.messageId = messageId
        editMessage.text = msg
        editMessage.replyMarkup = keys

        try {
            execute(editMessage)
        } catch (e: Exception) {
            try {
                editMessage.text += "..."
                execute(editMessage)
            } catch (e: Exception) {
                println("ERROR : Не удалось изменить сообщение\n${e.message}")
            }
        }
    }


    override fun getBotToken(): String {
        return "1684932877:AAFBveApKlKhflYl3LAMNYDG7BSaKXO6Wxc"
    }

    override fun getBotUsername(): String {
        return "@forms_survey_bot"
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasCallbackQuery()) {
            receiveCallback(update)
        } else {
            receiveChat(update)
        }
    }
}