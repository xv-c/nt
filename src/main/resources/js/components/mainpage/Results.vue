<template>
  <v-container>
    <loading-mask :mask-model="maskModel"/>
    <v-row justify="center">
      <span v-if="!profile" style="font-size: large; margin-top: 5%"><b>Пожалуйста, <a @click="openAuthForm">авторизуйтесь</a>, чтобы просмотреть результаты опросов!</b></span>
    </v-row>
    <template v-if="profile">
      <v-row justify="center">
        <v-btn @click="loadData()" color="blue" outlined>Обновить</v-btn>
      </v-row>
      <v-row justify="center">
        <span v-if="tests.length===0"
              style="font-size: large; margin-top: 5%"><b>У вас еще нет созданных опросов</b></span>
        <v-data-table
            v-if="tests.length>0"
            :headers="headers"
            :items="tests"
            hide-default-footer
        >
          <template v-slot:item.key="{ item }">
            <span @click="sendKeyToBuffer(item.key)">{{ item.key }}</span>
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn color="blue" outlined>ответы</v-btn>
            <v-btn color="blue" outlined>редактировать</v-btn>
            <v-btn color="blue" outlined>удалить</v-btn>
          </template>
        </v-data-table>
      </v-row>
    </template>
  </v-container>
</template>

<script>
import axios from 'axios'
import {mapActions, mapState} from "vuex"
import LoadingMask from "../util/LoadingMask.vue"

export default {
  components: {LoadingMask},
  computed: {
    ...mapState("app", ["profile"])
  },
  data() {
    return {
      maskModel: false,
      headers: [
        {text: 'Название опроса', value: 'name'},
        {text: 'Ключ опроса', value: 'key'},
        {text: 'Действия', value: 'actions', sortable: false, align: 'center'}
      ],
      tests: []
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
                    newTests.push(tests[i])
                  }
                  vue.maskModel = false
                })
            .catch(
                error => {
                  if (error.response.data.message)
                    vue.showMessage(error.response.data.message)
                  else
                    vue.showMessage("Не удалось выполнить запрос")
                  vue.maskModel = false
                })
            .finally(() => {
              vue.tests = newTests
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