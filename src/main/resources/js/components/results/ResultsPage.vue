<template>
  <div>
    <loading-mask :mask-model="maskModel"/>

    <v-row justify="center" v-if="test && results">
      <v-card tile width="950">
        <v-card-title>
          Результаты по опросу: {{ test.name }}<br/>
          Ответов: {{ results.length }}
        </v-card-title>
        <v-card-text>
          <v-row v-for="(data, index) in chartsData" :key="undefined"
                 style="margin-top: 20px" justify="center">

            <v-row style="margin: 10px 2%;">
              <span style="font-weight: bold; font-size: large">{{ test.questions[index].question }}</span>
              <v-spacer/>
              <span style="color: #5AACC7">Вопрос #{{ index + 1 }}</span>
            </v-row>

            <v-card v-if="data.type==='TEXT'"
                    tile
                    width="850">
              <v-card-title v-for="(answer, index) in data.results" :key="undefined">
                <v-chip :style="index>0?'margin-top:-3%':''">
                  {{ answer }}
                </v-chip>
              </v-card-title>
            </v-card>
            <chart v-if="data.type!=='TEXT'"
                   :total-results="results.length"
                   :chart-data="data.variants"/>
          </v-row>
        </v-card-text>
      </v-card>
    </v-row>
  </div>
</template>

<script>
import axios from 'axios'
import {mapActions} from "vuex"
import LoadingMask from "../util/LoadingMask.vue"
import Chart from "./charts/Chart.vue"

export default {
  components: {
    Chart,
    LoadingMask
  },
  data() {
    return {
      test: undefined,
      results: [],
      chartsData: [],
      maskModel: false
    }
  },
  methods: {
    ...mapActions("app", ["showMessage"]),
    prepareData() {
      for (let i = 0; i < this.test.questions.length; i++) {
        let vue = this
        let data = {
          id: vue.test.questions[i].id,
          value: vue.test.questions[i].question,
          type: vue.test.questions[i].type
        }

        if (data.type !== "TEXT") {
          data.variants = vue.test.questions[i].variants
          for (let j = 0; j < data.variants.length; j++) {
            data.variants[j].results = 0
          }
        } else {
          data.results = []
        }
        this.chartsData.push(data)
      }

      let chartData = this.chartsData
      for (let i = 0; i < this.results.length; i++) {
        let result = this.results[i]

        for (let j = 0; j < chartData.length; j++) {
          let data = chartData[j]
          if (data.type === "TEXT")
            data.results.push(result.answers[j].answer)
          else {
            for (let k = 0; k < data.variants.length; k++) {
              for (let l = 0; l < result.answers.length; l++) {
                let answer = result.answers[l]
                for (let m = 0; m < answer.answers.length; m++) {
                  if (answer.answers[m].id === data.variants[k].id) {
                    data.variants[k].results++
                    break
                  }
                }
              }
            }
          }
        }
      }
    }
  },
  created() {
    let vue = this
    vue.maskModel = true
    axios.get(`/api/results/${this.$route.params.key}`)
        .then(
            response => {
              vue.test = response.data.data.test
              vue.results = response.data.data.results
              vue.prepareData()
            })
        .catch(
            error => {
              if (error.response.data.message)
                vue.showMessage(error.response.data.message)
              else
                vue.showMessage("Некорректная почта и/или пароль")
            })
        .finally(() => {
          vue.maskModel = false
        })
  }
}
</script>

<style scoped>

</style>