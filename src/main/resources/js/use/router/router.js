import Vue from "vue"
import VueRouter from "vue-router"
import Registration from "../../components/forms/Registration.vue";
import MainPage from "../../pages/MainPage.vue";

Vue.use(VueRouter);

const routes = [
    {path:'/registration', component: Registration},
    {path: '/', component: MainPage},
    {path: '*', redirect: '/'}
];

export default new VueRouter({
    mode: 'history',
    routes
})