function draw() {

    if (canvas.getContext) {
        let random = Math.random();
        let ctx = canvas.getContext('2d');

        let x = random*width;
        let y = random*height;
        random = Math.random();
        let offsetX = (width-x)*random;
        let offsetY = (height-y)*random;
        ctx.strokeRect(x,y, offsetX, offsetY);

    }
}

const canvas = document.getElementById('canvas');
const width = 1300;
const height = 500;

setInterval(draw, 250);
ё