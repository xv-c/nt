package main.service

import main.model.Test
import main.model.TestAnswerVariant
import main.model.TestResult
import main.model.User
import main.repo.TestAnswerVariantRepo
import main.repo.TestRepo
import main.repo.TestResultRepo
import org.hibernate.service.spi.ServiceException
import org.springframework.boot.json.JsonParserFactory
import org.springframework.stereotype.Service

@Service
class ResultService(val testRepo: TestRepo, val testResultRepo: TestResultRepo, val testAnswerVariantRepo: TestAnswerVariantRepo) {

    fun parseResult(resultJson: String, test: Test, user: User): TestResult {
        val testResult = TestResult()
        val answersList = JsonParserFactory.getJsonParser().parseList(resultJson)

        if (answersList.size != test.questions.size)
            throw ServiceException("Количество ответов не совпадает с количеством вопросов в опросе")

        for (index in 0 until answersList.size) {
            val variant = TestAnswerVariant()
            variant.value = JsonParserFactory.getJsonParser().parseMap(answersList[index].toString())["value"].toString()
            testResult.answers[test.questions.elementAt(index)] = testAnswerVariantRepo.save(variant)
        }

        if (test.loginRequired)
            testResult.respondent = user

        testResult.test
        return testResult
    }

    fun save(testResult: TestResult): TestResult {
        return testResultRepo.save(testResult)
    }
}