package main.service

import main.model.TestResult
import main.repo.TestRepo
import org.springframework.stereotype.Service

@Service
class ResultService(val testRepo: TestRepo, val testResultRepo: TestRe) {
    fun parseResult(resultJson: String): TestResult {

    }

    fun save(testResult: TestResult): TestResult {

    }
}