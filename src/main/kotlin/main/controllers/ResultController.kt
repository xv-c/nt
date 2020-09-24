package main.controllers

import main.model.test.test.Test
import main.model.User
import main.service.ResultService
import main.service.TestService
import main.util.ResponseFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/results")
class ResultController(val testService: TestService, val resultService: ResultService) {

    @PostMapping("{key}")
    fun postResult(@PathVariable key: String,
                   @RequestParam("result") resultJson: String,
                   @AuthenticationPrincipal user: User?): ResponseEntity<*> {
        val test: Test
        try {
            test = testService.getTest(user, key)
        } catch (e: Exception) {
            return ResponseFactory.buildUnsuccessfulResponse(e.message)
        }
        val testResult = resultService.parseResult(resultJson, test, user)
        val result = resultService.save(testResult)
        return ResponseFactory.buildResponse("result", result, true, HttpStatus.OK)
    }
}