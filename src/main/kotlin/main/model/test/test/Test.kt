package main.model.test.test

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import main.model.User
import main.model.test.analysis.AnalysisResult
import main.util.Views
import java.sql.Date
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
    lateinit var creationDate: Date

    @JsonView(Views.Minimal::class)
    lateinit var name : String

    @JsonView(Views.Minimal::class)
    lateinit var description : String

    @JsonView(Views.Minimal::class)
    var loginRequired = false

    @JsonView(Views.Minimal::class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var isAnalysed = false

    @JsonView(Views.Minimal::class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var isAnalysing = false

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    var analysisResult : AnalysisResult? = null

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonView(Views.Minimal::class)
    lateinit var questions: List<TestQuestion>
}