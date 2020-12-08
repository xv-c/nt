<template>
  <v-container>
    <v-row v-if="!profile" justify="center" style="margin-top: 5%">
      <span style="font-size: large">
        <b>Пожалуйста, <a @click="openAuthForm">авторизуйтесь</a>, чтобы начать работу с конструктором опросов!</b>
      </span>
    </v-row>

    <v-row v-if="profile" justify="center">
      <v-card width="650" color="#ADD8E6">
        <v-row justify="center" :key="undefined" style="margin-top: 20px">
          <v-card width="600">
            <v-card-title>
              <v-text-field v-model="name" counter="50" :color="name.length===0? 'red' : 'blue'"
                            outlined dense
                            hint="Поле не может быть пустым"
                            :persistent-hint="name.length===0"
                            placeholder="Название"/>
            </v-card-title>

            <v-card-title style="margin-top: -35px">
              <v-textarea v-model="description" counter="200"
                          outlined dense
                          hint="Поле не может быть пустым"
                          :persistent-hint="description.length===0"
                          :color="description.length===0? 'red' : 'blue'"
                          placeholder="Краткое описание"/>
            </v-card-title>

            <v-card-title style="margin-top: -55px; margin-bottom: -25px">
              <v-checkbox v-model="loginRequired">
                <template v-slot:label>
                  <span style="color:black;">Необходима авторизация</span>
                </template>
              </v-checkbox>
            </v-card-title>
          </v-card>
        </v-row>
        <v-row v-for="(question, questionIndex) in questions" justify="center" :key="undefined"
               style="margin-top: 20px">
          <v-card width="600">
            <v-card-title>
              <span style="color: #5AACC7">Вопрос #{{ questionIndex + 1 }}</span>
              <v-spacer/>
              <v-hover v-slot:default="{ hover }">
                <v-btn @click="removeQuestion(questionIndex)" icon>
                  <v-icon :color="hover ? 'red' : undefined">clear</v-icon>
                </v-btn>
              </v-hover>
            </v-card-title>

            <v-card-title style="margin-top: -30px">
              <v-text-field v-model="question.question" placeholder="Вопрос"
                            hint="Поле не может быть пустым"
                            :persistent-hint="question.question.length===0"
                            outlined dense counter="200"/>
            </v-card-title>

            <v-card-subtitle style="margin-bottom: -30px">
              <v-select v-model="question.type" outlined dense
                          :items="['Один из списка', 'Несколько из списка', 'Текстовый ответ']"/>
            </v-card-subtitle>

            <v-card-text v-if="question.type!=='Текстовый ответ'">
              <v-card outlined style="margin-top: 15px; background-color: #9E9E9E">
                <v-card-text style="background-color: white">
                  <v-row v-for="(variant, variantIndex) in question.variants" :key="undefined">
                    <v-text-field placeholder="Вариант ответа"
                                  v-model="variant.text"
                                  outlined dense single-line
                                  :style="`margin-right: 5px; margin-left: 5px;${variantIndex===0 ? '' :'margin-top: 10px;'} margin-bottom: -25px`">
                      <template v-slot:append-outer>
                        <v-hover v-slot:default="{ hover }">
                          <v-btn @click="removeVariant(questionIndex, variantIndex)"
                                 :disabled="question.variants.length===2"
                                 style="margin-top: -5px;" icon>
                            <v-icon :color="hover ? 'red' : undefined">clear</v-icon>
                          </v-btn>
                        </v-hover>
                      </template>
                    </v-text-field>
                  </v-row>
                  <v-row style="margin-left: -7px">
                    <v-btn @click="addVariant(question)" color="blue"
                           style="margin-top: 15px"
                           outlined>Добавить вариант ответа
                    </v-btn>
                  </v-row>
                </v-card-text>
              </v-card>
            </v-card-text>
          </v-card>
        </v-row>
        <v-row justify="center" style="margin-top: 20px; margin-bottom: 15px">
          <v-btn @click="addQuestion()" color="blue" outlined>Добавить вопрос</v-btn>
        </v-row>
      </v-card>
    </v-row>

    <v-row v-if="profile" justify="center" style="margin-top: 20px">
      <v-col cols="3">
        <v-spacer/>
      </v-col>
      <v-col cols="2">
        <v-row justify="center">
          <v-btn @click="postTest()" color="blue" outlined>готово</v-btn>
        </v-row>
      </v-col>
      <v-col cols="2">
        <v-row justify="center">
          <v-btn @click="clear()" color="blue" outlined>очистить</v-btn>
        </v-row>
      </v-col>
      <v-col cols="3">
        <v-spacer/>
      </v-col>


    </v-row>
  </v-container>
</template>

<script>
import {mapActions, mapState} from "vuex";
import axios from "axios";

export default {
  computed: {
    ...mapState('app', ["profile"])
  },
  data() {
    return {
      name: 'Новая форма',
      description: '',
      questions: [],
      loginRequired: false
    }
  },
  methods: {
    ...mapActions('app', ["openAuthForm", "showMessage"]),
    clear() {
      this.name = 'Новая форма'
      this.description = ''
      this.questions = []
      this.loginRequired = false
      this.addQuestion()
    },
    postTest() {
      if (this.name.length === 0 || this.name.length > 50) {
        this.showMessage("Название формы не может быть пустым и не может превышать длину в 50 символов ")
        return
      }

      if (this.description.length === 0 || this.description.length > 200) {
        this.showMessage("Описание формы не может быть пустым и не может превышать длину в 200 символов ")
        return
      }

      for (let i = 0; i < this.questions.length; i++) {
        let currQuestion = this.questions[i]
        if (currQuestion.question.length === 0 || currQuestion.question.length > 200) {
          this.showMessage("Вопрос #" + (i + 1) + " не может быть пустым и не может превышать длину в 200 символов")
          return
        }
        if (currQuestion.type !== 'Текстовый ответ') {
          for (let j = 0; j < currQuestion.variants.length; j++)
            if (currQuestion.variants[j].text.length === 0 || currQuestion.variants[j].text.length > 50) {
              this.showMessage("Вариант ответа #" + (j + 1) + ", в вопросе #" + (i + 1) + ", не может быть пустым и не может превышать длину в 200 символов")
              return
            }
        }
      }
      let validQuestions = JSON.parse(JSON.stringify(this.questions))
      let map = new Map()
      map.set('Один из списка', 'ONE')
      map.set('Несколько из списка', 'MULTI')
      map.set('Текстовый ответ', 'TEXT')
      for (let i = 0; i < validQuestions.length; i++) {
        if (validQuestions[i].type === 'Текстовый ответ') {
          validQuestions[i].variants = []
        }
        validQuestions[i].type = map.get(validQuestions[i].type)
      }

      let formData = new FormData()
      formData.append('name', this.name)
      formData.append('loginRequired', this.loginRequired)
      formData.append('description', this.description)
      formData.append('questions', JSON.stringify(validQuestions))

      let vue = this
      axios.post("/api/tests", formData)
          .then(
              function (response) {
                vue.showMessage(`Тест успешно добавлен! Ключ опроса: ${response.data.data.test.key}`)
              })
          .catch(
              function (error) {
                if (error.response.data.message)
                  vue.showMessage(error.response.data.message)
                else
                  vue.showMessage("Не удалось выполнить запрос")
              })
    },
    addQuestion() {
      this.questions.push({
        type: 'Один из списка',
        question: '',
        variants: []
      })
      this.addVariant(this.questions[this.questions.length - 1])
      this.addVariant(this.questions[this.questions.length - 1])
    },
    addVariant(item) {
      item.variants.push({
        text: '',
      })
    },
    removeQuestion(index) {
      this.questions.splice(index, 1)
    },
    removeVariant(questionIndex, variantIndex) {
      this.questions[questionIndex].variants.splice(variantIndex, 1)
    },
  },
  created() {
    this.addQuestion()
  }
}
</script>

<style scoped>

</style>