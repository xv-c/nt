<template>
    <v-col>
        <loading-mask :mask-model="maskModel"/>

        <v-row>
            <v-btn @click="returnToResults()" :ripple="false" text>
                <v-icon>keyboard_arrow_left</v-icon>
                вернутся к списку результатов
            </v-btn>
        </v-row>

        <v-row justify="center" v-if="test && results">
            <v-card color="#ADD8E6" width="1000">
                <v-row justify="center" style="margin-top: 20px">
                    <v-card width="950">
                        <v-card-title>
                            <span style="color: grey">Результаты по опросу:&nbsp;&nbsp;</span>{{ test.name }}
                            <v-spacer/>
                            <span style="color: grey">Ключ:&nbsp;&nbsp;</span>{{ test.key }}
                        </v-card-title>
                        <v-card-title style="margin-top: -30px">
                            <span style="color: grey">Ответов:&nbsp;&nbsp;</span>{{ results.length }}
                        </v-card-title>
                    </v-card>
                </v-row>
                <v-row justify="center" style="margin-top: 20px; margin-bottom: 15px">
                    <v-card width="950">
                        <v-tabs v-if="results.length>0"
                                grow
                                active-class="selected-tab" hide-slider
                                background-color="#5AACC7"
                                color="black"
                                style="border-radius: 0; margin-bottom: 35px">
                            <v-tab>ответы</v-tab>
                            <v-tab>анализ</v-tab>

                            <v-tab-item :transition="false" :reverse-transition="false">
                                <v-toolbar tile flat>
                                    <v-btn @click="getPptx()" text>скачать как&nbsp;<b style="color: #D35230">pptx</b></v-btn>
                                </v-toolbar>
                                <v-row v-for="(data, index) in chartsData" :key="undefined"
                                       style="margin-top: 20px" justify="center">
                                    <v-row style="margin: 10px 5%;">
                <span style="color: grey; font-weight: bold; font-size: large">{{
                        test.questions[index].question
                    }}</span>
                                        <v-spacer/>
                                        <span style="color: #5AACC7">Вопрос #{{ index + 1 }}</span>
                                    </v-row>

                                    <v-card v-if="data.type==='TEXT'"
                                            tile
                                            width="850">
                                        <v-card-title v-for="(answer, index) in data.results" :key="undefined">
                                            <v-chip :style="index>0?'margin-top:-3%':''">
                                                {{ answer }}
                                            </v-chip>
                                        </v-card-title>
                                    </v-card>
                                    <chart v-if="data.type!=='TEXT'"
                                           :total-results="results.length"
                                           :chart-data="data.variants"
                                           ref="chart"
                                           :id="index"
                                           :question="test.questions[index].question"
                                    />
                                </v-row>
                            </v-tab-item>

                            <v-tab-item :transition="false" :reverse-transition="false">
                                <v-btn>TODO</v-btn>
                            </v-tab-item>

                        </v-tabs>
                    </v-card>
                </v-row>
            </v-card>
        </v-row>
    </v-col>
</template>

<script>
import axios from 'axios'
import {mapActions} from "vuex"
import LoadingMask from "../../util/LoadingMask.vue"
import Chart from "./charts/Chart.vue"
import PptxGenJS from "pptxgenjs";

export default {
    components: {
        Chart,
        LoadingMask
    },
    data() {
        return {
            test: undefined,
            results: [],
            chartsData: [],
            maskModel: false
        }
    },
    methods: {
        ...mapActions("app", ["showMessage"]),
        async getPptx() {
            this.maskModel = true
            let canvasPromises = this.$refs.chart.map(chart => chart.getImg(3));

            await Promise.all(canvasPromises).then(canvases => {
                let pres = new PptxGenJS()
                let maxW = Math.max.apply(Math, canvases.map(c => c.width))
                let maxH = Math.max.apply(Math, canvases.map(c => c.height))

                pres.defineLayout({ name:'A33', width:maxW/96, height:maxH/96 });
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
                .finally(() => {
                    this.maskModel = false
                })
        },
        returnToResults() {
            let query = Object.assign({}, this.$route.query)
            delete query.resultTestKey
            this.$router.replace({query})
        },
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
                        data.results.push(result.answers[j].answer)
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
        }
    },
    created() {
        let vue = this
        vue.maskModel = true
        axios.get(`/api/results/${this.$route.query.resultTestKey}`)
            .then(
                response => {
                    vue.test = response.data.data.test
                    vue.results = response.data.data.results
                    vue.prepareData()
                })
            .catch(
                error => {
                    vue.showMessage(error.response.data.message)
                    vue.returnToResults()
                })
            .finally(() => {
                vue.maskModel = false
            })
    }
}
</script>

<style scoped>
.selected-tab {
    background-color: #91CAD8
}
</style>