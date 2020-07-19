import Vue from "vue"
import VueRouter from "vue-router"

Vue.use(VueRouter);

const routes = [
    /*{path: '/', component: MainAlbums},
    {path: '/profile', component: HomeProfile},
    {path: '/users/:id', component: Profile},
    {path: '/items/:id', component: ItemPage},
    {path: '/albums/:id', component: AlbumPage},
    {path: '/registration', component: Registration},
    {path: '/addnew', component: AddAlbumForm},
    {path: '/report', component: ReportProblem},
    {path: '/searchpage/:filter', component: SearchPage},
    {path: '/adminpanel', component: AdminPanel},
    {path: '*', redirect: '/', component: MainAlbums}*/
];

export default new VueRouter({
    mode: 'history',
    routes
})