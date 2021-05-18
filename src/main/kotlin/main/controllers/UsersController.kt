package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.service.UserService
import main.util.Endpoints
import main.util.ResponseFactory
import main.util.Views
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Endpoints.USERS)
class UsersController(private var userService: UserService) {

    @PutMapping
    @JsonView
    fun editUser(
        @AuthenticationPrincipal user: User
    ) {
    }

    @PostMapping
    @JsonView(Views.Minimal::class)
    fun registerUser(
        @RequestParam username: String,
        @RequestParam nickname: String,
        @RequestParam password: String
    ): ResponseEntity<*> {
        return ResponseFactory.ok("user", userService.create(username, nickname, password))
    }
}