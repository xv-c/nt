package main.model.test.analysis

import com.fasterxml.jackson.annotation.JsonView
import main.model.test.test.TestAnswerVariant
import main.model.test.test.TestQuestion
import main.util.Views
import javax.persistence.*


@Entity
class AnalysisCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @JsonView(Views.Minimal::class)
    var count = 0L

    @ManyToOne
    @JsonView(Views.Minimal::class)
    lateinit var forQuestion: TestQuestion

    @ManyToOne
    @JsonView(Views.Minimal::class)
    lateinit var forAnswer: TestAnswerVariant
}