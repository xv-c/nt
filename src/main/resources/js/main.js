import Vue from 'vue'
import Vuetify from 'vuetify'
import '@babel/polyfill'

import Main from "./Index.vue"
import store from "./use/store/store.js"
import router from './use/router.js'
import 'vuetify/dist/vuetify.min.css'

import ru from "vuetify/src/locale/ru.ts"

Vue.use(Vuetify);

new Vue({
    el: '#vueapp',
    router,
    store,
    vuetify: new Vuetify({
        icons: {
            iconfont: "mdi" || "fa"
        },
        lang:{
            locales: { ru },
            current: 'ru'
        }
    }),
    render: a => a(Main)
});