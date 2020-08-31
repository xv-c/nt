package main.service

import main.model.Test
import main.model.TestAnswerVariant
import main.model.TestQuestion
import main.model.User
import main.repo.TestAnswerVariantRepo
import main.repo.TestQuestionRepo
import main.repo.TestRepo
import main.repo.UserRepo
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList

@Service
class TestService(var userRepo: UserRepo, var testRepo: TestRepo, var testQuestionRepo: TestQuestionRepo, var testAnswerVariantRepo: TestAnswerVariantRepo) {

    fun createTest(name: String, description: String, loginRequired: Boolean, testQuestions: ArrayList<TestQuestion>, user: User): Test {
        val questions = ArrayList<TestQuestion>()
        testQuestions.forEach {
            val variants = ArrayList<TestAnswerVariant>()
            if (it.type != TestQuestion.QuestionType.TEXT) {
                it.variants!!.forEach { variant ->
                    variants.add(testAnswerVariantRepo.save(variant))
                }
                it.variants = variants
            }
        }
        testQuestions.forEach {
            questions.add(testQuestionRepo.save(it))
        }

        val test = Test()
        test.name = name
        test.loginRequired = loginRequired
        test.description = description
        test.creator = user
        test.testQuestions = questions
        while (true) {
            val key = UUID.randomUUID().toString().substring(0..17)
            if (testRepo.findByKey(key) == null) {
                test.key = key
                break
            }
        }

        return testRepo.save(test)
    }

    fun getUserTests(user: User): MutableList<Test> {
        return testRepo.findByCreator(userRepo.findByIdOrNull(user.id)!!)
    }

    fun getTest(user: User, key: String): Test {

    }
}