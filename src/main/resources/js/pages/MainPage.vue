<template>
  <v-tabs centered grow background-color="#91CAD8" color="black">
    <v-tab>
      <v-icon>search</v-icon>&nbsp;Найти тест
    </v-tab>
    <v-tab>
      <v-icon>add</v-icon>&nbsp;Конструктор
    </v-tab>
    <v-tab>
      <v-icon>far fa-chart-bar</v-icon>&nbsp;&nbsp;Результаты тестов
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
import Chart from "../components/charts/Chart.vue"
import Constructor from "../components/mainpage/Constructor.vue"
import Results from "../components/mainpage/Results.vue"
import SearchPage from "../components/mainpage/SearchPage.vue"

export default {
  components: {SearchPage, Constructor, Chart, Results},
  data() {
    return {
      chartData: [
        {value: 2, color: undefined, text: 'Да'},
        {value: 4, color: undefined, text: 'Нет'},
        {value: 3, color: undefined, text: 'Воздержусь'},
        {value: 3, color: undefined, text: 'Воздержусь'},
        {value: 3, color: undefined, text: 'Воздержусь'},
        {value: 3, color: undefined, text: 'Воздержусь'},
        {value: 3, color: undefined, text: 'Воздержусь'},
        {value: 3, color: undefined, text: 'Воздержусь'},
        {value: 3, color: undefined, text: 'Воздержусь'},
        {value: 3, color: undefined, text: 'Воздержусь'},
      ],
    }
  },
  methods: {
    fillColors() {
      this.chartData.sort((a, b) => a.value > b.value ? -1 : 1)
      let sum = 0
      for (let i = 0; i < this.chartData.length; i++)
        sum += this.chartData[i].value
      let accumSum = 0
      for (let i = 0; i < this.chartData.length; i++) {
        let colorPercent = (accumSum + (this.chartData[i].value / 2)) / sum
        let color = Math.floor(colorPercent * 255)
        this.chartData[i].color = {
          r: 0,
          g: color * 0.80,
          b: color
        }
        accumSum += this.chartData[i].value
      }
    }
  },
  created() {
    this.fillColors()
  }
}
</script>

<style scoped>

</style>