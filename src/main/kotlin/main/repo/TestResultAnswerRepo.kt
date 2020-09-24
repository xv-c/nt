package main.repo

import main.model.test.result.TestResultAnswer
import org.springframework.data.jpa.repository.JpaRepository

interface TestResultAnswerRepo : JpaRepository<TestResultAnswer, Long>