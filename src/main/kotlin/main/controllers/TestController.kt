package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.model.test.test.Test
import main.model.test.test.TestQuestion
import main.model.User
import main.service.TestService
import main.util.ResponseFactory
import main.util.Views
import org.hibernate.service.spi.ServiceException
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
    fun getOne(@AuthenticationPrincipal user: User?, @PathVariable key: String): ResponseEntity<*> {
        val test: Test?
        try {
            test = testService.getTest(user, key)
        } catch (e: ServiceException) {
            return ResponseFactory.buildUnsuccessfulResponse(e.message)
        }

        return ResponseFactory.buildResponse("test", test, true, HttpStatus.OK)
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
        val validQuestions: ArrayList<TestQuestion>
        try {
            validQuestions = testService.parseQuestions(testQuestionsJson)
        } catch (e: Exception) {
            return ResponseFactory.buildUnsuccessfulResponse(e.message)
        }
        val result = testService.save(name, description, loginRequired, validQuestions, user)
        return ResponseFactory.buildResponse("test", result, true, HttpStatus.OK)
    }
}