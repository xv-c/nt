package main.firstmodule

import main.controllers.TestController
import main.controllers.UserController
import main.exceptions.RestException
import main.model.User
import main.model.test.test.TestAnswerVariant
import main.model.test.test.TestQuestion
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AuthenticationTests {

    fun sleep() {
        val random = Random()
        Thread.sleep(random.nextInt(5000).toLong())
    }

    @Test
    fun testUserCreation(){
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testUserAuthorization(){
        sleep()
        sleep()
    }

}
