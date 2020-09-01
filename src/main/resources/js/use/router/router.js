import Vue from "vue"
import VueRouter from "vue-router"
import Registration from "../../components/forms/Registration.vue"
import MainPage from "../../pages/MainPage.vue"
import Test from "../../components/Test.vue"

Vue.use(VueRouter);

const routes = [
    {path: '/registration', component: Registration},
    {path: '/', component: MainPage},
    {path: '/test/:key', component: Test},
    {path: '*', redirect: '/'}
];

export default new VueRouter({
    mode: 'history',
    routes
})