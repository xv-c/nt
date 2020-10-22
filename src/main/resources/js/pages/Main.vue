<template>
  <v-tabs centered grow background-color="#91CAD8" color="black">
    <v-tab>
      <v-icon>search</v-icon>&nbsp;Найти опрос
    </v-tab>
    <v-tab>
      <v-icon>add</v-icon>&nbsp;Конструктор
    </v-tab>
    <v-tab>
      <v-icon>far fa-chart-bar</v-icon>&nbsp;&nbsp;Результаты опросов
    </v-tab>

    <v-tab-item :transition="false" :reverse-transition="false">
      <search-page/>
    </v-tab-item>

    <v-tab-item :transition="false" :reverse-transition="false">
      <constructor/>
    </v-tab-item>

    <v-tab-item :transition="false" :reverse-transition="false">
      <results/>
    </v-tab-item>
  </v-tabs>
</template>

<script>
import Constructor from "../components/mainpage/Constructor.vue"
import Results from "../components/mainpage/Results.vue"
import SearchPage from "../components/mainpage/Search.vue"

export default {
  components: {SearchPage, Constructor, Results},
}
</script>

<style scoped>

</style>