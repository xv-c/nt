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
            <v-card outlined style="width: 100%" class="ma-4">
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
                        <v-btn
                            @click="sendKeyToBuffer(item.key)"
                            style="color: black"
                            plain
                            :ripple="false"
                            icon
                        >
                            {{ item.key }}
                            <v-icon>
                                content_copy
                            </v-icon>
                        </v-btn>
                    </template>

                    <template v-slot:item.actions="{ item }">
                        <v-btn
                            @click="$router.replace({query: {...$route.query, resultTestKey: item.key}})"
                            color="blue" outlined
                        >
                            <span v-if="$vuetify.breakpoint.lgAndUp">ответы</span>
                            <v-icon v-else>info</v-icon>
                        </v-btn>

                        <v-btn color="blue" outlined :to="'/search?testKey='+item.key">
                            <span v-if="$vuetify.breakpoint.lgAndUp">открыть</span>
                            <v-icon v-else>open_with</v-icon>
                        </v-btn>

                        <v-btn @click="deleteTest(item)" color="blue" outlined>
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
import {mapActions, mapState} from "vuex"
import LoadingMask from "../../util/LoadingMask.vue"
import Result from "./result/Result.vue";
import api from "../../../use/api";
import endpoints from "../../../use/endpoints";

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
    deleteTest(item) {
      api.delete(endpoints.tests + item.key)
          .then(data => {
            if (data.success) {
              this.showMessage("Тест успешно удален")
              this.loadData()
            } else {
              this.showMessage(data.message)
            }
          })
    },
    formatDate(date){
      let dateTime = date.split("T")

      let formattedDate = dateTime[0].split('-').reverse().join('.')
      let formattedTime = dateTime[1].substring(0, 8)
      return formattedDate + ' / ' + formattedTime
    },
    async loadData() {
      let vue = this
      let newTests = []

      this.maskModel = true
      api.get(endpoints.tests + 'user')
          .then(data => {
            if (data.success) {
              let tests = data.data.tests
              for (let i = 0; i < tests.length; i++) {
                tests[i].creationDate = this.formatDate(tests[i].creationDate)
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