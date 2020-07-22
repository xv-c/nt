package main.controllers

import main.model.User
import main.repo.UserRepo
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController(val userRepo: UserRepo) {

    @GetMapping
    fun hello(@AuthenticationPrincipal user: User?, model: Model): String {

        if (user != null) {
            model.addAttribute("user", userRepo.findByUsername(user.username))
        }

        return "index"
    }
}