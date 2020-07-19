import Vue from 'vue'
import Vuetify from 'vuetify'
import '@babel/polyfill'

import Main from "./pages/Main.vue";
import store from "./use/store/store.js"
import router from 'use/router/router'

Vue.use(Vuetify);

new Vue({
    el: '#vueapp',
    router,
    store,
    vuetify: new Vuetify({
        icons: {
            iconfont: "mdi" || "fa"
        }
    }),
    render: a => a(Main)
});