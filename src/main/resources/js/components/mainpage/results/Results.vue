<template>
  <v-container>
    <loading-mask :mask-model="maskModel"/>

    <p v-if="!profile" class="font-weight-black text-center" style="margin-top: 10%">
      <b>Пожалуйста, <a @click="openAuthForm">авторизуйтесь</a>,
        чтобы просмотреть результаты опросов!
      </b>
    </p>

    <v-row
        v-if="profile && !resultTestKey"
        class="ma-0"
        justify="center"
    >
      <v-card outlined style="width: 100%">
        <v-data-table
            :headers="headers"
            :items="tests"
            :search="search"
            :loading="maskModel"
            no-data-text="У вас еще нет созданных опросов"
            no-results-text="Не удалось найти подходящие записи"
            loading-text="Загрузка данных.."
        >
          <template v-slot:top>
            <v-card-title>
              <v-text-field
                  v-model="search"
                  placeholder="Поиск по таблице"
                  outlined dense single-line
                  hide-details
              />
              <v-spacer/>
              <v-btn @click="loadData()" color="blue" outlined>
                <span v-if="$vuetify.breakpoint.lgAndUp">обновить таблицу</span>
                <v-icon v-else>refresh</v-icon>
              </v-btn>
            </v-card-title>
          </template>

          <template v-slot:item.key="{ item }">
            <span @click="sendKeyToBuffer(item.key)">{{ item.key }}</span>
          </template>

          <template v-slot:item.actions="{ item }">
            <v-btn
                @click="$router.replace({query: {...$route.query, resultTestKey: item.key}})"
                color="blue" outlined
            >
              <span v-if="$vuetify.breakpoint.lgAndUp">ответы</span>
              <v-icon v-else>info</v-icon>
            </v-btn>

            <v-btn color="blue" outlined @click="sendKeyToBuffer(item.key)">
              <span v-if="$vuetify.breakpoint.lgAndUp">скопировать ключ</span>
              <v-icon v-else>content_copy</v-icon>
            </v-btn>

            <v-btn color="blue" outlined>
              <span v-if="$vuetify.breakpoint.lgAndUp">удалить</span>
              <v-icon v-else>delete</v-icon>
            </v-btn>

          </template>
        </v-data-table>
      </v-card>
    </v-row>

    <result v-if="resultTestKey"/>

  </v-container>
</template>

<script>
import axios from 'axios'
import {mapActions, mapState} from "vuex"
import LoadingMask from "../../util/LoadingMask.vue"
import Result from "./Result.vue";
import api from "../../../use/api";

export default {
  components: {Result, LoadingMask},
  computed: {
    ...mapState("app", ["profile"]),
    resultTestKey() {
      return this.$route.query.resultTestKey
    }
  },
  data() {
    return {
      maskModel: false,
      headers: [
        {text: 'Дата создания', value: 'creationDate', width: "10%"},
        {text: 'Название опроса', value: 'name', width: "20%"},
        {text: 'Ключ опроса', value: 'key', align: 'center', width: "25%"},
        {text: 'Действия', value: 'actions', sortable: false, align: 'right', width: "45%"}
      ],
      tests: [],
      search: ''
    }
  },
  methods: {
    ...mapActions("app", ["showMessage", "openAuthForm"]),
    async loadData() {
      let vue = this
      let newTests = []

      this.maskModel = true
      setTimeout(() => {
        api.get("/api/tests")
            .then(data => {
              if (data.success) {
                let tests = data.data.tests
                console.log("tests")
                console.log(data)
                for (let i = 0; i < tests.length; i++) {
                  let splitted = tests[i].creationDate.split('-')
                  let validDay = splitted[2]
                  if (validDay.toString().length === 1)
                    validDay = `0${validDay}`
                  let validMonth = splitted[1]
                  if (validMonth.toString().length === 1)
                    validMonth = `0${validMonth}`
                  tests[i].creationDate = `${validDay}.${validMonth}.${splitted[0]}`
                  newTests.push(tests[i])
                }
              } else {
                vue.showMessage(data.message)
              }
            })
            .finally(() => {
              vue.tests = newTests
              vue.maskModel = false
            })
      }, 1000)
    },
    sendKeyToBuffer(key) {
      let el = document.createElement('textarea')
      el.value = key
      el.setAttribute('readonly', '')
      el.style = {position: 'absolute', left: '-9999px'}
      document.body.appendChild(el)
      el.select()
      document.execCommand('copy')
      document.body.removeChild(el)
      this.showMessage("Ключ опроса cкопирован!")
    }
  },
  created() {
    if (!this.profile)
      return
    this.loadData()
  }
}
</script>

<style scoped>

</style>