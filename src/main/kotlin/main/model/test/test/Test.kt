package main.model.test.test

import com.fasterxml.jackson.annotation.JsonView
import jdk.jfr.Timestamp
import main.model.User
import main.util.Views
import java.sql.Date
import javax.persistence.*

@Entity
class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Minimal::class)
    var id = 0L

    @JsonView(Views.Minimal::class)
    lateinit var key : String

    @ManyToOne
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

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonView(Views.Minimal::class)
    lateinit var questions: List<TestQuestion>
}