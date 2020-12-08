<template>
  <v-footer padless tile dark >
    <canvas height="200" width="4000" id="canvas"/>
  </v-footer>
</template>

<script>
export default {
  mounted() {
    const canvas = document.getElementById('canvas')
    let width = canvas.width
    let height = canvas.height

    let ctx = canvas.getContext('2d')
    ctx.fillRect(0, 0, width, height);
    setInterval(this.draw, 1)
  },
  methods: {
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
    },
  }
}
</script>

<style scoped>
canvas {
  width: 100%;
  height: 20px;
}
</style>