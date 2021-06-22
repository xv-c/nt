<template>
    <v-app>
        <v-dialog max-width="450" v-if="editForm.model" v-model="editForm.model">
            <v-card tile flat color="#add8e6">
                <v-card-title v-if="editForm.isProfile">
                    <v-spacer/>
                    Профиль
                    <v-spacer/>
                    <v-btn @click="editForm.isProfile=false" text :ripple="false">
                        Смена пароля
                        <v-icon>keyboard_arrow_right</v-icon>
                    </v-btn>
                </v-card-title>

                <v-card-title v-if="!editForm.isProfile">
                    <v-btn @click="editForm.isProfile=true" text :ripple="false">
                        <v-icon>keyboard_arrow_left</v-icon>
                        Профиль
                    </v-btn>
                    <v-spacer/>
                    Смена пароля
                    <v-spacer/>
                </v-card-title>

                <v-row class="mx-4 py-4">
                    <v-divider/>
                </v-row>

                <v-card-text class="pt-4">
                    <div v-if="editForm.isProfile">
                        <v-text-field
                            v-model="editForm.profile.nickname"
                            placeholder="Имя пользователя"
                            clearable
                            dense solo single-line
                            counter="20"
                            :persistent-hint="editForm.profile.nickname.length<5"
                            hint="Поле должно содержать минимум 5 символов"
                        >
                            <template slot="prepend-inner">
                                <v-icon :color="editForm.profile.nickname.length<5?'red':'blue'">person</v-icon>
                            </template>
                        </v-text-field>
                        <v-text-field
                            v-model="editForm.profile.username"
                            placeholder="Электронная почта"
                            clearable
                            dense solo single-line
                        >
                            <template slot="prepend-inner">
                                <v-icon :color="validMail?'blue':'red'">mail</v-icon>
                            </template>
                        </v-text-field>
                    </div>
                    <div v-else>
                        <v-text-field
                            v-model="editForm.pass.password"
                            placeholder="Новый пароль"
                            dense solo single-line
                            counter="64"
                            :append-icon="editForm.pass.show ? 'mdi-eye' : 'mdi-eye-off'"
                            :type="editForm.pass.show ? 'text' : 'password'"

                            :persistent-hint="editForm.pass.password.length<8"
                            hint="Поле должно содержать минимум 8 символов"
                            @click:append="editForm.pass.show = !editForm.pass.show"
                        >
                            <template slot="prepend-inner">
                                <v-icon :color="editForm.pass.password.length<8?'red':'blue'">mail</v-icon>
                            </template>
                        </v-text-field>
                        <v-text-field
                            v-model="editForm.pass.confirm"
                            placeholder="Подтверждение нового пароля"
                            dense solo single-line
                            counter="64"

                            :hint="match"
                            :persistent-hint="match!==''"
                            :append-icon="editForm.pass.showConf ? 'mdi-eye' : 'mdi-eye-off'"
                            :type="editForm.pass.showConf ? 'text' : 'password'"
                            @click:append="editForm.pass.showConf = !editForm.pass.showConf"
                        >
                            <template slot="prepend-inner">
                                <v-icon :color="match===''?'blue':'red'">lock</v-icon>
                            </template>
                        </v-text-field>
                    </div>
                </v-card-text>

                <v-row class="mx-4 py-4">
                    <v-divider/>
                </v-row>

                <v-card-actions>
                    <v-spacer/>
                    <v-btn @click="submit" outlined color="blue"
                           :disabled="notValid">
                        сохранить
                    </v-btn>
                    <v-spacer/>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <snackbar/>
        <auth-form/>

        <v-app-bar color="#5AACC7" flat app>
            <v-tabs active-class="selected-tab" hide-slider color="black">
                <v-tab :ripple="false" to="/search">
                    <v-icon>search</v-icon>&nbsp;Найти опрос
                </v-tab>
                <v-tab :ripple="false" to="/constructor">
                    <v-icon>add</v-icon>&nbsp;Конструктор
                </v-tab>
                <v-tab :ripple="false" to="/results">
                    <v-icon>far fa-chart-bar</v-icon>&nbsp;&nbsp;Результаты опросов
                </v-tab>
            </v-tabs>

            <v-spacer/>
            <span v-if="profile"><b>{{ profile.nickname }}</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
            <v-hover v-slot="{hover}">
                <v-btn v-if="profile" @click="openProfileEditForm" color="white" icon>
                    <v-icon> {{hover ? 'edit':'person'}} </v-icon>
                </v-btn>
            </v-hover>
            <v-btn v-if="!profile" @click="openAuthForm" color="#CE7A46" rounded>Авторизация</v-btn>
            <v-btn v-if="profile" @click="logout()" color="#CE7A46" rounded>Выход</v-btn>
        </v-app-bar>

        <v-main>
            <router-view/>
        </v-main>

        <my-footer/>
    </v-app>
</template>

<script>
import {mapActions, mapState} from "vuex";
import AuthForm from "./components/auth/AuthForm.vue";
import Snackbar from "./components/util/Snackbar.vue";
import MyFooter from "./components/mainpage/MyFooter.vue";
import Results from "./components/mainpage/results/Results.vue";
import Constructor from "./components/mainpage/Constructor.vue";
import Search from "./components/mainpage/search/Search.vue";
import api from "./use/api";
import endpoints from "./use/endpoints";

export default {
    components: {Snackbar, AuthForm, MyFooter, Results, Constructor, Search},
    computed: {
        ...mapState('app', ["profile"]),
        notValid() {
            if (this.editForm.isProfile){
                return this.editForm.profile.username.length === 0 || !this.validMail
                    || this.editForm.profile.nickname.length < 5 || this.editForm.profile.nickname.length > 20
            } else {
                return this.editForm.pass.password.length < 8 || this.editForm.pass.password.length > 64
                    || this.match !== ''
            }
        },
        validMail() {
            const regex = /^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$/gm
            return regex.test(this.editForm.profile.username)
        },
        match() {
            return this.editForm.pass.confirm === this.editForm.pass.password ? "" : "Пароли не совпадают"
        },
    },
    data() {
        return {
            editForm: {
                model: false,
                isProfile: true,

                profile: {
                    username: '',
                    nickname: '',
                },
                pass: {
                    password: '',
                    confirm: '',

                    show: false,
                    showConf: false,
                },
            },
            tabValue: undefined,
        }
    },
    methods: {
        ...mapActions('app', ['openAuthForm', "showMessage"]),
        openProfileEditForm(){
            let form = this.editForm.profile

            form.username = this.profile.username
            form.nickname = this.profile.nickname

            this.editForm.model = true
        },

        submit() {
            const vue = this

            let data = new FormData();
            data.append("nickname", this.editForm.profile.nickname)
            data.append("username", this.editForm.profile.username)
            data.append("password", this.editForm.pass.password)

            api.put(endpoints.users + (this.editForm.isProfile ? 'profile' : 'password'), data)
                .then(data => {
                    if (data.success){
                        vue.showMessage("Данные изменены")
                        this.$router.go()
                    }
                    else
                        vue.showMessage(data.message)
                })
        },

        logout() {
            api.post(endpoints.logout)
                .then(res => this.$router.go())
        }
    }
}
</script>

<style>
.selected-tab {
    background-color: #91CAD8
}

.icon {
    -webkit-animation: spin 3s linear infinite;
    -moz-animation: spin 3s linear infinite;
    animation: spin 3s linear infinite;
}

@-moz-keyframes spin {
    100% {
        -moz-transform: rotate(360deg);
    }
}

@-webkit-keyframes spin {
    100% {
        -webkit-transform: rotate(360deg);
    }
}

@keyframes spin {
    100% {
        -webkit-transform: rotate(360deg);
        transform: rotate(360deg);
    }
}

::-webkit-scrollbar {
    width: 5px;
}

::-webkit-scrollbar-track {
    background: #add8e6;
}

::-webkit-scrollbar-thumb {
    background: #A5A5A5;
}

::-webkit-scrollbar-thumb:hover {
    background: #5B5B5B;
}
</style>