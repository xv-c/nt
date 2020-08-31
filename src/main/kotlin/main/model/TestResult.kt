package main.model

import com.fasterxml.jackson.annotation.JsonView
import main.util.Views
import javax.persistence.*

@Entity
class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @ManyToOne
    @JsonView(Views.Minimal::class)
    var test: Test? = null

    @ManyToOne
    @JsonView(Views.Minimal::class)
    var respondent: User? = null

    @OneToMany
    @JsonView(Views.Minimal::class)
    var answers: Map<TestQuestion, TestAnswerVariant> = HashMap()
}