package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.service.UserService
import main.util.ResponseFactory
import main.util.Views
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UsersController(private var userService: UserService) {

    @PostMapping
    @JsonView(Views.Minimal::class)
    fun registerUser(
            @RequestParam username: String,
            @RequestParam nickname: String,
            @RequestParam password: String):
            ResponseEntity<*> {
        val user = userService.saveUser(username, nickname, password)
        return ResponseFactory.ok("user", user)
    }
}