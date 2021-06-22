package main.controller

import main.model.User
import main.repo.UserRepo
import main.util.Endpoints
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(Endpoints.HOME)
class HomeController(private val userRepo: UserRepo) {

    @GetMapping
    fun hello(
            @AuthenticationPrincipal user: User?,
            model: Model
    ): String {

        if (user != null)
            model.addAttribute("user", userRepo.findByUsername(user.username))

        return "index"
    }
}