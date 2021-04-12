<template>
  <v-container>
    <p v-if="!profile" class="font-weight-black text-center" style="margin-top: 10%">
      <b>Пожалуйста, <a @click="openAuthForm">авторизуйтесь</a>,
        чтобы начать работу с конструктором тестов!
      </b>
    </p>

    <v-row v-if="profile" justify="center">
      <v-sheet :rounded="!isTile" :tile="isTile" :flat="isTile" :width="cardWidth" color="#ADD8E6">
        <v-card class="ma-4">
          <v-card-subtitle>
            <v-text-field
                v-model="name"
                placeholder="Название"
                counter="50"
                outlined dense
                hint="Поле не может быть пустым"
                :persistent-hint="name.length===0"
                :color="name.length === 0 ? 'red' : 'blue'"
            >
              <template slot="prepend-inner">
                <v-icon size="x-small" :color="name.length === 0 ? 'red' : 'blue'">
                  fiber_manual_record
                </v-icon>
              </template>
            </v-text-field>
            <v-textarea
                v-model="description"
                placeholder="Краткое описание"
                counter="200"
                :color="description.length===0? 'red' : 'blue'"
                outlined dense
                hint="Поле не может быть пустым"
                :persistent-hint="description.length===0"
            >
              <template slot="prepend-inner">
                <v-icon size="x-small" :color="description.length === 0 ? 'red' : 'blue'">
                  fiber_manual_record
                </v-icon>
              </template>
            </v-textarea>
            <v-checkbox v-model="loginRequired" class="mt-0" hide-details>
              <template v-slot:label>
                <span style="color:black;">Необходима аутентификация</span>
              </template>
            </v-checkbox>
          </v-card-subtitle>
        </v-card>

        <v-card
            v-for="(question, questionIndex) in questions"
            :key="undefined"
            class="ma-4"
        >
          <v-card-title class="pb-0">
            <span style="color: #5AACC7">Вопрос #{{ questionIndex + 1 }}</span>
            <v-spacer/>
            <v-hover v-slot:default="{ hover }">
              <v-btn @click="removeQuestion(questionIndex)" :disabled="questions.length===1" icon>
                <v-icon :color="hover ? 'red' : undefined">clear</v-icon>
              </v-btn>
            </v-hover>
          </v-card-title>

          <v-card-title class="pt-0">
            <v-text-field
                v-model="question.question"
                placeholder="Вопрос"
                counter="200"
                hint="Поле не может быть пустым"
                :persistent-hint="question.question.length===0"
                outlined dense
            >
              <template slot="prepend-inner">
                <v-icon size="x-small" :color="question.question.length===0 ? 'red' : 'blue'">
                  fiber_manual_record
                </v-icon>
              </template>
            </v-text-field>
          </v-card-title>

          <v-card-subtitle>
            <v-select
                v-model="question.type"
                :items="['Один из списка', 'Несколько из списка', 'Текстовый ответ']"
                outlined dense hide-details
            />
          </v-card-subtitle>

          <v-card-text v-if="question.type!=='Текстовый ответ'">
            <v-card outlined style="border: 1px solid #9E9E9E">
              <v-card-actions
                  v-for="(variant, variantIndex) in question.variants"
                  :key="undefined"
                  class="pt-2 pb-0"
              >
                <v-text-field
                    v-model="variant.text"
                    placeholder="Вариант ответа"
                    outlined dense single-line
                    hide-details
                >
                  <template slot="prepend-inner">
                    <v-icon size="x-small" :color="variant.text.length===0 ? 'red' : 'blue'">
                      fiber_manual_record
                    </v-icon>
                  </template>
                </v-text-field>
                <v-hover v-slot:default="{ hover }">
                  <v-btn @click="removeVariant(questionIndex, variantIndex)"
                         :disabled="question.variants.length===2" icon>
                    <v-icon :color="hover ? 'red' : undefined">clear</v-icon>
                  </v-btn>
                </v-hover>
              </v-card-actions>
              <v-card-actions style="background-color: white">
                <v-btn
                    @click="addVariant(question)"
                    color="blue"
                    outlined>Добавить вариант ответа
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-card-text>
        </v-card>
        <v-card-actions class="px-4 py-4">
          <v-btn @click="postTest()" color="blue" outlined>готово</v-btn>
          <v-spacer/>
          <v-btn @click="addQuestion()" color="blue" outlined>
            <span v-if="!isTile">добавить&nbsp;</span>
            <v-icon v-else>add</v-icon>
            вопрос
          </v-btn>
          <v-spacer/>
          <v-btn @click="clear()" color="blue" outlined>очистить</v-btn>
        </v-card-actions>
      </v-sheet>
    </v-row>
  </v-container>
</template>

<script>
import {mapActions, mapState} from "vuex";
import api from "../../use/api";

export default {
  computed: {
    ...mapState('app', ["profile"]),
    isTile() {
      return this.cardWidth === this.$vuetify.breakpoint.width
    },
    cardWidth() {
      if (this.$vuetify.breakpoint.width < 1000)
        return this.$vuetify.breakpoint.width

      return this.$vuetify.breakpoint.width * 0.40
    }
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

      api.post("/api/tests", formData)
          .then(data => {
            if (data.success)
              this.showMessage(`Тест успешно добавлен! Ключ опроса: ${data.test.key}`)
            else
              this.showMessage(data.message)
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