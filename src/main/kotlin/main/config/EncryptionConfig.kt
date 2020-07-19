package main.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class EncryptionConfig {
    val passwordEncoder: PasswordEncoder
    @Bean
        get() = BCryptPasswordEncoder(8)
}