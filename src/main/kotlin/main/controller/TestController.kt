package main.controller

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.model.test.test.Test
import main.service.TestService
import main.util.Endpoints
import main.util.ResponseFactory
import main.util.RestResponse
import main.util.Views
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Endpoints.TESTS)
class TestController(private var testService: TestService) {

    @GetMapping("/user")
    @JsonView(Views.Minimal::class)
    fun getList(
        @AuthenticationPrincipal user: User
    ): RestResponse{
        return ResponseFactory.ok("tests", testService.getUserTests(user))
    }

    @GetMapping("/public")
    @JsonView(Views.Minimal::class)
    fun listPublic(): RestResponse {
        return ResponseFactory.ok("tests", testService.getLastPublic())
    }

    @GetMapping("/search/{name}")
    @JsonView(Views.Minimal::class)
    fun search(
        @PathVariable name: String
    ): RestResponse {
        return ResponseFactory.ok("tests", testService.search(name))
    }

    @GetMapping("/key/{key}")
    @JsonView(Views.Minimal::class)
    fun get(
        @AuthenticationPrincipal user: User?,
        @PathVariable key: String
    ): RestResponse {
        return ResponseFactory.ok("test", testService.getTestForRespondent(user, key))
    }

    @PostMapping
    @JsonView(Views.Minimal::class)
    fun create(
        @RequestBody test: Test,
        @AuthenticationPrincipal user: User
    ): RestResponse {
        test.creator = user
        return ResponseFactory.ok("test", testService.create(test))
    }

    @DeleteMapping("{key}")
    @JsonView(Views.Minimal::class)
    fun delete(
        @AuthenticationPrincipal user: User,
        @PathVariable key: String
    ): RestResponse {
        return ResponseFactory.ok("testId", testService.delete(user, key))
    }
}
