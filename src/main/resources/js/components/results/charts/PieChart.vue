<template>
  <svg :width="chartRadius*2+8"
       :height="chartRadius*2+8">
    <g v-if="chartData.length>1" v-for="(item, index) in animatedChartData">
      <path @mouseover="$emit('mouseoverchild', chartData[index])"
            @mouseleave="$emit('mouseleavechild')"
            :d="'M '+item.start[0] + ' ' + item.start[1] +
                    ' A '+chartRadius+' '+chartRadius+' 0 ' + item.largeArcFlag + ' 1 ' + item.end[0] + ' ' + item.end[1] +
                    ' L '+(chartRadius+4)+' '+(chartRadius+4)"
            :fill="getRgb(chartData[index].color)"
            :style="'transform-origin: center; transform: scale('+chartData[index].scale+', '+chartData[index].scale+')'"
            stroke="grey" stroke-width="2"/>

    </g>
    <circle v-if="totalResults===1" :cx="chartRadius+4" :cy="chartRadius+4" :r="chartRadius"
            style="transform-origin: center"
            :fill="getRgb(getNotEmptyColor())"
    />
    <circle v-if="isRing" :cx="chartRadius+4" :cy="chartRadius+4" :r="chartRadius/2"
            stroke-width="2"
            stroke="grey"
            style="transform-origin: center"
            fill="white"
    />
    <text v-if="isRing" font-size="70px" text-anchor="middle" font-weight="bold"
          style="fill: black"
          :y="chartRadius+19"
          :x="chartRadius+4">{{ totalResults }}
    </text>
    <text v-if="isRing" font-size="18px" text-anchor="middle" font-weight="bold"
          style="fill: black"
          :y="chartRadius+44"
          :x="chartRadius+4">{{ getLocalizedText(totalResults) }}
    </text>
  </svg>
</template>

<script>
export default {
  props: ['chartData', 'isRing', 'totalResults'],
  data() {
    return {
      chartRadius: 250,
      animatedChartData: []
    }
  },
  created() {
    let accumulatingPercent = 0
    for (let i = 0; i < this.chartData.length; i++) {
      let vue = this;
      let percent = this.getPercent(this.chartData[i], this.chartData);
      this.animatedChartData.push({
        value: vue.chartData[i].value,
        largeArcFlag: percent > 0.5 ? 1 : 0,
        start: vue.getCoordinatesForPercent(accumulatingPercent),
        end: vue.getCoordinatesForPercent(accumulatingPercent + percent)
      })
      accumulatingPercent += percent
    }
    console.log(this.animatedChartData)
  },
  methods: {
    getNotEmptyColor() {
      for (let i = 0; i < this.chartData.length; i++)
        if (this.chartData[i].value !== 0)
          return this.chartData[i].color
    },
    getRgb(item) {
      return 'rgb(' + item.r + ',' + item.g + ',' + item.b + ')'
    },
    getCoordinatesForPercent(percent) {
      const x = Math.cos(2 * Math.PI * percent - Math.PI / 2)
      const y = Math.sin(2 * Math.PI * percent - Math.PI / 2)
      return [
        x * this.chartRadius + (this.chartRadius + 4),
        y * this.chartRadius + (this.chartRadius + 4)
      ]
    },
    getPercent(item, data) {
      let sum = this.getDataSum(data)
      return item.value / sum
    },
    getDataSum(data) {
      let sum = 0
      for (let i = 0; i < data.length; i++)
        sum += data[i].value
      return sum
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
  }
}
</script>

<style scoped>

</style>