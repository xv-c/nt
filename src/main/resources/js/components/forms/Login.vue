<template>
    <div>
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
    import {mapActions} from "vuex";
    export default {
        components:{
            Snackbar
        },
        data() {
            return {
                username: '',
                password: '',
                show: false,
            }
        },
        methods: {
            ...mapActions('app', ["showMessage"]),
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
                            if(error.response.data.message)
                                vue.showMessage(error.response.data.message)
                            else
                                vue.showMessage("Некорректная почта и/или пароль")
                        })
            }
        }
    }
</script>

<style scoped>

</style>