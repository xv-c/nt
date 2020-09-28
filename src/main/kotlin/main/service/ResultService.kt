package main.service

import main.model.User
import main.model.test.result.TestResult
import main.model.test.result.TestResultAnswer
import main.model.test.test.Test
import main.model.test.test.TestQuestion
import main.repo.TestAnswerVariantRepo
import main.repo.TestRepo
import main.repo.TestResultAnswerRepo
import main.repo.TestResultRepo
import org.hibernate.service.spi.ServiceException
import org.springframework.boot.json.JsonParserFactory
import org.springframework.stereotype.Service

@Service
class ResultService(
        val testRepo: TestRepo,
        val testResultRepo: TestResultRepo,
        val testAnswerVariantRepo: TestAnswerVariantRepo,
        val testResultAnswerRepo: TestResultAnswerRepo
) {

    fun getResults(test: Test): List<TestResult> {
        return testResultRepo.findByTest(test)
    }

    fun parseResult(resultJson: String, test: Test, user: User?): TestResult {
        val testResult = TestResult()
        val answersList = JsonParserFactory.getJsonParser().parseList(resultJson)

        if (answersList.size != test.questions.size)
            throw ServiceException("Количество ответов не совпадает с количеством вопросов в опросе")

        testResult.answers = mutableListOf()
        for (index in 0 until answersList.size) {
            when (test.questions[index].type) {
                TestQuestion.QuestionType.MULTI -> {
                    val answerParsed = (answersList[index] as Map<*, *>)["value"] as ArrayList<Int>
                    val answer = TestResultAnswer()
                    answer.question = test.questions[index]
                    answer.answers = mutableListOf()
                    for (answerVariant in answerParsed) {
                        answer.answers!!.add(test.questions[index].variants!![answerVariant])
                    }
                    testResult.answers!!.add(testResultAnswerRepo.save(answer))
                }
                TestQuestion.QuestionType.ONE -> {
                    val answerParsed = (answersList[index] as Map<*, *>)["value"] as Int
                    val answer = TestResultAnswer()
                    answer.question = test.questions[index]
                    answer.answers = mutableListOf()
                    answer.answers!!.add(test.questions[index].variants!![answerParsed])
                    testResult.answers!!.add(testResultAnswerRepo.save(answer))
                }
                TestQuestion.QuestionType.TEXT -> {
                    val answer = TestResultAnswer()
                    answer.question = test.questions[index]
                    answer.answer = (answersList[index] as Map<*, *>)["value"] as String
                    testResult.answers!!.add(testResultAnswerRepo.save(answer))
                }
            }
        }

        if (test.loginRequired)
            testResult.respondent = user


        testResult.test = test
        return testResult
    }

    fun save(testResult: TestResult): TestResult {
        return testResultRepo.save(testResult)
    }
}