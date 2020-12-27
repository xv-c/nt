package main.model.test.result

import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.model.test.test.Test
import main.util.Views
import javax.persistence.*

@Entity
class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @ManyToOne
    @JsonView(Views.ResultViews.ResultComplete::class)
    var test: Test? = null

    @ManyToOne
    @JsonView(Views.Minimal::class)
    var respondent: User? = null

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonView(Views.Minimal::class)
    var answers: MutableList<TestResultAnswer>? = null
}