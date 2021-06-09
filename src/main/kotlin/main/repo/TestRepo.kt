package main.repo

import main.model.User
import main.model.test.test.Test
import org.springframework.data.jpa.repository.JpaRepository

interface TestRepo : JpaRepository<Test, Long> {
    fun findByCreator(user: User): MutableList<Test>
    fun findByKey(key: String): Test?
    fun findTop10ByPublicTrueOrderByCreationDateDesc() : List<Test>
    fun findByNameLike(name: String): List<Test>
}