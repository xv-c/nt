package main.repo

import main.model.test.test.TestAnswerVariant
import org.springframework.data.jpa.repository.JpaRepository

interface TestAnswerVariantRepo : JpaRepository<TestAnswerVariant, Long>