<template>
    <v-container>
        <svg :width="chartWidth" :height="chartHeight" @click="handleClick">
            <g
                    v-for="(value, i) in animatedChartData"
                    :transform="`translate(0, ${i * (barHeight + 10)})`">
                <rect :height="barHeight" :width="barWidth(value)"
                      style="fill:rgb(237,41,57);stroke-width:1;stroke:rgb(0,0,0)"/>
                <text :y="barHeight / 2" :x="barWidth(value) + 10">{{ chartData[i] }}</text>
            </g>
        </svg>
    </v-container>
</template>

<script>
    export default {
        data() {
            return {
                dataMax: 20,
                chartWidth: 500,
                chartHeight: 300,
                chartData: [2.5, 4, 10, 1.5, 8.5, 7],
                animatedChartData: [0, 0, 0, 0, 0, 0],
            }
        },
        mounted() {
            this.animate();
        },
        created() {
            this.animate()
        },
        methods: {
            randomData() {
                return new Array(6).fill('').map(() => 1 + Math.floor(Math.random() * 20));
            },
            barWidth(value) {
                return (this.chartWidth - 30) / Math.max(...this.chartData) * value;
            },
            handleClick() {
                this.chartData = this.randomData();
            },
            animate() {

                const duration = 600;
                const start = Date.now();

                const frame = () => {
                    const elapsed = Date.now() - start;
                    const percent = elapsed / duration;

                    if (percent >= 1) {
                        this.animatedChartData = this.chartData;
                    } else {

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
        filters: {
            round(number) {
                return Math.round(number);
            },
        },
    }
</script>

<style scoped>
</style>