<template>
  <v-app>

    <snackbar/>
    <auth-form/>

    <v-app-bar color="#5AACC7" flat app>
      <v-tabs active-class="selected-tab" hide-slider color="black" v-model="tab">
        <v-tab :ripple="false" href="#search">
          <v-icon>search</v-icon>&nbsp;Найти опрос
        </v-tab>
        <v-tab :ripple="false" href="#constructor">
          <v-icon :class="tab==='constructor'?'icon':''">add</v-icon>&nbsp;Конструктор
        </v-tab>
        <v-tab :ripple="false" href="#results">
          <v-icon>far fa-chart-bar</v-icon>&nbsp;&nbsp;Результаты опросов
        </v-tab>
      </v-tabs>

      <v-spacer/>
      <span v-if="profile"><b>{{ profile.nickname }}</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
      <v-btn v-if="!profile" @click="openAuthForm" color="#CE7A46" rounded>Авторизация</v-btn>
      <v-btn v-if="profile" @click="logout()" color="#CE7A46" rounded>Выход</v-btn>
    </v-app-bar>

    <v-main>
      <v-tabs-items :value="tab">
        <v-tab-item value="search" :transition="false" :reverse-transition="false">
          <search/>
        </v-tab-item>

        <v-tab-item value="constructor" :transition="false" :reverse-transition="false">
          <constructor/>
        </v-tab-item>

        <v-tab-item value="results" :transition="false" :reverse-transition="false">
          <results/>
        </v-tab-item>
      </v-tabs-items>
    </v-main>

    <my-footer/>
  </v-app>
</template>

<script>
import {mapActions, mapState} from "vuex";
import axios from 'axios'
import AuthForm from "./components/auth/AuthForm.vue";
import Snackbar from "./components/util/Snackbar.vue";
import MyFooter from "./components/mainpage/MyFooter.vue";
import Results from "./components/mainpage/results/Results.vue";
import Constructor from "./components/mainpage/Constructor.vue";
import Search from "./components/mainpage/search/Search.vue";

export default {
  components: {Snackbar, AuthForm, MyFooter, Results, Constructor, Search},
  computed: {
    ...mapState('app', ["profile"]),
    tab: {
      set(tab) {
        this.$router.replace({query: null});
        this.$router.replace({query: {...this.$route.query, tab}})
      },
      get() {
        return this.$route.query.tab
      }
    }
  },
  data() {
    return {
      tabValue: undefined,
    }
  },
  methods: {
    ...mapActions('app', ['openAuthForm']),
    logout() {
      let vue = this
      axios.post("/logout")
          .finally(() => {
            vue.$router.go()
          })
    }
  },
}
</script>

<style scoped>
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
</style>