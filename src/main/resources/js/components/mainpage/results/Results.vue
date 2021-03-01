<template>
  <v-container fluid>
    <loading-mask :mask-model="maskModel"/>
    <v-row justify="center">
      <span v-if="!profile" style="font-size: large; margin-top: 5%"><b>Пожалуйста, <a @click="openAuthForm">авторизуйтесь</a>, чтобы просмотреть результаты опросов!</b></span>
    </v-row>
    <v-row v-if="profile && !resultTestKey" justify="center">
      <v-data-table
          :headers="headers"
          :items="tests"
          :search="search"
          hide-default-footer
          no-data-text="У вас еще нет созданных опросов"
          no-results-text="Не удалось найти подходящие записи"
          loading-text="Загрузка данных.."
          :loading="maskModel"
      >
        <template v-slot:top>
          <v-row style="margin: 0 1.5%">
            <v-text-field v-model="search" placeholder="Поиск по таблице" dense single-line
                          style="margin-right: 10px"/>
            <v-btn @click="loadData()" color="blue" outlined>Обновить</v-btn>
          </v-row>
        </template>
        <template v-slot:item.key="{ item }">
          <span @click="sendKeyToBuffer(item.key)">{{ item.key }}</span>
        </template>
        <template v-slot:item.actions="{ item }">
          <v-btn color="blue" outlined @click="$router.replace({query: {...$route.query, resultTestKey: item.key}})">ответы</v-btn>
          <v-btn color="blue" outlined @click="sendKeyToBuffer(item.key)">скопировать ключ</v-btn>
          <v-btn color="blue" outlined>удалить</v-btn>
        </template>
      </v-data-table>
    </v-row>
    <result v-if="resultTestKey"/>
  </v-container>
</template>

<script>
import axios from 'axios'
import {mapActions, mapState} from "vuex"
import LoadingMask from "../../util/LoadingMask.vue"
import Result from "./Result.vue";

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
        {text: 'Дата создания', value: 'creationDate'},
        {text: 'Название опроса', value: 'name'},
        {text: 'Ключ опроса', value: 'key', align: 'center'},
        {text: 'Действия', value: 'actions', sortable: false, align: 'center'}
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
        axios.get("/api/tests")
            .then(
                response => {
                  let tests = response.data.data.tests
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
                })
            .catch(
                error => {
                  if (error.response.data.message)
                    vue.showMessage(error.response.data.message)
                  else
                    vue.showMessage("Не удалось выполнить запрос")
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