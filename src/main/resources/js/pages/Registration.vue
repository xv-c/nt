<template>
    <v-container fill-height fluid>
        <snackbar :snack-time="snackbarTime" :snackbar-message="snackbarMessage"/>
        <v-row justify="center" align="center">
            <v-card tile width="400" flat color="#add8e6">
                <v-card-title>Регистрация</v-card-title>
                <v-divider/>
                <v-col>
                    <v-text-field placeholder="Имя пользователя" clearable dense solo single-line counter="20"
                                  v-model="nickname"/>
                    <v-text-field placeholder="Почта" clearable dense solo single-line counter="20"
                                  v-model="username"/>
                    <v-text-field placeholder="Пароль" dense solo single-line counter="20"
                                  :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                                  :type="show ? 'text' : 'password'"
                                  v-model="password"
                                  @click:append="show = !show"/>
                    <v-text-field placeholder="Подтверждение пароля" dense solo single-line counter="20"
                                  :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                                  :type="show ? 'text' : 'password'"
                                  v-model="confirmPassword"
                                  @click:append="show = !show"/>
                </v-col>
                <v-card-actions>
                    <v-spacer/>
                    <v-btn @click="register" outlined color="blue">Зарегистрироваться</v-btn>
                    <v-spacer/>
                </v-card-actions>
                <v-divider/>
                <v-col>
                    <v-expansion-panels style="color: blue">
                        <v-expansion-panel>
                            <v-expansion-panel-header><b>Авторизоваться с помощью сервиса</b></v-expansion-panel-header>
                            <v-expansion-panel-content>
                                <v-btn text>
                                    <v-icon color="blue">fab fa-google</v-icon>&nbsp;&nbsp;Google
                                </v-btn>
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-expansion-panels>
                </v-col>
            </v-card>
        </v-row>
    </v-container>
</template>

<script>
    import axios from 'axios'
    import Snackbar from "../util/Snackbar.vue";

    export default {
        components: {
            Snackbar
        },
        data() {
            return {
                nickname: '',
                username: '',
                password: '',
                confirmPassword: '',
                show: false,
                snackbarMessage: '',
                snackbarTime: 0
            }
        },
        methods: {
            register() {
                const vue = this

                if (this.confirmPassword !== this.password) {
                    vue.snackbarMessage = "Пароли не совпадают"
                    vue.snackbarTime++
                }

                let formData = new FormData();
                formData.append("nickname", this.nickname)
                formData.append("username", this.username)
                formData.append("password", this.password)
                axios.post("/api/users", formData)
                    .then(
                        function (response) {
                            vue.snackbarMessage = "Пользователь успешно зарегистрирован"
                            vue.snackbarTime++
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