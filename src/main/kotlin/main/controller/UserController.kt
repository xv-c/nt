package main.controller

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.service.UserService
import main.util.Endpoints
import main.util.ResponseFactory
import main.util.RestResponse
import main.util.Views
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Endpoints.USERS)
class UserController(private var userService: UserService) {

    @PutMapping("/password")
    @JsonView(Views.Minimal::class)
    fun edit(
        @AuthenticationPrincipal user: User,
        password: String
    ) : RestResponse {
        userService.editPassword(user, password)
        return ResponseFactory.ok()
    }

    @PutMapping("/profile")
    @JsonView(Views.Minimal::class)
    fun edit(
        @AuthenticationPrincipal user: User,
        username: String,
        nickname: String
    ) : RestResponse {
        userService.editProfile(user, username, nickname)
        return ResponseFactory.ok()
    }

    @PostMapping
    @JsonView(Views.Minimal::class)
    fun registerUser(
        @RequestParam username: String,
        @RequestParam nickname: String,
        @RequestParam password: String
    ): RestResponse {
        return ResponseFactory.ok("user", userService.create(username, nickname, password))
    }
}