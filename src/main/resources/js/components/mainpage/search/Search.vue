<template>
  <v-col v-if="lastTests">
      <v-row v-if="!testKey"
             justify="center"
             class="pa-0 ma-0">
        <v-sheet rounded color="#ADD8E6" width="874">
          <v-card class="ma-4">
            <v-card-subtitle>
              <v-text-field
                  v-model="searchValue"
                  placeholder="Найти опрос по ключу или названию..."
                  dense outlined hide-details
                  prepend-inner-icon="search"
                  append-outer-icon="mdi-send"
                  @click:append-outer="doSearch()"
              />
            </v-card-subtitle>
          </v-card>
          <v-card class="ma-4">
            <v-card-title>
              Последние публичные опросы
            </v-card-title>

            <div v-for="test in lastTests">
              <div>
                <v-divider/>
                <v-card-title>
                  {{ test.name }}
                </v-card-title>
                <v-card-subtitle>
                  {{ test.description }}
                </v-card-subtitle>
                <v-card-subtitle class="pt-0">
                  <v-btn @click="openTest(test.key)"
                      class="pa-0"
                      color="blue"
                      text>
                    пройти
                  </v-btn>
                </v-card-subtitle>
              </div>
            </div>
          </v-card>
        </v-sheet>
      </v-row>

      <test v-if="testKey"/>
  </v-col>
</template>

<script>
import {mapActions} from "vuex"
import Test from "./Test.vue";
import api from "../../../use/api";
import endpoints from "../../../use/endpoints";

export default {
  components: {Test},
  data() {
    return {
      searchValue: '',
      lastTests: undefined
    }
  },
  computed: {
    testKey() {
      return this.$route.query.testKey
    }
  },
  created() {
    api.get(endpoints.tests + 'public')
        .then(resp => {
          this.lastTests = resp.data.tests
        })
  },
  methods: {
    ...mapActions("app", ["showMessage"]),
    openTest(key){
      this.$router.replace({query: {...this.$route.query, testKey: key}})
    },
    doSearch() {
      if (this.searchValue === '') {
        this.showMessage('Нет опроса с таким ключом')
        return
      }
      this.openTest(this.searchValue)
    },
  }
}
</script>

<style scoped>
</style>