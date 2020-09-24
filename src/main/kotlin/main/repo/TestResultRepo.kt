package main.repo

import main.model.test.test.Test
import main.model.test.result.TestResult
import main.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface TestResultRepo : JpaRepository<TestResult, Long> {
    fun findByRespondentAndTest(respondent: User, test: Test): TestResult?
}