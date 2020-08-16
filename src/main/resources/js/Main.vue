<template>
    <v-app>
        <v-app-bar color="#5AACC7" flat app>
            <v-btn href="/" text>Главная</v-btn>
            <v-spacer/>
            <span v-if="profile"><b>{{profile.nickname}}</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
            <v-btn v-if="!profile" @click="openAuthForm" color="#CE7A46" rounded>Авторизация</v-btn>
            <v-btn v-if="profile" href="/logout" color="#CE7A46" rounded>Выход</v-btn>
        </v-app-bar>

        <snackbar/>
        <auth-form/>

        <v-main>
            <router-view/>
        </v-main>

        <v-footer>
            <canvas height="200" width="4000" id="canvas"></canvas>
        </v-footer>
    </v-app>
</template>

<script>
    import {mapActions, mapState} from "vuex";
    import AuthForm from "./components/forms/AuthForm.vue";
    import Snackbar from "./components/util/Snackbar.vue";

    export default {
        components: {Snackbar, AuthForm},
        computed: {
            ...mapState('app', ["profile"])
        },
        methods: {
            ...mapActions('app', ['openAuthForm']),
            draw() {
                const canvas = document.getElementById('canvas')
                let width = canvas.width
                let height = canvas.height
                let ctx = canvas.getContext('2d')

                let x = Math.random() * width
                let y = Math.random() * height

                let offsetX = 10;
                let offsetY = 20;

                ctx.fillStyle = 'rgb(0,' + Math.floor(x / width * 255) + ',' + Math.floor(y / height * 255) + ')'

                ctx.fillRect(x, y, offsetX, offsetY)
            }
        },
        mounted() {
            const canvas = document.getElementById('canvas')
            let width = canvas.width
            let height = canvas.height

            let ctx = canvas.getContext('2d')
            ctx.fillRect(0, 0, width, height);
            setInterval(this.draw, 1)
        }
    }
</script>

<style scoped>
    canvas {
        width: 100%;
        height: 20px;
    }
</style>