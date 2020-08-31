<template>
  <v-container>
    <v-dialog v-if="selectedItem" v-model="showDialog" width="300">
      <v-color-picker v-model="selectedItem.color">

      </v-color-picker>
    </v-dialog>
    <v-card :width="850">
      <v-card-title>
        <v-col style="margin-top: -10px" cols="4">
          <v-combobox single-line dense solo v-model="selectedChart"
                      :items="['Кольцевая диаграмма', 'Круговая диаграмма', 'Столбчатая диаграмма']">
          </v-combobox>
        </v-col>
        <v-spacer/>
        <v-col style="margin-top: -31px" cols="2">
          <v-btn text @click="recolor()">
            перекрасить
          </v-btn>
        </v-col>
        <v-col style="margin-top: -31px" cols="2">
          <v-btn text @click="saveImg()">
            скачать
          </v-btn>
        </v-col>

      </v-card-title>

      <v-divider style="margin-top: -40px"/>

      <v-card-text style="margin-top: -10px" id="chart">
        <v-row>
          <svg :width="325"
               :height="chartData.length*30+10">
            <g v-for="(item, index) in chartData">
              <text @mouseover="focusItem(item)"
                    @mouseleave="resetColors()"
                    font-weight="bold"
                    :y="index*30+4.2*5"
                    :x="35"
                    style="fill: black">
                {{ item.text }} ({{ item.value }} {{ getLocalizedText(item.value) }})
              </text>
              <rect @click="showPicker(item)"
                    @mouseover="focusItem(item)"
                    @mouseleave="resetColors()"
                    :height="25" :width="25"
                    :x="5"
                    :y="index*30+5"
                    :style="'transform-origin: center; ' +
                                   'transform: scale('+animatedChartData[index].scale+', '+animatedChartData[index].scale+'); ' +
                                    'fill:'+getRgb(animatedChartData[index].color)+';' +
                                     'stroke-width:1;stroke:rgb(0,0,0)'"/>
            </g>
          </svg>

          <bar-chart v-if="selectedChart==='Столбчатая диаграмма'"
                     @mouseleavechild="handleMouseLeaveChild" @mouseoverchild="handleMouseOverChild"
                     :chart-data="animatedChartData"/>
          <pie-chart v-if="selectedChart==='Кольцевая диаграмма' || selectedChart==='Круговая диаграмма'"
                     @mouseleavechild="handleMouseLeaveChild" @mouseoverchild="handleMouseOverChild"
                     :chart-data="animatedChartData"
                     :is-ring="selectedChart==='Кольцевая диаграмма'"/>
        </v-row>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
import html2canvas from "html2canvas"
import {saveAs} from 'file-saver'
import BarChart from "./BarChart.vue"
import PieChart from "./PieChart.vue"

export default {
  components: {PieChart, BarChart},
  props: ['chartData'],
  data() {
    return {
      selectedItem: undefined,
      showDialog: false,
      animatedChartData: [],
      selectedChart: 'Кольцевая диаграмма'
    }
  },
  watch: {
    showDialog: function (value) {
      if (!value)
        this.resetColors()
    }
  },
  created() {
    for (let i = 0; i < this.chartData.length; i++) {
      let vue = this;
      this.animatedChartData.push({
        value: vue.chartData[i].value,
        color: vue.chartData[i].color,
        scale: 1
      })
    }
  },
  methods: {
    recolor() {
      for (let i = 0; i < this.chartData.length; i++) {
        this.chartData[i].color = {
          r: Math.floor(Math.random() * 255),
          g: Math.floor(Math.random() * 255),
          b: Math.floor(Math.random() * 255),
        }
      }
      this.resetColors()
    },
    handleMouseLeaveChild() {
      this.resetColors()
    },
    handleMouseOverChild(item) {
      this.focusItem(item)
    },
    saveImg() {
      let chart = document.getElementById('chart')

      html2canvas(chart, {scale: 3}).then(function (canvas) {
        canvas.toBlob(function (blob) {
          saveAs(blob, "diagram.png");
        })
      })
    },
    getRgb(item) {
      return 'rgb(' + item.r + ',' + item.g + ',' + item.b + ')'
    },
    resetColors() {
      for (let i = 0; i < this.chartData.length; i++) {
        this.animatedChartData[i].scale = 1
        this.animatedChartData[i].color = this.chartData[i].color
      }
    },
    showPicker(item) {
      this.showDialog = true
      this.selectedItem = item
    },
    focusItem(item) {
      for (let i = 0; i < this.animatedChartData.length; i++) {
        if (this.animatedChartData[i].color === item.color) {
          this.animatedChartData[i].scale = 1.015
        } else {
          this.animatedChartData[i].color = {
            r: 188,
            g: 188,
            b: 188
          }
        }
      }
    },
    getTextPercent(value, data) {
      let sum = 0
      for (let i = 0; i < data.length; i++) {
        sum += data[i].value
      }
      return Math.round(value / sum * 10000) / 100 + '%'
    },
    getLocalizedText(amount) {
      let stringSum = amount.toString()
      let lastNum = stringSum.charAt(stringSum.length - 1)

      if (stringSum.length > 1 && stringSum.charAt(stringSum.length - 2) === '1')
        return 'ответов'
      if (lastNum === '1')
        return 'ответ'
      if (['2', '3', '4'].includes(lastNum))
        return 'ответа'
      if (['0', '5', '6', '7', '8', '9'].includes(lastNum))
        return 'ответов'
    }
  },
}
</script>

<style scoped>
</style>