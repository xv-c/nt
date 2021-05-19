<template>
  <div>
    <v-toolbar tile flat>
      <v-btn @click="startAnalysis" text>
        выполнить анализ
      </v-btn>
      <v-btn text @click="getPptx">
        скачать как&nbsp;<b style="color: #D35230">pptx</b>
      </v-btn>
    </v-toolbar>

    <v-divider/>

    <div v-if="!test.isAnalysed">
      <v-card-title>
        Анализ не проведен
      </v-card-title>
    </div>

    <v-tabs v-else hide-slider grow v-model="activeTab">
      <v-tab>Нетекстовые ответы</v-tab>
      <v-tab>Текстовые ответы</v-tab>

      <v-tab-item :transition="false" :reverse-transition="false">
        <div v-if="nonTextChartsData.length === 0">
          <v-card-title>
            Не удалось выявить зависимости
          </v-card-title>
        </div>

        <div
            v-for="(data, index) in nonTextChartsData"
        >
          <v-card-title class="pb-0">
              <span style="color: grey">
                {{ data.title }}
              </span>
          </v-card-title>
          <chart
              :chart-data="data.variants"
              :title="''"
              :total-results="data.total"
              :id="index"
              ref="nonTextChartsData"
          />
        </div>
      </v-tab-item>
      <v-tab-item :transition="false" :reverse-transition="false">
        <div
            v-for="(data, index) in textChartsData"
        >
          <v-card-title class="pb-0">
              <span style="color: grey">
                {{ data.title }}
              </span>
            <v-spacer/>
            <v-btn class="ma-4" color="blue" outlined @click="data.details = !data.details">подробнее</v-btn>
          </v-card-title>
          <chart
              v-if="!data.details"
              :chart-data="data.variants"
              :title="data.title"
              :total-results="results.length"
              :id="index"
              ref="nonTextChartsData"
          />
          <v-data-table
              v-else
              :headers="[{text:'Текст',value: 'answer'}, {text: 'Тональность', value: 'tonality'}]"
              :items="getItemsFor(index)"
              :search="data.search"
          >
            <template v-slot:top>
              <v-divider/>
              <v-text-field
                  v-model="data.search"
                  class="pa-4"
                  outlined
                  single-line dense hide-details
              />
              <v-divider/>
            </template>
          </v-data-table>
        </div>
      </v-tab-item>
    </v-tabs>

  </div>
</template>

<script>
import api from "../../../../use/api";
import endpoints from "../../../../use/endpoints";
import {mapActions} from "vuex";
import Chart from "../charts/Chart.vue";
import PptxGenJS from "pptxgenjs";

export default {
  components: {Chart},
  props: ['test', 'results'],
  watch: {
    activeTab(newVal) {
      console.log(newVal)
    }
  },
  data() {
    return {
      activeTab: undefined,
      textActiveTab: undefined,
      analysis: undefined,
      nonTextChartsData: [],
      textChartsData: [],
    }
  },
  methods: {
    ...mapActions("app", ["showMessage"]),
    async getPptx() {
      let canvasPromises = undefined
      if (this.activeTab === 0)
        canvasPromises = this.$refs.nonTextChartsData.map(chart => chart.getImg(3));
      else
        canvasPromises = this.$refs.textextChartsData.map(chart => chart.getImg(3));

      await Promise.all(canvasPromises).then(canvases => {
        let pres = new PptxGenJS()
        let maxW = Math.max.apply(Math, canvases.map(c => c.width))
        let maxH = Math.max.apply(Math, canvases.map(c => c.height))

        pres.defineLayout({name: 'A33', width: maxW / 96, height: maxH / 96});
        pres.layout = 'A33'

        for (let i = 0; i < canvases.length; i++) {
          let canvas = canvases[i]
          let slide = pres.addSlide()
          slide.addImage({
            data: canvas.toDataURL(),
            w: `${parseInt(`${100.0 * canvas.width / maxW}`)}'%'`,
            h: `${parseInt(`${100.0 * canvas.height / maxH}`)}'%'`,
          })
        }
        pres.writeFile('presentation');
      })
    },
    getItemsFor(textIndex) {
      let index = -1
      let i = 0
      while (index !== textIndex) {
        if (this.test.questions[i].type === 'TEXT')
          index++
        if (index !== textIndex)
          i++
      }
      index = i
      let result = []

      for (let j = 0; j < this.results.length; j++) {
        let tonality = this.results[j].answers[index].tonality ? 'Позитивная' : 'Негативная'

        console.log("this.results[j]")
        console.log(this.results[j])
        console.log("this.results[j].answers[index]")
        console.log(this.results[j].answers[index])
        result.push({answer: this.results[j].answers[index].answer, tonality: tonality})
      }
      console.log("result")
      console.log(result)
      return result
    },
    loadAnalysis() {
      api.get(`${endpoints.analysis}${this.test.key}`)
          .then(resp => {
            if (resp.success) {
              this.analysis = resp.data.analysis
              this.prepareTextChartsData()
              this.prepareNonTextChartsData()
            } else {
              this.showMessage(resp.message)
            }
          })
    },
    startAnalysis() {
      this.maskModel = true
      api.post(endpoints.analysis + this.test.key)
          .then(data => {
            this.maskModel = false
            if (data.success) {
              this.showMessage("Анализ выполнен")
              this.$router.go()
            } else
              this.showMessage(data.message)
          })
    },
    flatData() {
      let flatted = []
      for (let i = 0; i < this.analysis.stats.length; i++) {
        let stat = this.analysis.stats[i]

        let statObj = {
          question: stat.forQuestion.question,
          answer: stat.forAnswer.value,
          counts: new Map()
        }

        for (let j = 0; j < stat.counts.length; j++) {
          let count = stat.counts[j]

          if (statObj.counts.has(count.forQuestion.id)) {
            let child = statObj.counts.get(count.forQuestion.id)
            child.total += count.count
            child.variants.push({
              total: count.count,
              answer: count.forAnswer.value
            })
          } else {
            statObj.counts.set(count.forQuestion.id, {
              total: count.count,
              question: count.forQuestion.question,
              variants: [{
                total: count.count,
                answer: count.forAnswer.value
              }]
            })
          }
        }

        flatted.push(statObj)
      }
      return flatted
    },
    prepareTextChartsData() {
      for (let i = 0; i < this.test.questions.length; i++) {
        let question = this.test.questions[i]

        if (question.type === "TEXT") {
          let positive = 0
          let negative = 0
          for (let j = 0; j < this.results.length; j++) {
            if (this.results[j].answers[i].tonality)
              positive++
            else
              negative++
          }
          this.textChartsData.push({
            title: `${question.question}`,
            details: false,
            variants: [
              {results: positive, value: 'Позитивные'},
              {results: negative, value: 'Негативные'}
            ]
          })
        }
      }
    },
    prepareNonTextChartsData() {
      let flat = this.flatData()
      for (let i = 0; i < flat.length; i++) {
        let data = flat[i]

        data.counts.forEach((value, key) => {
          if (value.total === 0)
            return

          let chartData = {
            title: `Пользователи, выбравшие "${data.answer}", в вопросе "${data.question}", в вопросе "${value.question}" выбирали:`,
            total: value.total,
            variants: []
          }

          for (let j = 0; j < value.variants.length; j++) {
            chartData.variants.push({
              results: value.variants[j].total,
              value: value.variants[j].answer
            })
          }
          this.nonTextChartsData.push(chartData)
        })
      }
      this.nonTextChartsData = this.nonTextChartsData.sort((it, other) => it.total - other.total)
    }
  },
  created() {
    if (this.test.isAnalysed) {
      this.loadAnalysis()
    }
  }
}
</script>

<style scoped>

</style>