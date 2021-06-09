package main.thirdmodule

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestFormTests {

    fun sleep() {
        val random = Random()
        Thread.sleep(random.nextInt(5000).toLong())
    }

    @Test
    fun testTestSearch(){
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testTestAnswer(){
        sleep()
        sleep()
        sleep()
        sleep()
        sleep()
        sleep()
        sleep()
        sleep()
        sleep()
        sleep()
    }
}