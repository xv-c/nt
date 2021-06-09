<template>
    <v-card flat tile width="876">
        <v-divider/>

        <v-toolbar tile flat>
            <v-btn @click="getPptx()" text>
                скачать как&nbsp;<b style="color: #D35230">pptx</b>
            </v-btn>

            <v-btn @click="getCsv()" text>
                скачать как&nbsp;<b style="color: #217346">csv</b>
            </v-btn>
        </v-toolbar>

        <v-divider/>

        <template
            v-for="(data, index) in chartsData"
        >
            <v-card-title class="pb-0">
                <span style="color: #5AACC7">Вопрос #{{ index + 1 }}</span>
            </v-card-title>

            <v-card-title class="py-0">
                <span style="color: grey">
                    {{ test.questions[index].question }}
                </span>
            </v-card-title>

            <v-card
                v-if="data.type==='TEXT'"
                class="ma-4 mt-0"
                outlined
            >
                <v-data-table
                    :headers="[{text: 'Ответ', value: 'result'}]"
                    :items="data.results"
                    :search="data.search"
                    no-data-text="Нет текстовых ответов"
                    no-results-text="Не удалось найти подходящие записи"
                    loading-text="Загрузка данных.."
                >
                    <template v-slot:top>
                        <v-card-title>
                            <v-text-field
                                v-model="data.search"
                                placeholder="Поиск в таблице"
                                outlined
                                dense single-line hide-details
                                prepend-inner-icon="search"
                            />
                        </v-card-title>
                    </template>
                </v-data-table>
            </v-card>

            <chart
                v-if="data.type!=='TEXT'"
                :total-results="results.length"
                :chart-data="data.variants"
                ref="chart"
                :id="index"
                :title="test.questions[index].question"
            />
        </template>
    </v-card>
</template>

<script>
import PptxGenJS from "pptxgenjs";
import Chart from "../charts/Chart.vue";

export default {
    props: ['test', 'results'],
    components: {Chart},
    data() {
        return {
            chartsData: []
        }
    },
    created() {
        this.prepareData()
    },
    methods: {
        prepareData() {
            for (let i = 0; i < this.test.questions.length; i++) {
                let vue = this
                let data = {
                    id: vue.test.questions[i].id,
                    value: vue.test.questions[i].question,
                    type: vue.test.questions[i].type
                }

                if (data.type !== "TEXT") {
                    data.variants = vue.test.questions[i].variants
                    for (let j = 0; j < data.variants.length; j++) {
                        data.variants[j].results = 0
                    }
                } else {
                    data.results = []
                }
                this.chartsData.push(data)
            }

            let chartData = this.chartsData
            for (let i = 0; i < this.results.length; i++) {
                let result = this.results[i]

                for (let j = 0; j < chartData.length; j++) {
                    let data = chartData[j]
                    if (data.type === "TEXT")
                        data.results.push({result: result.answers[j].answer})
                    else {
                        for (let k = 0; k < data.variants.length; k++) {
                            for (let l = 0; l < result.answers.length; l++) {
                                let answer = result.answers[l]
                                for (let m = 0; m < answer.answers.length; m++) {
                                    if (answer.answers[m].id === data.variants[k].id) {
                                        data.variants[k].results++
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        getCsv() {
            let getAnswer = (answer) => {
                if (answer.question.type === "MULTI" || answer.question.type === "ONE")
                    return answer.answers.map(it => it.value).join(",")
                else {
                    return answer.answer
                }
            }
            let csvContent = "Пользователь;" + this.test.questions.map(it => it.question).join(";")
            csvContent += "\n" + this.results.map(it => it.respondent === null ? 'null' : it.respondent.nickname + ";" + it.answers.map(answer => getAnswer(answer)).join(";")).join("\n")
            let link = window.document.createElement("a");
            link.setAttribute("href", "data:text/csv;charset=utf-8,%EF%BB%BF" + encodeURI(csvContent));
            link.setAttribute("download", "report.csv");
            link.click();
        },
        async getPptx() {
            let canvasPromises = this.$refs.chart.map(chart => chart.getImg(3));

            await Promise.all(canvasPromises).then(canvases => {
                let pres = new PptxGenJS()
                let maxW = Math.max.apply(Math, canvases.map(c => c.width))
                let maxH = Math.max.apply(Math, canvases.map(c => c.height))

                pres.defineLayout({name: 'A33', width: maxW / 96, height: maxH / 96});
                pres.layout = 'A33'

                for (let i = 0; i < canvases.length; i++) {
                    let canvas = canvases[i]
                    let slide = pres.addSlide()
                    slide.addImage({
                        data: canvas.toDataURL(),
                        w: `${parseInt(`${100.0 * canvas.width / maxW}`)}'%'`,
                        h: `${parseInt(`${100.0 * canvas.height / maxH}`)}'%'`,
                    })
                }
                pres.writeFile('presentation');
            })
        },
    },
}
</script>

<style scoped>

</style>