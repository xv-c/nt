package main.controllers

import main.repo.UserRepo
import main.model.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
class HomeController {

    @GetMapping
    fun hello(@AuthenticationPrincipal user:User?, model:Model): String {

        if(user!=null)
            model.addAttribute("user", user)

        return "index"
    }
}