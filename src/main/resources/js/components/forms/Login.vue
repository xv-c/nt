<template>
    <div>
        <snackbar :snack-time="snackbarTime" :snackbar-message="snackbarMessage"/>
        <v-card-text>
            <v-col>
                <v-text-field placeholder="Электронная почта" clearable dense solo single-line
                              prepend-inner-icon="mail"
                              v-model="username"/>
                <v-text-field placeholder="Пароль" dense solo single-line
                              prepend-inner-icon="lock"
                              :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                              :type="show ? 'text' : 'password'"
                              v-model="password"
                              @click:append="show = !show"/>
            </v-col>
        </v-card-text>

        <v-card-actions style="margin-top: -20px">
            <v-spacer/>
            <v-btn :ripple="false" @click="login" outlined color="blue">Войти</v-btn>
            <v-spacer/>
        </v-card-actions>
    </div>
</template>

<script>
    import axios from "axios";
    import Snackbar from "../util/Snackbar.vue";
    export default {
        components:{
            Snackbar
        },
        data() {
            return {
                username: '',
                password: '',
                show: false,
                snackbarMessage: '',
                snackbarTime: 0
            }
        },
        methods: {
            login() {
                const vue = this

                let formData = new FormData();
                formData.append("username", this.username)
                formData.append("password", this.password)
                axios.post("/api/login", formData)
                    .then(
                        function (response) {
                            vue.$router.push('/')
                            vue.$router.go()
                        })
                    .catch(
                        function (error) {
                            vue.snackbarMessage = error.response.data.message
                            vue.snackbarTime++
                        })
            }
        }
    }
</script>

<style scoped>

</style>