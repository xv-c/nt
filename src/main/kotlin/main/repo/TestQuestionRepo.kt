package main.repo

import main.model.test.test.TestQuestion
import org.springframework.data.jpa.repository.JpaRepository

interface TestQuestionRepo:JpaRepository<TestQuestion, Long>