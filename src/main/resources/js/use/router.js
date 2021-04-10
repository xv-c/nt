import Vue from "vue"
import VueRouter from "vue-router"
import Index from "../Index.vue";
import Constructor from "../components/mainpage/Constructor.vue";
import Search from "../components/mainpage/search/Search.vue";
import Results from "../components/mainpage/results/Results.vue";

Vue.use(VueRouter);

const routes = [
    {path: '/constructor', component: Constructor},
    {path: '/results', component: Results},
    {path: '/search', component: Search},
    {path: '*', component: Index, redirect: '/search'}
];

export default new VueRouter({
    mode: 'history',
    routes
})