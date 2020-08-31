<template>
  <v-row justify="center">
    <span v-if="!profile" style="font-size: large; margin-top: 5%">
      <b>Пожалуйста, <a @click="openAuthForm">авторизуйтесь</a>, чтобы просмотреть результаты тестов!</b>
    </span>
    <v-data-table
        v-if="profile"
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

<script>
import axios from 'axios'
import {mapActions, mapState} from "vuex";

export default {
  computed:{
    ...mapState("app", ["profile"])
  },
  data() {
    return {
      headers: [
        {text: 'Название теста', value: 'name'},
        {text: 'Ключ теста', value: 'key'},
        {text: 'Действия', value: 'actions', sortable: false, align: 'center'}
      ],
      tests: []
    }
  },
  methods: {
    ...mapActions("app", ["showMessage", "openAuthForm"]),
    sendKeyToBuffer(key) {
      let el = document.createElement('textarea')
      el.value = key
      el.setAttribute('readonly', '')
      el.style = {position: 'absolute', left: '-9999px'}
      document.body.appendChild(el)
      el.select()
      document.execCommand('copy')
      document.body.removeChild(el)
      this.showMessage("Ключ теста cкопирован!")
    }
  },
  created() {
    if(!this.profile)
      return
    let vue = this
    axios.get("/api/tests")
        .then(
            function (response) {
              let tests = response.data.data.tests
              for (let i = 0; i < tests.length; i++) {
                vue.tests.push(tests[i])
              }
            })
        .catch(
            function (error) {
              if (error.response.data.message)
                vue.showMessage(error.response.data.message)
              else
                vue.showMessage("Не удалось выполнить запрос")
            })
  }
}
</script>

<style scoped>

</style>