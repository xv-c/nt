package main.service

import main.exceptions.RestException
import main.model.User
import main.model.test.result.TestResult
import main.model.test.result.TestResultAnswer
import main.model.test.test.Test
import main.model.test.test.TestQuestion
import main.repo.TestAnswerVariantRepo
import main.repo.TestRepo
import main.repo.TestResultAnswerRepo
import main.repo.TestResultRepo
import org.springframework.boot.json.JsonParserFactory
import org.springframework.stereotype.Service

@Service
class ResultService(
    val testResultRepo: TestResultRepo
) {

    fun getResults(test: Test): List<TestResult> {
        return testResultRepo.findByTest(test)
    }

    fun parseResult(resultJson: String, test: Test, user: User?): TestResult {
        val testResult = TestResult()
        val answersList = JsonParserFactory.getJsonParser().parseList(resultJson)

        if (answersList.size != test.questions.size)
            throw RestException("Количество ответов не совпадает с количеством вопросов в опросе")

        testResult.answers = mutableListOf()
        for (index in 0 until answersList.size) {
            val answer = TestResultAnswer()
            val mapped = answersList[index] as Map<*, *>
            when (test.questions[index].type) {
                TestQuestion.QuestionType.MULTI -> {
                    val answerParsed = mapped["value"] as ArrayList<Int>
                    answer.question = test.questions[index]
                    answer.answers = mutableListOf()
                    for (answerVariant in answerParsed) {
                        answer.answers!!.add(test.questions[index].variants!![answerVariant])
                    }
                }
                TestQuestion.QuestionType.ONE -> {
                    val answerParsed = mapped["value"] as Int
                    answer.question = test.questions[index]
                    answer.answers = mutableListOf()
                    answer.answers!!.add(test.questions[index].variants!![answerParsed])
                }
                TestQuestion.QuestionType.TEXT -> {
                    answer.question = test.questions[index]
                    answer.answer = mapped["value"] as String
                }
            }
            testResult.answers.add(answer)
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