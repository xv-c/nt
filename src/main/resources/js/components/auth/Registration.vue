<template>
    <div>
        <v-card-text>
            <v-text-field
                v-model="nickname"
                placeholder="Имя пользователя"
                clearable
                dense solo single-line
                counter="20"
                :persistent-hint="nickname.length<5"
                hint="Поле должно содержать минимум 5 символов"
            >
                <template slot="prepend-inner">
                    <v-icon :color="nickname.length<5?'red':'blue'">person</v-icon>
                </template>
            </v-text-field>
            <v-text-field
                v-model="username"
                placeholder="Электронная почта"
                clearable
                dense solo single-line
            >
                <template slot="prepend-inner">
                    <v-icon :color="validMail?'blue':'red'">mail</v-icon>
                </template>
            </v-text-field>
            <v-text-field
                v-model="password"
                placeholder="Пароль"
                dense solo single-line
                counter="64"
                :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
                :type="show ? 'text' : 'password'"

                :persistent-hint="password.length<8"
                hint="Поле должно содержать минимум 8 символов"
                @click:append="show = !show"
            >
                <template slot="prepend-inner">
                    <v-icon :color="password.length<8?'red':'blue'">mail</v-icon>
                </template>
            </v-text-field>
            <v-text-field
                v-model="confirmPassword"
                placeholder="Подтверждение пароля"
                dense solo single-line
                counter="64"

                :hint="match"
                :persistent-hint="match!==''"
                :append-icon="showConf ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showConf ? 'text' : 'password'"
                @click:append="showConf = !showConf"
            >
                <template slot="prepend-inner">
                    <v-icon :color="match===''?'blue':'red'">lock</v-icon>
                </template>
            </v-text-field>
        </v-card-text>

        <v-divider/>

        <v-card-actions>
            <v-spacer/>
            <v-btn @click="register" outlined color="blue"
                   :disabled="notValid">
                Зарегистрироваться
            </v-btn>
            <v-spacer/>
        </v-card-actions>
    </div>
</template>

<script>
import api from "../../use/api";
import {mapActions} from "vuex"
import endpoints from "../../use/endpoints";

export default {
    components: {},
    data() {
        return {
            nickname: '',
            username: '',
            password: '',
            confirmPassword: '',
            show: false,
            showConf: false,
        }
    },
    computed: {
        notValid() {
            return this.confirmPassword.length === 0 || this.match !== ''
                || this.password.length < 8 || this.password.length > 64
                || this.username.length === 0 || !this.validMail
                || this.nickname.length < 5 || this.nickname.length > 20
        },
        validMail() {
            const regex = /^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$/gm
            return regex.test(this.username)
        },
        match() {
            return this.confirmPassword.length > 0 && this.confirmPassword === this.password ? "" : "Пароли не совпадают"
        }
    },
    methods: {
        ...mapActions('app', ["showMessage"]),
        register() {
            const vue = this

            let data = new FormData();
            data.append("nickname", this.nickname)
            data.append("username", this.username)
            data.append("password", this.password)

            api.post(endpoints.users, data)
                .then(data => {
                    if (data.success)
                        vue.showMessage("Пользователь успешно зарегистрирован")
                    else
                        vue.showMessage(data.message)
                })
        }
    }
}
</script>

<style scoped>

</style>