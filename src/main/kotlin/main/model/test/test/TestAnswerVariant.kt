package main.model.test.test

import com.fasterxml.jackson.annotation.JsonView
import main.util.Views
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class TestAnswerVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @JsonView(Views.Minimal::class)
    var value: String = ""
}