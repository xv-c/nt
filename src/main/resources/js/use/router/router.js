import Vue from "vue"
import VueRouter from "vue-router"
import Registration from "../../components/auth/Registration.vue"
import MainPage from "../../pages/Main.vue"
import Test from "../../pages/Test.vue"
import ResultsPage from "../../pages/Results.vue";

Vue.use(VueRouter);

const routes = [
    {path: '/registration', component: Registration},
    {path: '/', component: MainPage},
    {path: '/test/:key', component: Test},
    {path: '*', redirect: '/'},
    {path: '/results/:key', component: ResultsPage}
];

export default new VueRouter({
    mode: 'history',
    routes
})