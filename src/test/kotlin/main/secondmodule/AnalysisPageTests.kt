package main.secondmodule

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AnalysisPageTests {
    fun sleep() {
        val random = Random()
        Thread.sleep(random.nextInt(5000).toLong())
    }

    @Test
    fun testAnalysisOpening(){
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testDetailsPage(){
        sleep()
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun testAnalysisStart(){
        sleep()
        sleep()
        sleep()
    }

    @Test
    fun diagramsTests(){
        sleep()
        sleep()
        sleep()
    }
}