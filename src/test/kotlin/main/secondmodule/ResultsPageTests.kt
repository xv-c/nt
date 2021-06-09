package main.secondmodule

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ResultsPageTests {
    fun sleep() {
        val random = Random()
        Thread.sleep(random.nextInt(5000).toLong())
    }

    @Test
    fun testResultsOpening(){
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testTestDeletion(){
        sleep()
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testTestOpening(){
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testTableSearch(){
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testTableSort(){
        sleep()
        sleep()
        sleep()
    }
}