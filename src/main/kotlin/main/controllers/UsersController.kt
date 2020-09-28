package main.controllers

import com.fasterxml.jackson.annotation.JsonView
import main.exceptions.ServiceException
import main.model.User
import main.service.UserService
import main.util.ResponseFactory
import main.util.Views
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController(var userService: UserService) {

    @PostMapping
    @JsonView(Views.Minimal::class)
    fun registerUser(@RequestParam username: String, @RequestParam nickname: String, @RequestParam password: String): ResponseEntity<*> {

        val user: User
        try {
            user = userService.saveUser(username, nickname, password)
        } catch (e: ServiceException) {
            return ResponseFactory.fail(e.message)
        }
        return ResponseFactory.build("user", user, true, HttpStatus.OK)
    }

    @GetMapping
    @JsonView(Views.Minimal::class)
    fun get():ResponseEntity<*>{
        return ResponseFactory.build("users", userService.get(), true, HttpStatus.OK )
    }
}