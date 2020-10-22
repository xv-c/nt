<template>
  <v-row justify="center" v-if="test">
    <v-card width="650" tile>
      <v-card-title>
        {{ test.name }}
        <v-spacer/>
        <span v-if="test.loginRequired"
              style="color: red; font-size: 12px">
          Это не анонимный вопрос!
        </span>
      </v-card-title>

      <v-card-subtitle style="margin-top: 5px; font-weight: bold">
        {{ test.description }}
      </v-card-subtitle>

      <v-card-text>
        <template v-for="(question, questionIndex) in test.questions">

          <v-row style="margin-left: 2%; margin-right: 2%">
            <span style="font-weight: bold; font-size: large">{{ question.question }}</span>
            <v-spacer/>
            <span style="color: #5AACC7">Вопрос #{{ questionIndex + 1 }}</span>
          </v-row>

          <v-row v-if="question.type==='MULTI'"
                 v-for="(variant, variantIndex) in question.variants" :key="undefined"
                 style="margin-left: 4%; margin-right: 4%">
            <v-checkbox v-model="answerForm[questionIndex].variants[variantIndex].value"
                        :style="variantIndex===0?'':'margin-top: -15px;'">
              <template v-slot:label>
                <span style="color:black;">{{ variant.value }}</span>
              </template>
            </v-checkbox>
          </v-row>

          <v-row v-if="question.type==='ONE'"
                 style="margin-left: 4%; margin-right: 4%">
            <v-radio-group v-model="answerForm[questionIndex].value">
              <v-radio
                  v-for="(variant, variantIndex) in question.variants"
                  :value="variantIndex"
                  :key="variantIndex">
                <template v-slot:label>
                  <span style="color:black;">{{ variant.value }}</span>
                </template>
              </v-radio>
            </v-radio-group>
          </v-row>

          <v-textarea v-if="question.type==='TEXT'"
                      outlined placeholder="Впишите ответ сюда"
                      counter="200"
                      v-model="answerForm[questionIndex].value"
                      style="margin-left: 4%; margin-right: 4%; margin-top: 20px"/>
        </template>
      </v-card-text>
      <v-card-actions>
        <v-spacer/>
        <v-btn @click="submit()" color="blue" outlined>отправить</v-btn>
        <v-spacer/>
      </v-card-actions>
    </v-card>
  </v-row>
</template>

<script>
import axios from "axios";
import {mapActions} from "vuex";

export default {
  data() {
    return {
      radioGroup: 1,
      test: undefined,
      answerForm: [],
    }
  },
  methods: {
    ...mapActions("app", ["showMessage"]),
    submit() {
      let data = []
      for (let i = 0; i < this.answerForm.length; i++) {
        let answer = this.answerForm[i]

        if (this.test.questions[i].type === 'MULTI') {
          data.push({value: []})
          for (let j = 0; j < answer.variants.length; j++) {
            if (answer.variants[j].value)
              data[data.length - 1].value.push(j)
          }
          continue
        }
        data.push({value: answer.value})
      }
      let body = new FormData()
      body.append("result", JSON.stringify(data))

      let vue = this
      axios.post(`/api/results/${this.$route.params.key}`, body)
          .then(
              response => {
                vue.showMessage("Ваш ответ успешно записан")
              }
          )
          .catch(
              error => {
                if (!error.response || !error.response.data || !error.response.data.message) {
                  vue.showMessage("Не удалось выполнить запрос")
                  return
                }
                if (error.response.data.message)
                  vue.showMessage(error.response.data.message)
              })
    }
  },
  created() {
    let vue = this
    axios.get('/api/tests/' + this.$route.params.key)
        .then(
            response => {
              let test = response.data.data.test
              vue.test = test

              for (let i = 0; i < test.questions.length; i++) {
                let question = test.questions[i]
                if (question.type === 'TEXT') {
                  vue.answerForm.push({value: ''})
                } else {
                  let variants = []
                  for (let j = 0; j < question.variants.length; j++) {
                    variants.push({value: false})
                  }
                  vue.answerForm.push({variants: variants})
                  if (question.type === 'ONE')
                    vue.answerForm[vue.answerForm.length - 1].value = 0
                }
              }
            })
        .catch(
            error => {
              if (!error.response || !error.response.data || !error.response.data.message) {
                vue.showMessage("Не удалось выполнить запрос")
                return
              }
              if (error.response.data.message)
                vue.showMessage(error.response.data.message)
            })
  }
}
</script>

<style scoped>

</style>