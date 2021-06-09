<template>
    <v-container>
        <p v-if="!profile" class="font-weight-black text-center" style="margin-top: 10%">
            <b>Пожалуйста, <a @click="openAuthForm">авторизуйтесь</a>,
                чтобы начать работу с конструктором тестов!
            </b>
        </p>

        <v-row v-if="profile" justify="center" class="ma-0 pa-0">
            <v-sheet :rounded="!isTile" :tile="isTile" :flat="isTile" :width="cardWidth" color="#ADD8E6">
                <v-card class="ma-4">
                    <v-card-subtitle>
                        <v-text-field
                            v-model="form.name"
                            placeholder="Название"
                            counter="50"
                            outlined dense
                            hint="Поле не может быть пустым"
                            :persistent-hint="form.name.length===0"
                            :color="form.name.length === 0 ? 'red' : 'blue'"
                        >
                            <template slot="prepend-inner">
                                <v-icon size="x-small" :color="form.name.length === 0 ? 'red' : 'blue'">
                                    fiber_manual_record
                                </v-icon>
                            </template>
                        </v-text-field>
                        <v-textarea
                            v-model="form.description"
                            placeholder="Краткое описание"
                            counter="200"
                            :color="form.description.length===0? 'red' : 'blue'"
                            outlined dense
                            hint="Поле не может быть пустым"
                            :persistent-hint="form.description.length===0"
                        >
                            <template slot="prepend-inner">
                                <v-icon size="x-small" :color="form.description.length === 0 ? 'red' : 'blue'">
                                    fiber_manual_record
                                </v-icon>
                            </template>
                        </v-textarea>
                        <v-checkbox v-model="form.loginRequired" class="mt-0" hide-details>
                            <template v-slot:label>
                                <span style="color:black;">Необходима аутентификация</span>
                            </template>
                        </v-checkbox>
                        <v-checkbox v-model="form.public" class="mt-0" hide-details>
                            <template v-slot:label>
                                <span style="color:black;">Публичный тест</span>
                            </template>
                        </v-checkbox>
                    </v-card-subtitle>
                </v-card>

                <v-card
                    v-for="(question, questionIndex) in form.questions"
                    :key="form.qId"
                    class="ma-4"
                >
                    <v-card-title class="pb-0">
                        <span style="color: #5AACC7">Вопрос #{{ questionIndex + 1 }}</span>
                        <v-spacer/>
                        <v-btn
                            v-if="questionIndex!==(form.questions.length - 1)"
                            @click="downQuestion(questionIndex)"
                            icon>
                            <v-icon>expand_more</v-icon>
                        </v-btn>

                        <v-btn
                            v-if="questionIndex!==0 && form.questions.length !== 1"
                            @click="downQuestion(questionIndex-1)"
                            icon>
                            <v-icon>expand_less</v-icon>
                        </v-btn>

                        <v-hover v-slot:default="{ hover }">
                            <v-btn @click="removeQuestion(questionIndex)" :disabled="form.questions.length===1" icon>
                                <v-icon :color="hover ? 'red' : undefined">clear</v-icon>
                            </v-btn>
                        </v-hover>
                    </v-card-title>

                    <v-card-title class="pt-0">
                        <v-text-field
                            v-model="question.question"
                            placeholder="Вопрос"
                            counter="200"
                            hint="Поле не может быть пустым"
                            :persistent-hint="question.question.length===0"
                            outlined dense
                        >
                            <template slot="prepend-inner">
                                <v-icon size="x-small" :color="question.question.length===0 ? 'red' : 'blue'">
                                    fiber_manual_record
                                </v-icon>
                            </template>
                        </v-text-field>
                    </v-card-title>

                    <v-card-subtitle>
                        <v-select
                            v-model="question.type"
                            :items="questionTypes"
                            item-text="text"
                            item-value="type"
                            outlined dense hide-details
                        />
                    </v-card-subtitle>

                    <v-card-text v-if="question.type!=='TEXT'">
                        <v-card outlined style="border: 1px solid #9E9E9E">
                            <v-card-actions
                                v-for="(variant, variantIndex) in question.variants"
                                :key="undefined"
                                class="pt-2 pb-0"
                            >
                                <v-text-field
                                    v-model="variant.value"
                                    placeholder="Вариант ответа"
                                    outlined dense single-line
                                    hide-details
                                >
                                    <template slot="prepend-inner">
                                        <v-icon size="x-small" :color="variant.value.length===0 ? 'red' : 'blue'">
                                            fiber_manual_record
                                        </v-icon>
                                    </template>
                                </v-text-field>
                                <v-hover v-slot:default="{ hover }">
                                    <v-btn @click="removeVariant(questionIndex, variantIndex)"
                                           :disabled="question.variants.length===2" icon>
                                        <v-icon :color="hover ? 'red' : undefined">clear</v-icon>
                                    </v-btn>
                                </v-hover>
                            </v-card-actions>
                            <v-card-actions style="background-color: white">
                                <v-btn
                                    @click="addVariant(question)"
                                    color="blue"
                                    outlined>Добавить вариант ответа
                                </v-btn>
                            </v-card-actions>
                        </v-card>
                    </v-card-text>
                </v-card>
                <v-card class="ma-4">
                    <v-card-actions class="px-4 py-4">
                        <v-btn :disabled="!isValid"
                               @click="postTest()" color="blue" outlined>готово
                        </v-btn>
                        <v-spacer/>
                        <v-btn @click="addQuestion()" color="blue" outlined>
                            <span v-if="!isTile">добавить&nbsp;</span>
                            <v-icon v-else>add</v-icon>
                            вопрос
                        </v-btn>
                        <v-spacer/>
                        <v-btn @click="clear()" color="blue" outlined>очистить</v-btn>
                    </v-card-actions>
                </v-card>
            </v-sheet>
        </v-row>
    </v-container>
</template>

<script>
import {mapActions, mapState} from "vuex";
import api from "../../use/api";
import endpoints from "../../use/endpoints";

export default {
    computed: {
        ...mapState('app', ["profile"]),
        isValid() {
            if (this.form.name.length === 0 || this.form.name.length > 50)
                return false

            if (this.form.description.length === 0 || this.form.description.length > 200)
                return false

            for (let i = 0; i < this.form.questions.length; i++) {
                let currQuestion = this.form.questions[i]
                if (currQuestion.question.length === 0 || currQuestion.question.length > 200)
                    return false

                if (currQuestion.type !== 'TEXT') {
                    for (let j = 0; j < currQuestion.variants.length; j++) {
                        let variantTextLength = currQuestion.variants[j].value.length
                        if (variantTextLength === 0
                            || variantTextLength > 50)
                            return false
                    }
                }
            }
            return true
        },
        isTile() {
            return this.cardWidth === this.$vuetify.breakpoint.width
        },
        cardWidth() {
            if (this.$vuetify.breakpoint.width < 1000)
                return this.$vuetify.breakpoint.width

            return this.$vuetify.breakpoint.width * 0.40
        }
    },
    data() {
        return {
            questionTypes: [
                {text: 'Один из списка', type: 'ONE'},
                {text: 'Несколько из списка', type: 'MULTI'},
                {text: 'Текстовый ответ', type: 'TEXT'}
            ],
            form: {
                qId: 0,
                name: 'Новая форма',
                description: '',
                questions: [],
                loginRequired: false,
                public: false
            }
        }
    },
    methods: {
        ...mapActions('app', ["openAuthForm", "showMessage"]),
        downQuestion(index){
            let x = index
            let y = index + 1
            let list = this.form.questions

            let b = list[y];
            list[y] = list[x];
            list[x] = b;
            this.form.qId++
        },
        clear() {
            this.form.name = 'Новая форма'
            this.form.description = ''
            this.form.questions = []
            this.form.loginRequired = false
            this.addQuestion()
        },
        postTest() {
            api.post(endpoints.tests, this.form)
                .then(data => {
                    if (data.success)
                        this.showMessage(`Тест успешно добавлен! Ключ опроса: ${data.data.test.key}`)
                    else
                        this.showMessage(data.message)
                })
        },
        addQuestion() {
            this.form.questions.push({
                type: this.questionTypes[0].type,
                question: '',
                variants: []
            })
            this.form.qId++
            this.addVariant(this.form.questions[this.form.questions.length - 1])
            this.addVariant(this.form.questions[this.form.questions.length - 1])
        },
        addVariant(item) {
            item.variants.push({value: ''})
        },
        removeQuestion(index) {
            this.form.questions.splice(index, 1)
        },
        removeVariant(questionIndex, variantIndex) {
            this.form.questions[questionIndex].variants.splice(variantIndex, 1)
        },
    },
    created() {
        this.addQuestion()
    }
}
</script>

<style scoped>

</style>