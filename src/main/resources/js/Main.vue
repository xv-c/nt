<template>
    <v-app>
        <v-app-bar color="#5AACC7" flat app>
            <v-btn href="/" text>Главная</v-btn>
            <v-spacer/>
            <v-btn href="/login" text>Вход</v-btn>
            <v-btn to="/registration" text>Регистрация</v-btn>
        </v-app-bar>
        <v-main>
            <router-view/>
        </v-main>
        <v-footer>
            <canvas height="20" width="4000" id="canvas"></canvas>
        </v-footer>
    </v-app>
</template>

<script>
    export default {
        methods: {
            draw() {
                const canvas = document.getElementById('canvas');
                let width = canvas.width;
                let height = canvas.height;
                let ctx = canvas.getContext('2d');

                let random = Math.random();
                let x = random * width;

                random = Math.random();
                let y = random * height;

                random = Math.random();
                let offsetX = (width - x) * random / 2;

                random = Math.random();
                let offsetY = (height - y) * random / 2;

                ctx.strokeStyle = 'rgb(0,' + Math.floor(x/width*255) + ',' + Math.floor(y/height*255) + ')';

                ctx.strokeRect(x, y, offsetX, offsetY);
            }
        },
        created() {
            setInterval(this.draw, 250)
        }
    }
</script>

<style scoped>
    canvas{
        width: 100%;
        height: 20px;
    }
</style>