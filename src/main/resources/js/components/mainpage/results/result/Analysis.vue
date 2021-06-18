<template>
    <v-card flat tile width="876">
        <v-divider/>

        <v-toolbar tile flat>
            <v-btn @click="startAnalysis" text>
                выполнить анализ
            </v-btn>
            <v-btn text @click="getPptx">
                скачать как&nbsp;<b style="color: #D35230">pptx</b>
            </v-btn>
        </v-toolbar>

        <v-divider/>

        <div v-if="!test.analysed">
            <v-card-title>
                Анализ не проведен
            </v-card-title>
        </div>

        <v-tabs
            v-else
            v-model="activeTab"
            active-class="selected-tab"
            background-color="#ADD8E6"
            hide-slider grow
        >
            <v-tab>Нетекстовые ответы</v-tab>
            <v-tab>Текстовые ответы</v-tab>

            <v-tab-item :transition="false" :reverse-transition="false">
                <v-divider/>

                <v-card-title v-if="nonTextChartsData.length === 0">
                    Не удалось выявить зависимости
                </v-card-title>

                <div v-else>
                    <v-toolbar tile flat>
                        <v-btn @click="filter.model = true" text>
                            <v-icon>filter_alt</v-icon>
                            фильтрация
                        </v-btn>
                    </v-toolbar>

                    <v-dialog width="800" v-model="filter.model">
                        <v-data-table v-model="filterValues"
                                      :items="getNonTextFiltered"
                                      :headers="[{text: 'Вопрос', value: 'question'}]"
                                      show-select/>
                    </v-dialog>
                </div>

                <div
                    v-for="(data, index) in filteredNonTextChartsData"
                >
                    <v-card-title class="pb-0">
                        Пользователи, выбравшие&nbsp;<b style="color: #91CAD8">{{ data.answer }}</b>&nbsp;в вопросе&nbsp;<b style="color: #91CAD8">{{ data.question }}</b>, в вопросе&nbsp;<b style="color: #91CAD8">{{ data.childQuestion }}</b>&nbsp;выбирали:
                    </v-card-title>

                    <chart
                        :chart-data="data.variants"
                        :title="''"
                        :total-results="data.total"
                        :id="index"
                        ref="nonTextChartsData"
                    />
                </div>
            </v-tab-item>
            <v-tab-item :transition="false" :reverse-transition="false">
                <v-divider/>

                <div v-if="textChartsData.length === 0">
                    <v-card-title>
                        Не удалось выявить зависимости
                    </v-card-title>
                </div>

                <div
                    v-for="(data, index) in textChartsData"
                >
                    <v-card-title class="pb-0">
              <span style="color: grey">
                {{ data.title }}
              </span>
                        <v-spacer/>
                        <v-btn class="ma-4" color="blue" outlined @click="data.details = !data.details">
                            {{ data.details ? 'в виде диаграммы' : 'подробнее' }}
                        </v-btn>
                    </v-card-title>
                    <chart
                        v-if="!data.details"
                        :chart-data="data.variants"
                        :title="data.title"
                        :total-results="results.length"
                        :id="index"
                        ref="nonTextChartsData"
                    />
                    <v-data-table
                        v-else
                        :headers="[{text:'Текст',value: 'answer'}, {text: 'Тональность', value: 'tonality'}]"
                        :items="getItemsFor(index)"
                        :search="data.search"
                    >
                        <template v-slot:top>
                            <v-divider/>
                            <v-text-field
                                v-model="data.search"
                                class="pa-4"
                                outlined
                                single-line dense hide-details
                                placeholder="Поиск в таблице"
                                prepend-inner-icon="search"
                            />
                            <v-divider/>
                        </template>
                        <template v-slot:item.tonality="{item}">
                            <v-chip :color="item.tonality === 'Позитивная' ? '#B5FFC0' : '#FF8A82'">
                                {{ item.tonality }}
                            </v-chip>
                        </template>
                    </v-data-table>
                </div>
            </v-tab-item>
        </v-tabs>

    </v-card>
</template>

<script>
import api from "../../../../use/api";
import endpoints from "../../../../use/endpoints";
import {mapActions} from "vuex";
import Chart from "../charts/Chart.vue";
import PptxGenJS from "pptxgenjs";

export default {
    components: {Chart},
    props: ['test', 'results'],
    created() {
        if (this.test.analysed) {
            this.loadAnalysis()
        }
    },
    watch: {
        filterValues(selected) {
            let selectedHas = (value) => {
                let hasQuestion = false
                let hasChildQuestion = false
                for (let i = 0; i < selected.length; i++) {
                    if (selected[i].question === value.question)
                        hasQuestion = true
                    if (selected[i].question === value.childQuestion)
                        hasChildQuestion = true
                }
                return hasQuestion && hasChildQuestion
            }
            this.filteredNonTextChartsData = this.nonTextChartsData.filter(x => selectedHas(x))
        }
    },
    computed: {
        getNonTextFiltered() {
            if (!this.filterValues)
                this.filterValues = this.test.questions.filter(x => x.type !== 'TEXT')
            return this.test.questions.filter(x => x.type !== 'TEXT')
        }
    },
    data() {
        return {
            activeTab: undefined,
            textActiveTab: undefined,
            analysis: undefined,
            filter: {
                model: false,
            },
            filterValues: undefined,
            nonTextChartsData: [],
            filteredNonTextChartsData: [],
            textChartsData: [],
        }
    },
    methods: {
        ...mapActions("app", ["showMessage"]),
        async getPptx() {
            let canvasPromises = undefined
            if (this.activeTab === 0)
                canvasPromises = this.$refs.nonTextChartsData.map(chart => chart.getImg(3));
            else
                canvasPromises = this.$refs.textextChartsData.map(chart => chart.getImg(3));

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
        getItemsFor(textIndex) {
            let index = -1
            let i = 0
            while (index !== textIndex) {
                if (this.test.questions[i].type === 'TEXT')
                    index++
                if (index !== textIndex)
                    i++
            }
            index = i
            let result = []

            for (let j = 0; j < this.results.length; j++) {
                if (this.results[j].answers[index].tonality !== null) {
                    let tonality = this.results[j].answers[index].tonality ? 'Позитивная' : 'Негативная'
                    result.push({answer: this.results[j].answers[index].answer, tonality: tonality})

                }
            }
            return result
        },
        loadAnalysis() {
            api.get(`${endpoints.analysis}${this.test.key}`)
                .then(resp => {
                    if (resp.success) {
                        this.analysis = resp.data.analysis
                        this.prepareTextChartsData()
                        this.prepareNonTextChartsData()
                    } else {
                        this.showMessage(resp.message)
                    }
                })
        },
        startAnalysis() {
            this.maskModel = true
            api.post(endpoints.analysis + this.test.key)
                .then(data => {
                    this.maskModel = false
                    if (data.success) {
                        this.showMessage("Анализ выполнен")
                        this.$router.go()
                    } else
                        this.showMessage(data.message)
                })
        },
        prepareTextChartsData() {
            for (let i = 0; i < this.test.questions.length; i++) {
                let question = this.test.questions[i]

                if (question.type === "TEXT") {
                    let positive = 0
                    let negative = 0
                    for (let j = 0; j < this.results.length; j++) {
                        if (this.results[j].answers[i].tonality)
                            positive++
                        else
                            negative++
                    }
                    this.textChartsData.push({
                        title: `${question.question}`,
                        details: false,
                        variants: [
                            {results: positive, value: 'Позитивные'},
                            {results: negative, value: 'Негативные'}
                        ]
                    })
                }
            }
        },
        prepareNonTextChartsData() {
            let flat = this.flatData()
            for (let i = 0; i < flat.length; i++) {
                let data = flat[i]

                data.counts.forEach((value, key) => {
                    if (value.total === 0)
                        return

                    let chartData = {
                        answer: data.answer,
                        question: data.question,
                        childQuestion: value.question,
                        total: value.total,
                        variants: []
                    }

                    for (let j = 0; j < value.variants.length; j++) {
                        chartData.variants.push({
                            results: value.variants[j].total,
                            value: value.variants[j].answer
                        })
                    }
                    this.nonTextChartsData.push(chartData)
                })
            }
            this.nonTextChartsData = this.nonTextChartsData.sort((it, other) => it.total - other.total)
            this.filteredNonTextChartsData = this.nonTextChartsData
        },
        flatData() {
            let flatted = []
            for (let i = 0; i < this.analysis.stats.length; i++) {
                let stat = this.analysis.stats[i]

                let statObj = {
                    question: stat.forQuestion.question,
                    answer: stat.forAnswer.value,
                    counts: new Map()
                }

                for (let j = 0; j < stat.counts.length; j++) {
                    let count = stat.counts[j]

                    if (statObj.counts.has(count.forQuestion.id)) {
                        let child = statObj.counts.get(count.forQuestion.id)
                        child.total += count.count
                        child.variants.push({
                            total: count.count,
                            answer: count.forAnswer.value
                        })
                    } else {
                        statObj.counts.set(count.forQuestion.id, {
                            total: count.count,
                            question: count.forQuestion.question,
                            variants: [{
                                total: count.count,
                                answer: count.forAnswer.value
                            }]
                        })
                    }
                }

                flatted.push(statObj)
            }
            return flatted
        },
    },
}
</script>

<style scoped>
.selected-tab {
    font-weight: bolder;
    font-size: large;
    background-color: white
}
</style>