package main.repo

import main.model.User
import main.model.test.result.TestResult
import main.model.test.test.Test
import org.springframework.data.jpa.repository.JpaRepository

interface TestResultRepo : JpaRepository<TestResult, Long> {
    fun findByRespondentAndTest(respondent: User, test: Test): TestResult?
    fun findByTest(test: Test): List<TestResult>
}