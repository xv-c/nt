package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.exceptions.ServiceException
import main.model.test.test.TestQuestion
import main.model.User
import main.service.TestService
import main.util.ResponseFactory
import main.util.Views
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tests")
class TestController(private var testService: TestService) {

    @GetMapping
    @JsonView(Views.Minimal::class)
    fun getList(
            @AuthenticationPrincipal user: User):
            ResponseEntity<*> {
        return ResponseFactory.ok("tests", testService.getUserTests(user))
    }

    @GetMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun getOne(
            @AuthenticationPrincipal user: User?,
            @PathVariable key: String):
            ResponseEntity<*> {
        return ResponseFactory.ok("test", testService.getTestForRespondent(user, key))
    }

    @PostMapping
    @JsonView(Views.Minimal::class)
    fun createTest(
            @RequestParam(name = "questions") testQuestionsJson: String,
            @RequestParam(name = "name") name: String,
            @RequestParam(name = "loginRequired") loginRequired: Boolean,
            @RequestParam(name = "description") description: String,
            @AuthenticationPrincipal user: User)
            : ResponseEntity<*> {
        if (name.isEmpty() || name.length > 50)
            return ResponseFactory.fail("Название формы не может быть пустым и не может превышать длину в 50 символов")
        if (description.isEmpty() || description.length > 200)
            return ResponseFactory.fail("Описание формы не может быть пустым и не может превышать длину в 200 символов")
        val validQuestions = testService.parseQuestions(testQuestionsJson)
        val result = testService.save(name, description, loginRequired, validQuestions, user)

        return ResponseFactory.ok("test", result)
    }

    @DeleteMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun removeTest(
            @AuthenticationPrincipal user: User,
            @PathVariable key: String)
            : ResponseEntity<*> {
        return ResponseFactory.ok("testId", testService.remove(user, key))
    }
}
