<template>
    <v-app>

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
    },
    data() {
        return {
            tabValue: undefined,
        }
    },
    methods: {
        ...mapActions('app', ['openAuthForm']),
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
    background: #888;
}

::-webkit-scrollbar-thumb:hover {
    background: #add8e6;
}
</style>