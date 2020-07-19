package main.service

import main.repo.UserRepo
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserService(val userRepo: UserRepo):UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails {
        return userRepo.findByUsername(userName)
    }

}