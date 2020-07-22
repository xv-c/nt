<template>
    <div>
        <snackbar :snack-time="snackbarTime" :snackbar-message="snackbarMessage"/>
        <v-card-text>
            <v-col>
                <v-text-field placeholder="Имя пользователя" clearable dense solo single-line counter="20"
                              prepend-inner-icon="person"
                              :color="nickname.length<5?'red':undefined"
                              :persistent-hint="nickname.length<5"
                              hint="Поле должно содержать минимум 5 символов"
                              v-model="nickname"/>
                <v-text-field placeholder="Электронная почта" clearable dense solo single-line
                              prepend-inner-icon="mail"
                              :persistent-hint="username.length===0"
                              v-model="username"/>
                <v-text-field placeholder="Пароль" dense solo single-line counter="64"
                              prepend-inner-icon="lock"
                              :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                              :type="show ? 'text' : 'password'"
                              v-model="password"
                              :color="password.length<8?'red':undefined"
                              :persistent-hint="password.length<8"
                              hint="Поле должно содержать минимум 8 символов"
                              @click:append="show = !show"/>
                <v-text-field placeholder="Подтверждение пароля" dense solo single-line counter="64"
                              prepend-inner-icon="lock"
                              :color="match===''?undefined:'red'"
                              :hint="match"
                              :persistent-hint="confirmPassword.length!==0 && match!==''"
                              :append-icon="showConf ? 'mdi-eye' : 'mdi-eye-off'"
                              :type="showConf ? 'text' : 'password'"
                              v-model="confirmPassword"
                              @click:append="showConf = !showConf"/>
            </v-col>
        </v-card-text>
        <v-card-actions style="margin-top: -25px">
            <v-spacer/>
            <v-col>
                <v-btn @click="register" outlined color="blue"
                       :disabled="confirmPassword.length!==0 && match!=='' ||
                       password.length<8 || password.length>64 || username.length===0 || nickname.length<5 || nickname.length>20">Зарегистрироваться</v-btn>
            </v-col>
            <v-spacer/>
        </v-card-actions>
    </div>
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
                showConf: false,
                snackbarMessage: '',
                snackbarTime: 0
            }
        },
        computed: {
            match() {
                return this.confirmPassword === this.password ? "" : "Пароли не совпадают"
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