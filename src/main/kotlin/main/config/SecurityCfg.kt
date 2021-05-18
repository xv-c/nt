package main.config

import com.fasterxml.jackson.databind.ObjectMapper
import main.exceptions.ExceptionsHandler
import main.exceptions.RestException
import main.service.UserService
import main.util.Endpoints
import main.util.ResponseFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import kotlin.math.log


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityCfg(
    private val resolver: ExceptionsHandler,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/").permitAll()

            .and()

            .formLogin { login ->
                login
                    .loginPage(Endpoints.LOGIN)

                    .failureHandler { _, resp, _ ->
                        resp.status = HttpStatus.BAD_REQUEST.value()
                        resp.contentType = "application/json;charset=UTF-8"

                        val body =
                            resolver.handleServiceException(RestException("Некорректная почта и/или пароль")).body
                        resp.writer.write(ObjectMapper().writeValueAsString(body))

                        resp.flushBuffer()
                    }

                    .successHandler { _, resp, _ ->
                        resp.status = HttpStatus.OK.value()
                        resp.contentType = "application/json;charset=UTF-8"

                        val body = ResponseFactory.ok().body
                        resp.writer.write(ObjectMapper().writeValueAsString(body))

                        resp.flushBuffer()
                    }


            }

            .logout { logout ->
                logout
                    .logoutUrl(Endpoints.LOGOUT)
                    .logoutSuccessUrl("/")
            }

            .oauth2Login()

            .and()

            .csrf().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService<UserDetailsService?>(userService)
            .passwordEncoder(passwordEncoder)
    }
}
