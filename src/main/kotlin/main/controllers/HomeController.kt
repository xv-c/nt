package main.controllers

import main.repo.UserRepo
import main.model.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
class HomeController {

    @GetMapping
    fun hello(): String {
        return "index"
    }
}