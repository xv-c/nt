<template>
    <v-row justify="center" v-if="test">
        <v-card width="650">
            <v-card-title>
                {{ test.name }}
            </v-card-title>
            <v-card-subtitle style="margin-top: 5px; font-weight: bold">
                {{ test.description}}
            </v-card-subtitle>
            <v-card-text>
                <template v-for="(question, questionIndex) in test.questions">
                    <v-row style="margin-left: 2%; margin-right: 2%">
                        <span style="font-weight: bold; font-size: large">{{question.question}}</span>
                        <v-spacer/>
                        <span style="color: #5AACC7">Вопрос #{{ questionIndex + 1 }}</span>
                    </v-row>
                    <v-row v-if="question.type!=='TEXT'"
                           v-for="(variant, variantIndex) in question.variants" :key="undefined">
                        <v-checkbox v-if="question.type==='MULTI'" v-model="answerForm[questionIndex].variants[variantIndex].value">
                            <template v-slot:label>
                                <span style="color:black;">{{ variant.text }}</span>
                            </template>
                        </v-checkbox>
                        <v-radio-group></v-radio-group>
                    </v-row>
                    <v-textarea v-if="question.type==='TEXT'"/>
                </template>
            </v-card-text>
        </v-card>
    </v-row>
</template>

<script>
    import axios from "axios";
    import {mapActions} from "vuex";

    export default {
        data(){
            return{
                test: undefined,
                answerForm: [],
            }
        },
        methods:{
            ...mapActions("app", ["showMessage"])
        },
        created() {
            let vue = this
            axios.get('/api/tests/'+this.$route.params.key)
                .then(
                    function (response) {
                        let test = response.data.data.test
                        vue.test = test

                        for (let i = 0; i < test.questions.length; i++) {
                            let question = test.questions[i]
                            if(question.type==='TEXT'){
                                vue.answerForm.push({value:''})
                            } else {
                                let variants = []
                                for (let j = 0; j < question.variants.length; j++) {
                                    variants.push({value: false})
                                }
                                vue.answerForm.push({variants:variants})
                            }
                        }
                    })
                .catch(
                    function (error) {
                        if(!error.response || !error.response.data || !error.response.data.message){
                            vue.showMessage("Не удалось выполнить запрос")
                            return
                        }
                        if (error.response.data.message)
                            vue.showMessage(error.response.data.message)
                    })
        }
    }
</script>

<style scoped>

</style>