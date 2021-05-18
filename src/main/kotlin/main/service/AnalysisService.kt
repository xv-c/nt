package main.service

import main.exceptions.RestException
import main.model.test.analysis.AnalysisCount
import main.model.test.analysis.AnalysisResult
import main.model.test.analysis.AnalysisStat
import main.model.test.result.TestResult
import main.model.test.test.Test
import main.model.test.test.TestQuestion
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AnalysisService(
    var testService: TestService,
    var testResultService: TestResultService
) {
    @Value("\${flask_url}")
    lateinit var flaskUrl: String

    private class FlaskResponse(val positive: Int)

    fun isPositive(sentence: String): Boolean {
        val template = RestTemplate()
        val response = template.postForEntity(
            flaskUrl,
            HttpEntity<HashMap<String, String>>(hashMapOf("text" to sentence)),
            FlaskResponse::class.java
        )
        return response.body!!.positive == 1
    }

    fun analyseText(test: Test, results: List<TestResult>) {
        test.questions.forEachIndexed { index, testQuestion ->
            if (testQuestion.type == TestQuestion.QuestionType.TEXT) {
                results.forEach { result ->
                    result.answers[index].tonality = isPositive(result.answers[index].answer!!)
                }
            }
        }
    }

    fun analyseSelects(test: Test, results: List<TestResult>) {
        if (test.analysisResult == null)
            throw Exception("Ошибка при анализе")
        else {
            val analysisResult = test.analysisResult!!
            val filteredQuestions = test.questions.filter { it.type != TestQuestion.QuestionType.TEXT }

            analysisResult.total = results.size.toLong()

            for (question in filteredQuestions) {
                for (variant in question.variants!!) {
                    val stat = AnalysisStat()
                    stat.forQuestion = question
                    stat.forAnswer = variant

                    for (child in filteredQuestions) {
                        for (childVariant in child.variants!!) {
                            if (childVariant.id == variant.id)
                                continue

                            val count = AnalysisCount()
                            count.forQuestion = child
                            count.forAnswer = childVariant
                            stat.counts.add(count)
                        }
                    }

                    analysisResult.stats.add(stat)
                }
            }

            for (result in results) {
                val filteredAnswers = result.answers.filter { it.question.type != TestQuestion.QuestionType.TEXT }
                for (answer in filteredAnswers) {
                    for (variant in answer.answers!!) {
                        val stat = analysisResult
                            .stats
                            .first {
                                it.forQuestion.id == answer.question.id
                                        && variant.id == it.forAnswer.id
                            }
                        for (childAnswer in filteredAnswers) {
                            for (childVariant in childAnswer.answers!!) {
                                if (childVariant.id == variant.id)
                                    continue

                                val count = stat.counts.first { it.forAnswer.id == childVariant.id }
                                count.count = count.count + 1
                            }
                        }
                    }
                }
            }
        }
    }

    fun start(testKey: String) {
        var test = testService.get(testKey)
        test.isAnalysing = true
        test = testService.save(test)
        val results = testResultService.get(test)

        test.analysisResult = AnalysisResult()
        analyseSelects(test, results)
        analyseText(test, results)

        test.isAnalysed = true
        test.isAnalysing = false
        testService.save(test)
    }

    fun get(testKey: String): AnalysisResult {
        return testService.get(testKey).analysisResult ?: throw RestException("Анализ для этого теста еще не выполнен")
    }
}