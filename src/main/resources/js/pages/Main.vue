<template>
  <v-tabs background-color="#91CAD8" color="black" v-model="tab">
    <v-tab>
      <v-icon>search</v-icon>&nbsp;Найти опрос
    </v-tab>
    <v-tab>
      <v-icon :class="tab===1?'icon':''">add</v-icon>&nbsp;Конструктор
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
  data() {
    return {
      tab: 0,
    }
  }
}
</script>

<style scoped>
.icon {
  -webkit-animation: spin 2s linear infinite;
  -moz-animation: spin 2s linear infinite;
  animation: spin 2s linear infinite;
}

@-moz-keyframes spin {
  100% {
    -moz-transform: rotate(360deg);
  }
}

@-webkit-keyframes spin {
  100% {
    -webkit-transform: rotate(360deg);
  }
}

@keyframes spin {
  100% {
    -webkit-transform: rotate(360deg);
    transform: rotate(360deg);
  }
}

</style>