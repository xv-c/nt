package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.service.ResultService
import main.service.TestService
import main.util.ResponseFactory
import main.util.Views
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/results/")
class ResultController(private val testService: TestService, val resultService: ResultService) {

    @GetMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun getResults(
        @PathVariable key: String,
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<*> {

        val test = testService.getTestForCreator(user, key)
        val results = resultService.getResults(test)
        return ResponseFactory.ok(
            arrayOf("test", "results"),
            arrayOf(test, results)
        )
    }

    /*@GetMapping("/status/{key}")
    @JsonView(Views.Minimal::class)
    fun getAnalysisStatus(
        @PathVariable key: String
    ) : ResponseEntity<*> {
        return ResponseFactory.ok()
    }*/

    /*@PostMapping("{key}")
    @JsonView
    fun startAnalysis(
        @PathVariable key: String
    ): ResponseEntity<*> {
        val test = testService.getTest(key)
        resultService.startAnalysis(test)
        return ResponseFactory.ok()
    }*/

    @PostMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun postResult(
        @PathVariable key: String,
        @RequestParam("result") resultJson: String,
        @AuthenticationPrincipal user: User?
    ): ResponseEntity<*> {
        val test = testService.getTestForRespondent(user, key)
        val testResult = resultService.parseResult(resultJson, test, user)
        val result = resultService.save(testResult)
        return ResponseFactory.ok("result", result)
    }
}