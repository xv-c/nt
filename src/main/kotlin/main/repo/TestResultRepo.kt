package main.repo

import main.model.TestResult
import org.springframework.data.jpa.repository.JpaRepository

interface TestResultRepo : JpaRepository<TestResult, Long> {
    fun find
}