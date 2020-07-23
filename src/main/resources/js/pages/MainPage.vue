<template>
    <v-container>
        <span>Это MainPage, {{users}}</span>
        <br/>
        <bar-chart/>
    </v-container>
</template>

<script>
    import axios from 'axios'
    import BarChart from "../components/charts/BarChartHorizontal.vue"

    export default {
        components: {BarChart},
        data() {
            return {
                users: undefined,
            }
        },
        created() {
            const vue = this
            axios.get("/api/users").then(function (response) {
                vue.users = JSON.stringify(response.data.data.users)
            })
        }
    }
</script>

<style scoped>

</style>