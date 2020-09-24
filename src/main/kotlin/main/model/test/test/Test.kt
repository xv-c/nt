package main.model.test.test

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.util.Views
import javax.persistence.*

@Entity
class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @JsonView(Views.Minimal::class)
    var key: String = ""

    @ManyToOne
    @JsonView(Views.Minimal::class)
    var creator: User? = null

    @JsonView(Views.Minimal::class)
    var name: String = ""

    @JsonView(Views.Minimal::class)
    var description: String = ""

    @JsonView(Views.Minimal::class)
    var loginRequired: Boolean = false

    @OneToMany
    @JsonView(Views.Minimal::class)
    var questions: List<TestQuestion> = ArrayList()
}