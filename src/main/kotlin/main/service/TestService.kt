package main.service

import main.exceptions.RestException
import main.model.User
import main.model.test.test.Test
import main.repo.TestRepo
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class TestService(
    var userService: UserService,
    var testResultService: TestResultService,
    var testRepo: TestRepo
) {

    fun getUserTests(user: User): MutableList<Test> {
        return testRepo.findByCreator(userService.get(user.id))
    }

    fun get(key: String): Test {
        return testRepo.findByKey(key.toUpperCase()) ?: throw RestException("Не удалось найти опрос с таким ключом")
    }

    fun getTestForCreator(user: User?, key: String): Test {
        val test = get(key)
        if (test.creator == user)
            return test
        else
            throw RestException("Недостаточно прав для просмотра результатов")
    }

    fun getTestForRespondent(user: User?, key: String): Test {
        val test = get(key)

        if (test.creator == user)
            return test

        if (test.loginRequired) {
            if (user == null)
                throw RestException("Для данного опроса необходимо авторизоваться")
            else if (testResultService.isVisited(user, test))
                throw RestException("Вы уже проходили этот опрос")
        }

        return test
    }

    fun save(test: Test): Test {
        return testRepo.save(test)
    }

    fun create(test: Test): Test {
        test.creationDate = LocalDateTime.now(ZoneId.of("Europe/Moscow"))
        while (true) {
            val key = UUID.randomUUID().toString().substring(0..17).toUpperCase()
            try {
                get(key)
            } catch (e: RestException) {
                test.key = key
                break
            }
        }
        return save(test)
    }

    fun delete(user: User, key: String): Long {
        val test = get(key)
        return if (test.creator != user)
            -1
        else {
            testResultService.get(test).forEach {
                testResultService.delete(it)
            }
            testRepo.delete(test)
            test.id
        }
    }

    fun getLastPublic(): List<Test> {
        return testRepo.findTop10ByPublicTrueOrderByCreationDateDesc()
    }

    fun search(name: String): List<Test> {
        return testRepo.findByNameLike(name)
    }
}