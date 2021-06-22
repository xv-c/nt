package main.service

import main.exception.RestException
import main.model.User
import main.repo.UserRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(val userRepo: UserRepo, val passwordEncoder: PasswordEncoder) : UserDetailsService {

    fun get(id: Long): User {
        return userRepo.findByIdOrNull(id) ?: throw RestException("Пользователь с ID <${id}> не найден")
    }

    fun editProfile(user: User, username: String, nickname: String) : User {
        checkUsername(username, user)
        checkNickname(nickname)

        val userFromDb = get(user.id)
        userFromDb.username = username
        userFromDb.nickname = nickname
        return userRepo.save(userFromDb)
    }

    fun editPassword(user: User, password: String) : User {
        checkPassword(password)

        val userFromDb = get(user.id)
        userFromDb.password = passwordEncoder.encode(password)

        return userRepo.save(userFromDb)
    }

    fun create(username: String, nickname: String, password: String): User {
        checkUsername(username)
        checkNickname(nickname)
        checkPassword(password)

        val user = User()
        user.username = username
        user.nickname = nickname
        user.password = passwordEncoder.encode(password)
        user.roles.add(User.Roles.USER)
        return userRepo.save(user)
    }

    override fun loadUserByUsername(userName: String): User {
        return userRepo.findByUsername(userName.toLowerCase()) ?: throw UsernameNotFoundException("Пользователь $userName не найден")
    }

    fun checkUsername(username: String, user: User? = null){
        try {
            val userFromDb = loadUserByUsername(username)

            if (user == null || user.id != userFromDb.id)
                throw RestException("Пользователь с такой почтой уже зарегистрирован")
        } catch (e: UsernameNotFoundException){
        }

        var errors = ""
        if (username.isEmpty())
            errors += "Почта не может быть пустой\r\n"

        val emailRegex = Regex("^[a-zA-Z0-9_!#\$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$")
        if (!username.matches(emailRegex))
            errors += "Почта введена некорректно\r\n"

        if (errors.isNotEmpty())
            throw RestException(errors)
    }

    fun checkPassword(password: String) {
        var errors = ""
        if (password.isEmpty())
            errors += "Пароль не может быть пустым\r\n"
        if (password.length < 8)
            errors += "Пароль должен содержать не менее, чем 8 символов\r\n"
        if (password.length > 64)
            errors += "Пароль должен содержать не более, чем 64 символа\r\n"

        if (errors.isNotEmpty())
            throw RestException(errors)
    }

    fun checkNickname(nickname: String){
        var errors = ""
        if (nickname.isEmpty())
            errors += "Имя пользователя не может быть пустым\r\n"
        if (nickname.length < 5)
            errors += "Имя пользователя должно содержать не менее, чем 5 символов\r\n"
        if (nickname.length > 20)
            errors += "Имя пользователя должно содержать не более, чем 20 символов\r\n"

        if (errors.isNotEmpty())
            throw RestException(errors)
    }
}