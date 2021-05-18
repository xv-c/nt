package main.model.test.test

import com.fasterxml.jackson.annotation.JsonView
import main.util.Views
import javax.persistence.*

@Entity
class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @JsonView(Views.Minimal::class)
    var question: String = ""

    @JsonView(Views.Minimal::class)
    var type: QuestionType = QuestionType.ONE

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonView(Views.Minimal::class)
    var variants: MutableList<TestAnswerVariant>? = null

    enum class QuestionType{
        ONE, MULTI, TEXT
    }
}