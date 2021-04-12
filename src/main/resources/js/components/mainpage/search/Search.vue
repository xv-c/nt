<template>
    <v-container>
        <v-row v-if="!testKey" justify="center" align="center">
            <v-col cols="5">
                <v-text-field
                    v-model="searchValue"
                    placeholder="Найти опрос по ключу..."
                    dense outlined
                    prepend-inner-icon="search"
                    append-outer-icon="mdi-send"
                    @click:append-outer="doSearch()"
                />
            </v-col>
        </v-row>

        <test v-if="testKey"/>
    </v-container>
</template>

<script>
import {mapActions} from "vuex"
import Test from "./Test.vue";

export default {
    components: {Test},
    data() {
        return {
            searchValue: '',
        }
    },
    computed: {
        testKey() {
            return this.$route.query.testKey
        }
    },
    methods: {
        ...mapActions("app", ["showMessage"]),
        doSearch() {
            if (this.searchValue === '') {
                this.showMessage('Нет опроса с таким ключом')
                return
            }

            this.$router.replace({query: {...this.$route.query, testKey: this.searchValue}})
        },
    }
}
</script>

<style scoped>
</style>