package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.service.TestResultService
import main.service.TestService
import main.util.Endpoints
import main.util.ResponseFactory
import main.util.Views
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Endpoints.RESULTS)
class ResultController(private val testService: TestService, val testResultService: TestResultService) {

    @GetMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun getResults(
        @PathVariable key: String,
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<*> {

        val test = testService.getTestForCreator(user, key)
        val results = testResultService.get(test)
        return ResponseFactory.ok(
            arrayOf("test", "results"),
            arrayOf(test, results)
        )
    }

    @PostMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun postResult(
        @PathVariable key: String,
        @RequestParam("result") resultJson: String,
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<*> {
        val test = testService.getTestForRespondent(user, key)
        val testResult = testResultService.parseResult(resultJson, test, user)
        val result = testResultService.save(testResult)
        return ResponseFactory.ok("result", result)
    }
}