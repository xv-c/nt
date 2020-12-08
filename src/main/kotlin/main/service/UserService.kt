package main.service

import main.exceptions.ServiceException
import main.model.User
import main.repo.UserRepo
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(val userRepo: UserRepo, val passwordEncoder: PasswordEncoder) : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails {
        return userRepo.findByUsername(userName) ?: throw UsernameNotFoundException("Пользователь $userName не найден")
    }

    fun isValid(username: String, nickname: String, password: String): String {
        var errors = ""
        if (username.isEmpty())
            errors += "Почта не может быть пустой\r\n"

        val emailRegex = Regex("^[a-zA-Z0-9_!#\$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$")
        if (!username.matches(emailRegex))
            errors += "Почта введена некорректно\r\n"

        if (nickname.isEmpty())
            errors += "Имя пользователя не может быть пустым\r\n"
        if (nickname.length < 5)
            errors += "Имя пользователя должно содержать не менее, чем 5 символов\r\n"
        if (nickname.length > 20)
            errors += "Имя пользователя должно содержать не более, чем 20 символов\r\n"

        if (password.isEmpty())
            errors += "Пароль не может быть пустым\r\n"
        if (password.length < 8)
            errors += "Пароль должен содержать не менее, чем 8 символов\r\n"
        if (password.length > 64)
            errors += "Пароль должен содержать не более, чем 64 символа\r\n"

        return errors
    }

    fun saveUser(username: String, nickname: String, password: String): User {
        val errors = isValid(username, nickname, password)
        if (errors.isNotEmpty())
            throw ServiceException(errors)

        var user = userRepo.findByUsername(username.toLowerCase())

        if (user != null)
            throw ServiceException("Пользователь с такой почтой уже зарегистрирован")

        user = User()
        user.username = username.toLowerCase()
        user.nickname = nickname
        user.password = passwordEncoder.encode(password)
        user.roles.add(User.Roles.USER)
        return userRepo.save(user)
    }
}