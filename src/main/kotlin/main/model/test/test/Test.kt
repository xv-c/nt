package main.model.test.test

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.model.test.analysis.AnalysisResult
import main.util.Views
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal::class)
    var id = 0L

    @JsonView(Views.Minimal::class)
    lateinit var key : String

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(Views.UserViews.UserComplete::class)
    lateinit var creator: User

    @JsonView(Views.Minimal::class)
    lateinit var creationDate: LocalDateTime

    @JsonView(Views.Minimal::class)
    lateinit var name : String

    @JsonView(Views.Minimal::class)
    lateinit var description : String

    @JsonView(Views.Minimal::class)
    var loginRequired = false

    @JsonView(Views.Minimal::class)
    var public = false

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonView(Views.Minimal::class)
    lateinit var questions: List<TestQuestion>

    @JsonView(Views.Minimal::class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var analysed = false

    @JsonView(Views.Minimal::class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var analysing = false

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var analysisResult : AnalysisResult? = null
}