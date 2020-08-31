package main.repo

import main.model.TestAnswerVariant
import org.springframework.data.jpa.repository.JpaRepository

interface TestAnswerVariantRepo : JpaRepository<TestAnswerVariant, Long> {

}