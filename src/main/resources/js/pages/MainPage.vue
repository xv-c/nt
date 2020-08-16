<template>
    <v-tabs centered grow>
        <v-tab>
            <v-icon>add</v-icon>&nbsp;Конструктор
        </v-tab>
        <v-tab>
            <v-icon>search</v-icon>&nbsp;Найти тест
        </v-tab>
        <v-tab>
            <v-icon>far fa-chart-bar</v-icon>&nbsp;&nbsp;Результаты тестов
        </v-tab>

        <v-tab-item :transition="false" :reverse-transition="false">
            <pie-chart :is-ring="false" :chart-data="chartData"/>
        </v-tab-item>

        <v-tab-item :transition="false" :reverse-transition="false">
        </v-tab-item>

        <v-tab-item :transition="false" :reverse-transition="false">
        </v-tab-item>
    </v-tabs>
</template>

<script>
    import BarChart from "../components/charts/BarChart.vue"
    import PieChart from "../components/charts/PieChart.vue"

    export default {
        components: {PieChart, BarChart},
        data() {
            return {
                chartData: [
                    {value: 2, color: undefined, text: 'Да'},
                    {value: 4, color: undefined, text: 'Нет'},
                    {value: 10, color: undefined, text: 'Пизда'},
                    {value: 8, color: undefined, text: 'За'},
                ],
            }
        },
        methods: {
            getRandomColor() {
                return 'rgb(' +
                    Math.random() * 255 + ',' +
                    Math.random() * 255 + ',' +
                    Math.random() * 255 + ')'
            },
            fillColors() {
                this.chartData.sort((a, b) => a.value > b.value ? -1 : 1)
                let sum = 0
                for (let i = 0; i < this.chartData.length; i++)
                    sum += this.chartData[i].value
                let accumSum = 0
                for (let i = 0; i < this.chartData.length; i++) {
                    let colorPercent = (accumSum + (this.chartData[i].value / 2)) / sum
                    let color = Math.floor(colorPercent * 255)
                    this.chartData[i].color =
                        'rgb(0,' +
                        color * 0.80 + ',' +
                        color + ')'
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