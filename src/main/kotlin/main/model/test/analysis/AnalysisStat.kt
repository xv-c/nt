package main.model.test.analysis

import com.fasterxml.jackson.annotation.JsonView
import main.model.test.test.TestAnswerVariant
import main.model.test.test.TestQuestion
import main.util.Views
import javax.persistence.*

@Entity
class AnalysisStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @OneToOne
    @JsonView(Views.Minimal::class)
    lateinit var forQuestion: TestQuestion

    @OneToOne
    @JsonView(Views.Minimal::class)
    lateinit var forAnswer : TestAnswerVariant

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonView(Views.Minimal::class)
    var counts : MutableList<AnalysisCount> = mutableListOf()
}