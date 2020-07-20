<template>
    <v-container>
        <span>hi</span>
    </v-container>
</template>

<script>
    import axios from 'axios'
    export default {
        created() {
            axios.get("/api/users").then(function (response) {
                console.log(response)
            })
        }
    }
</script>

<style scoped>

</style>