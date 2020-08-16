<template>
    <svg :width="chartWidth" :height="chartHeight">
        <g
                v-for="(value, i) in animatedChartData"
                :transform="`translate(0, ${i * (barHeight + 10)})`">
            <rect :height="barHeight" :width="barWidth(value.value, animatedChartData)"
                  :style="'fill:'+value.color+';stroke-width:1;stroke:rgb(0,0,0)'"/>
            <text font-weight="bold" :y="barHeight / 2" :x="barWidth(value.value, animatedChartData) + 10">{{
                chartData[i] }}
                ({{ getPercent(chartData[i], chartData) }})
            </text>
        </g>
    </svg>
</template>

<script>
    export default {
        props: ['chartData'],
        data() {
            return {
                chartWidth: 700,
                chartHeight: 300,
                animatedChartData: [],
            }
        },
        created() {
            for (let i = 0; i < this.chartData.length; i++) {
                let vue = this
                this.animatedChartData.push({
                    value: 0,
                    color: vue.chartData[i].color
                })
            }
            this.animate()
        },
        methods: {
            getPercent(value, data) {
                let sum = 0
                for (let i = 0; i < data.length; i++) {
                    sum += data[i].value
                }
                return Math.round(value / sum * 10000) / 100 + '%'
            },
            barWidth(value, data) {
                let maxVal = -1;
                for (let i = 0; i < data.length; i++) {
                    maxVal = data[i].value > maxVal ? data[i].value : maxVal
                }
                if (maxVal === 0)
                    return 0;
                return (this.chartWidth - 100) / maxVal * value;
            },
            animate() {
                const fromValues = this.animatedChartData;
                const toValues = this.chartData;

                const duration = 1500;
                const start = Date.now();

                const frame = () => {
                    const elapsed = Date.now() - start;
                    const percent = elapsed / duration;

                    if (percent >= 1) {
                        for (let i = 0; i < this.chartData.length; i++) {
                            this.animatedChartData[i].value = this.chartData[i];
                        }
                    } else {
                        for (let i = 0; i < toValues.length; i++) {
                            const fromVal = fromValues[i].value
                            this.animatedChartData[i].value = fromVal + (toValues[i] - fromVal) * percent
                        }
                        requestAnimationFrame(frame);
                    }
                };

                requestAnimationFrame(frame);
            },
        },
        watch: {
            chartData() {
                this.animate();
            },
        },
        computed: {
            barHeight() {
                return this.chartHeight / this.chartData.length - 10;
            }
        },
    }
</script>

<style scoped>
</style>