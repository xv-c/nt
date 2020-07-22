<template>
    <v-container>
        <svg xmlns="http://www.w3.org/2000/svg" :height="chartHeight" :width="chartWidth">
            <g v-for="(value, i) in chartData">

            </g>
        </svg>
    </v-container>
</template>

<script>
    export default {
        props: ['data', 'width', 'height'],
        data() {
            return {
                chartData: [],
                chartHeight: 0,
            }
        },
        created() {

            let maximum = Math.max(...this.data)
            let minimum = Math.min(...this.data)

            if(minimum<0)

            for (let i = 0; i < this.data.length; i++) {
                this.chartData.append(this.data)
            }
        }
    }
</script>

<style scoped>

</style>