package main.service

import main.model.TestResult
import main.repo.TestRepo
import main.repo.TestResultRepo
import org.springframework.boot.json.JsonParserFactory
import org.springframework.stereotype.Service

@Service
class ResultService(val testRepo: TestRepo, val testResultRepo: TestResultRepo) {
    fun parseResult(resultJson: String): TestResult {
        val testResult = TestResult()
        JsonParserFactory.getJsonParser()
        return testResult
    }

    fun save(testResult: TestResult): TestResult {
        return TestResult()
    }
}