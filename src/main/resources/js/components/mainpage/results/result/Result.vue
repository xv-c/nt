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
            <v-card color="#ADD8E6" width="900">
                <v-card class="ma-4">
                    <v-card-title class="px-4 pb-0 pt-4">
                        <span style="color: grey">Название опроса:&nbsp;&nbsp;</span>{{ test.name }}
                    </v-card-title>
                    <v-card-title class="px-4 py-0">
                        <span style="color: grey">Описание:&nbsp;&nbsp;</span>{{ test.description }}
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
                        grow hide-slider
                        background-color="#ADD8E6"
                        style="border-radius: 0"
                    >
                        <v-tab>ответы</v-tab>
                        <v-tab>анализ</v-tab>

                        <v-tab-item :transition="false" :reverse-transition="false">
                            <answers
                                :test="test"
                                :results="results"
                            />
                        </v-tab-item>

                        <v-tab-item :transition="false" :reverse-transition="false">
                            <analysis
                                :test="test"
                                :results="results"
                            />
                        </v-tab-item>

                    </v-tabs>
                </v-card>
            </v-card>
        </v-row>
    </v-col>
</template>

<script>
import {mapActions} from "vuex"
import LoadingMask from "../../../util/LoadingMask.vue"
import Chart from "../charts/Chart.vue"
import api from "../../../../use/api";
import endpoints from "../../../../use/endpoints";
import Answers from "./Answers.vue";
import Analysis from "./Analysis.vue";

export default {
    components: {
        Analysis, Answers, Chart, LoadingMask
    },
    data() {
        return {
            test: undefined,
            results: [],
            maskModel: false,
            detailsSearch: ''
        }
    },
    methods: {
        ...mapActions("app", ["showMessage"]),
        returnToResults() {
            let query = Object.assign({}, this.$route.query)
            delete query.resultTestKey
            this.$router.replace({query})
        },
    },
    created() {
        this.maskModel = true
        api.get(`${endpoints.results}${this.$route.query.resultTestKey}`)
            .then(data => {
                if (data.success) {
                    this.test = data.data.test
                    this.results = data.data.results
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
    font-weight: bolder;
    font-size: large;
    background-color: white
}
</style>