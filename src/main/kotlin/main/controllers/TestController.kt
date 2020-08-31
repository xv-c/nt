package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.model.TestAnswerVariant
import main.model.TestQuestion
import main.model.User
import main.service.TestService
import main.util.ResponseFactory
import main.util.Views
import org.springframework.boot.json.JsonParserFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tests")
class TestController(var testService: TestService) {

    @GetMapping
    @JsonView(Views.Minimal::class)
    fun getList(@AuthenticationPrincipal user: User): ResponseEntity<*> {
        val list = testService.getUserTests(user)
        return ResponseFactory.buildResponse("tests", list, true, HttpStatus.OK)
    }

    @GetMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun getOne(@AuthenticationPrincipal user: User, @PathVariable key: String): ResponseEntity<*> {
        val list = testService.getTest(user, key)
        return ResponseFactory.buildResponse("tests", list, true, HttpStatus.OK)
    }

    @PostMapping
    @JsonView(Views.Minimal::class)
    fun createTest(
            @RequestParam(name = "questions") testQuestionsJson: String,
            @RequestParam(name = "name") name: String,
            @RequestParam(name = "loginRequired") loginRequired: Boolean,
            @RequestParam(name = "description") description: String,
            @AuthenticationPrincipal user: User
    ): ResponseEntity<*> {
        if (name.isEmpty() || name.length > 50)
            return ResponseFactory.buildUnsuccessfulResponse("Название формы не может быть пустым и не может превышать длину в 50 символов")
        if (description.isEmpty() || description.length > 200)
            return ResponseFactory.buildUnsuccessfulResponse("Описание формы не может быть пустым и не может превышать длину в 200 символов")

        val validQuestions = ArrayList<TestQuestion>()
        JsonParserFactory.getJsonParser().parseList(testQuestionsJson).forEach loop@{ jsonQuestion ->
            val questionMap = jsonQuestion as Map<*, *>
            val question = TestQuestion()

            question.question = questionMap["question"] as String
            if (question.question.isEmpty() || question.question.length > 200)
                return ResponseFactory.buildUnsuccessfulResponse("Вопрос не может быть пустым и не может превышать длину в 200 символов")

            try {
                question.type = TestQuestion.QuestionType.valueOf(questionMap["type"] as String)
            } catch (e: Exception) {
                return ResponseFactory.buildUnsuccessfulResponse("Недопустимый тип вопроса")
            }

            if (question.type != TestQuestion.QuestionType.TEXT) {
                val validVariants = ArrayList<TestAnswerVariant>()

                if ((questionMap["variants"] as ArrayList<*>).size == 0)
                    return ResponseFactory.buildUnsuccessfulResponse("Вопрос данного типа обязан иметь хотя бы один вариант ответа")

                (questionMap["variants"] as ArrayList<*>).forEach { jsonVariant ->
                    val variantMap = jsonVariant as Map<*, *>
                    val variant = TestAnswerVariant()
                    variant.text = variantMap["text"] as String

                    if (variant.text.isEmpty() || variant.text.length > 50)
                        return ResponseFactory.buildUnsuccessfulResponse("Вариант ответа не может быть пустым и не может превышать длину в 50 символов")
                    validVariants.add(variant)
                }
                question.variants = validVariants
            }
            validQuestions.add(question)
        }

        val result = testService.createTest(name, description, loginRequired, validQuestions, user)
        return ResponseFactory.buildResponse("test", result, true, HttpStatus.OK)
    }
}