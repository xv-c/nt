<template>
    <div>
        <svg :width="325"
             :height="chartData.length*30">
            <g v-for="(item, index) in chartData">
                <text @mouseover="focusItem(item)"
                      @mouseleave="resetColors()"
                      font-weight="bold"
                      :y="index*30+4.2*5"
                      :x="35">
                    {{item.text}} ({{ item.value }} {{getLocalizedText(item.value)}})
                </text>
                <rect @mouseover="focusItem(item)"
                      @mouseleave="resetColors()"
                      :height="25" :width="25"
                      :x="5"
                      :y="index*30+5"
                      :style="'fill:'+animatedChartData[index].color+';stroke-width:1;stroke:rgb(0,0,0)'"/>
            </g>
        </svg>
        <svg :width="chartRadius*2+8"
             :height="chartRadius*2+8">
            <g v-if="chartData.length>1" v-for="(item) in animatedChartData">
                <path @mouseover="focusItem(item)"
                      @mouseleave="resetColors()"
                      :d="'M '+item.start[0] + ' ' + item.start[1] +
                    ' A '+(chartRadius+4)+' '+(chartRadius+4)+' 0 ' + item.largeArcFlag + ' 1 ' + item.end[0] + ' ' + item.end[1] +
                    ' L '+(chartRadius+4)+' '+(chartRadius+4)"
                      :fill="item.color"
                      stroke="grey" stroke-width="2"/>
            </g>
            <circle v-if="chartData.length===1" :cx="chartRadius+4" :cy="chartRadius+4" :r="chartRadius"
                    style="transform-origin: center"
                    :fill="chartData[0].color"
            />
            <circle v-if="isRing" :cx="chartRadius+4" :cy="chartRadius+4" :r="chartRadius/2"
                    stroke-width="2"
                    stroke="grey"
                    style="transform-origin: center"
                    fill="white"
            />
            <text v-if="isRing" font-size="70px" text-anchor="middle" font-weight="bold"
                  :y="chartRadius+19"
                  :x="chartRadius+4">{{ getDataSum(chartData) }}
            </text>
            <text v-if="isRing" font-size="18px" text-anchor="middle" font-weight="bold"
                  :y="chartRadius+44"
                  :x="chartRadius+4">{{ getLocalizedText(getDataSum(chartData)) }}
            </text>
        </svg>
    </div>
</template>

<script>
    export default {
        props: ['chartData', 'isRing'],
        data() {
            return {
                chartRadius: 250,
                animatedChartData: [],
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
                    color: vue.chartData[i].color,
                    start: vue.getCoordinatesForPercent(accumulatingPercent),
                    end: vue.getCoordinatesForPercent(accumulatingPercent + percent)
                })
                accumulatingPercent += percent
            }
        },
        methods: {
            resetColors() {
                for (let i = 0; i < this.chartData.length; i++) {
                    this.animatedChartData[i].color = this.chartData[i].color
                }
            },
            focusItem(item) {
                for (let i = 0; i < this.animatedChartData.length; i++) {
                    if (this.animatedChartData[i].color !== item.color)
                        this.animatedChartData[i].color = '#BCBCBC'
                }
            },
            getCoordinatesForPercent(percent) {
                const x = Math.cos(2 * Math.PI * percent - Math.PI / 2)
                const y = Math.sin(2 * Math.PI * percent - Math.PI / 2)
                return [
                    x * this.chartRadius + (this.chartRadius+4),
                    y * this.chartRadius + (this.chartRadius+4)
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
        },
        watch: {},
        computed: {},
    }
</script>

<style scoped>
</style>