<template>
  <v-col>
    <v-row v-if="test">
      <v-btn :ripple="false" @click="returnToSearch()" text>
        <v-icon>keyboard_arrow_left</v-icon>
        Вернутся к поиску
      </v-btn>
    </v-row>

    <v-row justify="center" v-if="test">
      <v-sheet :rounded="!isTile" :tile="isTile" :flat="isTile" :width="cardWidth" color="#ADD8E6">

        <v-card class="ma-4">
          <v-card-title>
            {{ test.name }}
            <v-spacer/>
            <span v-if="test.loginRequired" style="color: red; font-size: 12px">
                                Это не анонимный опрос!
                        </span>
          </v-card-title>
          <v-card-subtitle>
            {{ test.description }}
          </v-card-subtitle>
        </v-card>

        <v-card class="ma-4">
          <template
              v-for="(question, questionIndex) in test.questions"

          >
            <v-card-title class="pb-0">
              <span>{{ question.question }}</span>
              <v-icon
                  v-if="question.type==='TEXT'"
                  :color="answerForm[questionIndex].value.length === 0 ? 'red' : 'blue'"
                  size="x-small"
              >
                fiber_manual_record
              </v-icon>
              <v-icon
                  v-if="question.type==='MULTI'"
                  :color="validCheckbox(answerForm[questionIndex]) ? 'blue' : 'red'"
                  size="x-small"
              >
                fiber_manual_record
              </v-icon>
              <v-spacer/>
              <span style="color: #5AACC7">№{{ questionIndex + 1 }}</span>
            </v-card-title>

            <v-card-text
                v-if="question.type==='MULTI'"
                v-for="(variant, variantIndex) in question.variants" :key="undefined"
                class="py-0"
            >
              <v-checkbox
                  v-model="answerForm[questionIndex].variants[variantIndex].value"
                  class="mt-0"
                  hide-details
              >
                <template v-slot:label>
                  <span style="color:black;">{{ variant.value }}</span>
                </template>
              </v-checkbox>
            </v-card-text>

            <v-card-text
                v-if="question.type==='ONE'"
                class="py-0"
            >
              <v-radio-group
                  v-model="answerForm[questionIndex].value"
                  class="mt-0"
                  hide-details
              >
                <v-radio
                    v-for="(variant, variantIndex) in question.variants"
                    :value="variantIndex"
                    :key="variantIndex"
                >
                  <template v-slot:label>
                    <span style="color:black;">{{ variant.value }}</span>
                  </template>
                </v-radio>
              </v-radio-group>
            </v-card-text>

            <v-card-text
                v-if="question.type==='TEXT'"
                class="py-0"
            >
              <v-textarea
                  v-if="question.type==='TEXT'"
                  outlined placeholder="Впишите ваш ответ"
                  counter="200"
                  v-model="answerForm[questionIndex].value"
                  hint="Поле не может быть пустым"
                  :persistent-hint="answerForm[questionIndex].value.length===0"
                  :color="answerForm[questionIndex].value.length === 0 ? 'red' : 'blue'"
              />
            </v-card-text>
          </template>
          <v-card-actions>
            <v-spacer/>
            <v-btn :disabled="!valid" @click="submit()" color="blue" outlined>отправить</v-btn>
            <v-spacer/>
          </v-card-actions>
        </v-card>
      </v-sheet>
    </v-row>
  </v-col>
</template>

<script>
import {mapActions} from "vuex";
import api from "../../../use/api";
import endpoints from "../../../use/endpoints";

export default {
  data() {
    return {
      radioGroup: 1,
      test: undefined,
      answerForm: [],
    }
  },
  computed: {
    cardWidth() {
      if (this.$vuetify.breakpoint.width < 1000)
        return this.$vuetify.breakpoint.width

      return this.$vuetify.breakpoint.width * 0.40
    },
    isTile() {
      return this.cardWidth === this.$vuetify.breakpoint.width
    },
    valid() {
      for (let i = 0; i < this.answerForm.length; i++) {
        let answer = this.answerForm[i]

        if (this.test.questions[i].type === 'TEXT') {
          if (answer.value.length === 0)
            return false
        } else if (this.test.questions[i].type === 'MULTI') {
          if (!this.validCheckbox(answer))
            return false
        }
      }
      return true
    },
  },
  methods: {
    ...mapActions("app", ["showMessage"]),
    returnToSearch() {
      let query = Object.assign({}, this.$route.query)
      delete query.testKey
      this.$router.replace({query})
    },
    validCheckbox(question) {
      for (let j = 0; j < question.variants.length; j++)
        if (question.variants[j].value)
          return true
      return false
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

      api.post(`${endpoints.results}${this.$route.query.testKey}`, body)
          .then(data => {
            if (data.success) {
              this.showMessage("Ваш ответ успешно записан")
              this.returnToSearch()
            } else
              this.showMessage(data.message)
          })

    }
  },
  created() {
    api.get(`${endpoints.tests}${this.$route.query.testKey}`)
        .then(data => {
          if (data.success) {
            let test = data.data.test
            this.test = test

            for (let i = 0; i < test.questions.length; i++) {
              let question = test.questions[i]
              if (question.type === 'TEXT') {
                this.answerForm.push({value: ''})
              } else {
                let variants = []
                for (let j = 0; j < question.variants.length; j++) {
                  variants.push({value: false})
                }
                this.answerForm.push({variants: variants})
                if (question.type === 'ONE')
                  this.answerForm[this.answerForm.length - 1].value = 0
              }
            }
          } else {
            this.showMessage(data.message)
            this.returnToSearch()
          }
        })
  }
}
</script>

<style scoped>

</style>