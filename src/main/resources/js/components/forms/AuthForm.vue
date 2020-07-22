<template>
    <v-dialog max-width="450" v-model="dialogModel">
        <v-card tile flat color="#add8e6">
            <v-card-title v-if="isLogin">
                <v-spacer/>
                Вход
                <v-spacer/>
                <v-btn @click="isLogin=false" text :ripple="false">Регистрация
                    <v-icon>keyboard_arrow_right</v-icon>
                </v-btn>
            </v-card-title>

            <v-card-title v-if="!isLogin">
                <v-btn @click="isLogin=true" text :ripple="false">
                    <v-icon>keyboard_arrow_left</v-icon>
                    Вход
                </v-btn>
                <v-spacer/>
                Регистрация
                <v-spacer/>
            </v-card-title>

            <v-row style="margin-left: 10px; margin-right: 10px">
                <v-col><v-divider/></v-col>
            </v-row>

            <login v-if="isLogin"></login>
            <registration v-if="!isLogin"/>

            <v-row style="margin-left: 10px; margin-right: 10px">
                <v-col cols="5">
                    <v-divider/>
                </v-col>
                <v-col style="text-align: center; margin-top: -10px" cols="2">
                    <span>или</span>
                </v-col>
                <v-col cols="5">
                    <v-divider/>
                </v-col>
            </v-row>

            <v-card-actions>
                <v-col>
                    <v-expansion-panels style="color: blue">
                        <v-expansion-panel>
                            <v-expansion-panel-header><b>Авторизоваться с помощью сервиса</b>
                            </v-expansion-panel-header>
                            <v-expansion-panel-content>
                                <v-btn text>
                                    <v-icon color="blue">fab fa-google</v-icon>&nbsp;&nbsp;Google
                                </v-btn>
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-expansion-panels>
                </v-col>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapActions, mapState} from 'vuex'
    import Login from "./Login.vue"
    import Registration from "./Registration.vue";

    export default {
        components: {Login, Registration},
        computed: {
            ...mapState('app', ['isAuthFormOpen']),
            dialogModel: {
                get: function () {
                    return this.isAuthFormOpen
                },
                set: function (value) {
                    this.setAuthForm(value)
                }
            },
        },
        data() {
            return {
                isLogin: true,
            }
        },
        methods: {
            ...mapActions('app', ["setAuthForm"])
        },
    }
</script>

<style scoped>

</style>