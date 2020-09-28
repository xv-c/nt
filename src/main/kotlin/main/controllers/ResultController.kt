package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.model.test.result.TestResult
import main.model.test.test.Test
import main.service.ResultService
import main.service.TestService
import main.util.ResponseFactory
import main.util.Views
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/results/")
class ResultController(val testService: TestService, val resultService: ResultService) {

    @GetMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun getResults(
            @PathVariable key: String,
            @AuthenticationPrincipal user: User?
    ): ResponseEntity<*> {
        if (user == null)
            return ResponseFactory.fail("Необходимо авторизоваться")
        val test: Test
        try {
            test = testService.getUserTest(user, key)
        } catch (e: Exception) {
            return ResponseFactory.fail(e.message)
        }
        var results: List<TestResult>? = null
        try {
            results = resultService.getResults(test)
        } catch (e: Exception) {
            ResponseFactory.fail(e.message)
        }
        return ResponseFactory.ok(
                arrayOf("test", "results"),
                arrayOf(test, results) as Array<Any>)
    }

    @PostMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun postResult(
            @PathVariable key: String,
            @RequestParam("result") resultJson: String,
            @AuthenticationPrincipal user: User?
    ): ResponseEntity<*> {
        val test: Test
        try {
            test = testService.getTest(user, key)
        } catch (e: Exception) {
            return ResponseFactory.fail(e.message)
        }
        val testResult = resultService.parseResult(resultJson, test, user)
        val result = resultService.save(testResult)
        return ResponseFactory.ok("result", result)
    }
}