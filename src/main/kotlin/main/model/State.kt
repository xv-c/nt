package main.model

import com.fasterxml.jackson.annotation.JsonView
import main.model.test.test.Test
import main.util.Views
import javax.persistence.*

@Entity
class State {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.Minimal::class)
    var id = 0L

    @OneToOne
    @JsonView(Views.ResultViews.ResultComplete::class)
    lateinit var test: Test

    @JsonView(Views.Minimal::class)
    var textState : Boolean = false

    @JsonView(Views.Minimal::class)
    var selectState : Boolean = false
}