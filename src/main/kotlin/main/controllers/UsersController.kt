package main.controllers

import main.model.User
import main.repo.UserRepo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UsersController(var userRepo: UserRepo) {

    @GetMapping
    fun getUsers(): MutableList<User> {
        return userRepo.findAll()
    }

    @PostMapping
    fun hello(@RequestParam username: String, @RequestParam login: String, @RequestParam password: String): User {
        val user = User(null)

    }
}