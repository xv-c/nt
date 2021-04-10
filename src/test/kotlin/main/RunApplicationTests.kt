package main

import main.controllers.TestController
import main.controllers.UsersController
import main.repo.UserRepo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert
import kotlin.random.Random

@SpringBootTest
@AutoConfigureMockMvc
class RunApplicationTests {

	@Autowired
	lateinit var usersController: UsersController
	@Autowired
	lateinit var testController: TestController
	@Autowired
	lateinit var userRepo: UserRepo

	fun assertThrows(func: () -> Unit) {
		try{
			func.invoke()
		}catch(e: Exception){
			return
		}
		throw Exception("Function didnt throw an exception")
	}

	fun assertNotThrows(func: () -> Unit) {
		try{
			func.invoke()
			return
		}catch(e: Exception){
			throw Exception("Function throw an exception\n${e.printStackTrace()}")
		}
	}

	@Test
	fun registerTest() {
		val rand = Random.Default
		assertThrows {
			usersController.registerUser("test", "test", "test")
		}
		assertThrows {
			usersController.registerUser("test${rand.nextInt()}@mail.ru", "test", "test")
		}
		assertNotThrows {
			usersController.registerUser("test${rand.nextInt()}@mail.ru", "tested", "testtest")
		}
	}

	@Test
	fun testCreationTest() {
		assertNotThrows {
			testController.createTest(
				"[{\"type\":\"MULTI\",\"question\":\"test\",\"variants\":[{\"text\":\"test\"},{\"text\":\"test\"}]}]",
				"test123",
				true,
				"test123",
				userRepo.findAll()[0]
			)
		}
	}

}
