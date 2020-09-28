<template>
  <svg :width="chartWidth" :height="chartData.length*35">
    <g v-for="(item, i) in chartData">
      <rect :x="5"
            :y="35*i+5"
            @mouseover="$emit('mouseoverchild', item)"
            @mouseleave="$emit('mouseleavechild')"
            :height="30"
            :width="barWidth(item, chartData)"
            :style="'fill:'+getRgb(item.color)+';stroke-width:1;stroke:rgb(0,0,0)'"/>
      <text font-weight="bold"
            style="fill: black"
            :y="35*i+22"
            :x="barWidth(item, chartData) + 10">
        {{ chartData[i].value }}/{{ totalResults }} ({{ getTextPercent(chartData[i].value, chartData) }})
      </text>
    </g>
  </svg>
</template>

<script>
export default {
  props: ['chartData', 'totalResults'],
  data() {
    return {
      chartWidth: 500,
    }
  },
  created() {
  },
  methods: {
    getTextPercent(value, data) {
      let sum = 0
      for (let i = 0; i < data.length; i++) {
        sum += data[i].value
      }
      return Math.round(value / sum * 10000) / 100 + '%'
    },
    getRgb(item) {
      return 'rgb(' + item.r + ',' + item.g + ',' + item.b + ')'
    },
    barWidth(item, data) {
      let maxVal = -1;
      for (let i = 0; i < data.length; i++) {
        maxVal = data[i].value > maxVal ? data[i].value : maxVal
      }
      if (maxVal === 0)
        return 0;
      return (this.chartWidth - 100) / maxVal * item.value;
    },
  },
  computed: {}
}
</script>

<style scoped>
</style>