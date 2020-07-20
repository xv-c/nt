<template>
    <v-container>
        <span>Это MainPage, {{users}}</span>
    </v-container>
</template>

<script>
    import axios from 'axios'

    export default {
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