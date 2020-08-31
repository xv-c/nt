package main.repo

import main.model.Test
import main.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface TestRepo : JpaRepository<Test, Long> {
    fun findByCreator(user: User): MutableList<Test>
    fun findByKey(key: String): Test?
}