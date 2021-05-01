package main.repo

import main.model.State
import main.model.test.test.Test
import org.springframework.data.jpa.repository.JpaRepository

interface StateRepo : JpaRepository<State, Long>{
    fun findByTest(test: Test) : State
}