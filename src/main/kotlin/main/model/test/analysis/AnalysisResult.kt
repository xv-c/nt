package main.model.test.analysis

import com.fasterxml.jackson.annotation.JsonView
import main.util.Views
import javax.persistence.*

@Entity
class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal::class)
    var id: Long = 0

    @JsonView(Views.Minimal::class)
    var total = 0L

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonView(Views.Minimal::class)
    var stats : MutableList<AnalysisStat> = mutableListOf()
}