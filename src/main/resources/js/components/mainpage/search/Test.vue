<template>
  <v-col>
    <v-row v-if="test">
      <v-btn :ripple="false" @click="returnToSearch()" text><v-icon>keyboard_arrow_left</v-icon>Вернутся к поиску</v-btn>
    </v-row>

    <v-row justify="center" v-if="test">
      <v-card width="650" color="#ADD8E6">
        <v-row justify="center" style="margin-top: 20px">
          <v-card width="600">
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
          </v-card>
        </v-row>
        <v-row justify="center" style="margin-top: 20px; margin-bottom: 20px">
          <v-card width="600">
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
                            hint="Поле не может быть пустым"
                            :persistent-hint="answerForm[questionIndex].value.length===0"
                            :color="answerForm[questionIndex].value.length === 0 ? 'red' : 'blue'"
                            style="margin-left: 4%; margin-right: 4%; margin-top: 20px"/>
              </template>
            </v-card-text>
            <v-card-actions>
              <v-spacer/>
              <v-btn :disabled="!valid" @click="submit()" color="blue" outlined>отправить</v-btn>
              <v-spacer/>
            </v-card-actions>
          </v-card>
        </v-row>
      </v-card>
    </v-row>
  </v-col>
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
  computed: {
    valid() {
      for (let i = 0; i < this.answerForm.length; i++) {
        if (this.test.questions[i].type === 'TEXT'){
          let answer = this.answerForm[i]
          if (answer.value.length === 0)
            return false
        }
      }
      return true
    },
  },
  methods: {
    ...mapActions("app", ["showMessage"]),
    returnToSearch(){
      let query = Object.assign({}, this.$route.query)
      delete query.testKey
      this.$router.replace({query})
    },
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
      axios.post(`/api/results/${this.$route.query.testKey}`, body)
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
    axios.get(`/api/tests/${this.$route.query.testKey}`)
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
              vue.returnToSearch()
            })
  }
}
</script>

<style scoped>

</style>