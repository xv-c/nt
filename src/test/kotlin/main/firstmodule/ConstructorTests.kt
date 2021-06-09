package main.firstmodule

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ConstructorTests {
    fun sleep() {
        val random = Random()
        Thread.sleep(random.nextInt(5000).toLong())
    }

    @Test
    fun testConstructorOpening() {
        sleep()
    }

    @Test
    fun testConstructorFields() {
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testCreationTest() {
        sleep()
        sleep()
        sleep()
        sleep()
        sleep()
    }
}