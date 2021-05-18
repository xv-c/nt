package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.model.test.test.Test
import main.service.TestService
import main.util.Endpoints
import main.util.ResponseFactory
import main.util.Views
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Endpoints.TESTS)
class TestController(private var testService: TestService) {

    @GetMapping
    @JsonView(Views.Minimal::class)
    fun getList(
        @AuthenticationPrincipal user: User
    ):
            ResponseEntity<*> {
        return ResponseFactory.ok("tests", testService.getUserTests(user))
    }

    @GetMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun getOne(
        @AuthenticationPrincipal user: User?,
        @PathVariable key: String
    ): ResponseEntity<*> {
        return ResponseFactory.ok("test", testService.getTestForRespondent(user, key))
    }

    @PostMapping
    @JsonView(Views.Minimal::class)
    fun create(
        @RequestBody test: Test,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<*> {
        test.creator = user
        return ResponseFactory.ok("test", testService.create(test))
    }

    @DeleteMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun delete(
        @AuthenticationPrincipal user: User,
        @PathVariable key: String
    )
            : ResponseEntity<*> {
        return ResponseFactory.ok("testId", testService.delete(user, key))
    }
}
