<template>
    <div>
        <canvas style="padding: 0;margin: auto;display: block" width="1300" height="500" id="canvas"></canvas>
    </div>
</template>

<script>
    export default {
        methods: {
            draw() {
                const canvas = document.getElementById('canvas');
                const width = 1300;
                const height = 500;
                let ctx = canvas.getContext('2d');

                let random = Math.random();
                let x = random * width;

                random = Math.random();
                let y = random * height;

                random = Math.random();
                let offsetX = (width - x) * random;

                random = Math.random();
                let offsetY = (height - y) * random;
                ctx.strokeRect(x, y, offsetX, offsetY);
            }
        },
        created() {
            setInterval(this.draw, 250)
        }
    }
</script>

<style scoped>

</style>