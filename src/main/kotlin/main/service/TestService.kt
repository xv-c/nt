package main.service

import main.exceptions.RestException
import main.model.User
import main.model.test.test.Test
import main.model.test.test.TestAnswerVariant
import main.model.test.test.TestQuestion
import main.repo.*
import org.springframework.boot.json.JsonParserFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

@Service
class TestService(var userRepo: UserRepo, var testRepo: TestRepo, var testQuestionRepo: TestQuestionRepo, var testResultRepo: TestResultRepo, var testAnswerVariantRepo: TestAnswerVariantRepo) {

    fun getUserTests(user: User): MutableList<Test> {
        return testRepo.findByCreator(userRepo.findByIdOrNull(user.id)!!)
    }

    fun getTest(key: String): Test {
        return testRepo.findByKey(key.toUpperCase()) ?: throw RestException("Не удалось найти опрос с таким ключом")
    }

    fun getTestForCreator(user: User?, key: String): Test {
        val test = getTest(key)
        if (test.creator == user)
            return test
        else
            throw RestException("Недостаточно прав для просмотра результатов")
    }

    fun getTestForRespondent(user: User?, key: String): Test {
        val test = getTest(key)

        if (test.creator == user)
            return test

        if (test.loginRequired) {
            if (user == null)
                throw RestException("Для данного опроса необходимо авторизоваться")
            else if (testResultRepo.findByRespondentAndTest(user, test) != null)
                throw RestException("Вы уже проходили этот опрос")
        }

        return test
    }

    fun save(name: String, description: String, loginRequired: Boolean, validQuestions: ArrayList<TestQuestion>, user: User): Test {
        val questions = ArrayList<TestQuestion>()
        validQuestions.forEach {
            val variants = ArrayList<TestAnswerVariant>()
            if (it.type != TestQuestion.QuestionType.TEXT) {
                it.variants!!.forEach { variant ->
                    variants.add(testAnswerVariantRepo.save(variant))
                }
                it.variants = variants
            }
        }

        val test = Test()
        test.name = name
        test.loginRequired = loginRequired
        test.description = description
        test.creator = user
        test.questions = questions
        test.creationDate = Date(Calendar.getInstance().timeInMillis)

        while (true) {
            val key = UUID.randomUUID().toString().substring(0..17).toUpperCase()
            if (testRepo.findByKey(key) == null) {
                test.key = key
                break
            }
        }

        return testRepo.save(test)
    }

    fun parseQuestions(testQuestionsJson: String): ArrayList<TestQuestion> {
        val validQuestions = ArrayList<TestQuestion>()
        JsonParserFactory.getJsonParser().parseList(testQuestionsJson).forEach loop@{ jsonQuestion ->
            val questionMap = jsonQuestion as Map<*, *>
            val question = TestQuestion()

            question.question = questionMap["question"] as String
            if (question.question.isEmpty() || question.question.length > 200)
                throw RestException("Вопрос не может быть пустым и не может превышать длину в 200 символов")
            try {
                question.type = TestQuestion.QuestionType.valueOf(questionMap["type"] as String)
            } catch (e: Exception) {
                throw RestException("Недопустимый тип вопроса")
            }

            if (question.type != TestQuestion.QuestionType.TEXT) {
                val validVariants = ArrayList<TestAnswerVariant>()

                if ((questionMap["variants"] as ArrayList<*>).size == 0)
                    throw RestException("Вопрос данного типа обязан иметь хотя бы один вариант ответа")

                (questionMap["variants"] as ArrayList<*>).forEach { jsonVariant ->
                    val variantMap = jsonVariant as Map<*, *>
                    val variant = TestAnswerVariant()
                    variant.value = variantMap["text"] as String

                    if (variant.value.isEmpty() ||
                            variant.value.length > 50)
                        throw RestException("Вариант ответа не может быть пустым и не может превышать длину в 50 символов")
                    validVariants.add(variant)
                }
                question.variants = validVariants
            }
            validQuestions.add(question)
        }
        return validQuestions
    }

    fun remove(user: User, key: String): Long {
        val test = getTest(key)
        return if (test.creator != user) -1
        else {
            testResultRepo.findByTest(test).forEach{
                testResultRepo.delete(it)
            }
            testRepo.delete(test)
            test.id
        }
    }
}