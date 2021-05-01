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
            <v-card color="#ADD8E6">
                <v-card class="ma-4">
                    <v-card-title class="px-4 pb-0 pt-4">
                        <span style="color: grey">Результаты по опросу:&nbsp;&nbsp;</span>{{ test.name }}
                    </v-card-title>
                    <v-card-title class="px-4 py-0">
                        <span style="color: grey">Ключ:&nbsp;&nbsp;</span>{{ test.key }}
                    </v-card-title>
                    <v-card-title class="px-4 pt-0 pb-4">
                        <span style="color: grey">Ответов:&nbsp;&nbsp;</span>{{ results.length }}
                    </v-card-title>
                </v-card>
                <v-card class="ma-4">
                    <v-tabs
                        v-if="results.length>0"
                        active-class="selected-tab"
                        background-color="#5AACC7"
                        color="black"
                        grow hide-slider
                    >
                        <v-tab>ответы</v-tab>
                        <v-tab>подробнее</v-tab>
                        <v-tab>анализ</v-tab>

                        <v-tab-item :transition="false" :reverse-transition="false">
                            <v-toolbar tile flat>
                                <v-btn @click="getPptx()" text>
                                    скачать как&nbsp;<b style="color: #D35230">pptx</b>
                                </v-btn>
                            </v-toolbar>

                            <v-divider/>

                            <template
                                v-for="(data, index) in chartsData"
                            >
                                <v-card-title class="pb-0">
                  <span style="color: grey">
                    {{ test.questions[index].question }}
                  </span>
                                    <v-spacer/>
                                    <span style="color: #5AACC7">Вопрос #{{ index + 1 }}</span>
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
                                                    placeholder="Поиск по таблице"
                                                    outlined dense single-line
                                                    hide-details
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
                                    :question="test.questions[index].question"
                                />
                            </template>
                        </v-tab-item>

                        <v-tab-item :transition="false" :reverse-transition="false">
                            <v-toolbar tile flat>
                                <v-btn @click="getCsv()" text>
                                    скачать как&nbsp;<b style="color: #217346">csv</b>
                                </v-btn>
                            </v-toolbar>

                            <v-divider/>

                            <v-card class="ma-4" outlined>
                                <v-data-table
                                    :headers="[{text: 'Ответ', value: 'result'}]"
                                    :items="results"
                                    :search="detailsSearch"
                                    no-data-text="Нет текстовых ответов"
                                    no-results-text="Не удалось найти подходящие записи"
                                    loading-text="Загрузка данных.."
                                >
                                    <template v-slot:top>
                                        <v-card-title>
                                            <v-text-field
                                                v-model="detailsSearch"
                                                placeholder="Поиск по таблице"
                                                outlined dense single-line
                                                hide-details
                                            />
                                        </v-card-title>
                                    </template>
                                </v-data-table>
                            </v-card>
                        </v-tab-item>

                        <v-tab-item :transition="false" :reverse-transition="false">
                            <v-btn>TODO</v-btn>
                        </v-tab-item>

                    </v-tabs>
                </v-card>
            </v-card>
        </v-row>
    </v-col>
</template>

<script>
import {mapActions} from "vuex"
import LoadingMask from "../../util/LoadingMask.vue"
import Chart from "./charts/Chart.vue"
import PptxGenJS from "pptxgenjs";
import api from "../../../use/api";

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
            maskModel: false,
            detailsSearch: ''
        }
    },
    methods: {
        ...mapActions("app", ["showMessage"]),
        getCsv() {

        },
        async getPptx() {
            this.maskModel = true
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
            console.log("results")
            console.log(this.results)
        }
    },
    created() {
        this.maskModel = true
        api.get(`/api/results/${this.$route.query.resultTestKey}`)
            .then(data => {
                if (data.success) {
                    this.test = data.data.test
                    this.results = data.data.results
                    this.prepareData()
                } else {
                    this.showMessage(data.message)
                    this.returnToResults()
                }
            })
            .finally(() => {
                this.maskModel = false
            })
    }
}
</script>

<style scoped>
.selected-tab {
    background-color: #91CAD8
}
</style>