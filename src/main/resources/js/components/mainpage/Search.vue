<template>
  <v-container fluid>
    <v-row justify="center" style="margin-top: 10%">
      <v-col cols="5">
        <v-text-field v-model="searchValue"
                      placeholder="Найти опрос по ключу..."
                      dense outlined
                      prepend-inner-icon="search"
                      append-outer-icon="mdi-send"
                      @click:append-outer="doSearch()"/>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import {mapActions} from "vuex"
import axios from 'axios'

export default {
  data() {
    return {
      searchValue: ''
    }
  },
  methods: {
    ...mapActions("app", ["showMessage"]),
    doSearch(){
      if(this.searchValue===''){
        this.showMessage('Нет опроса с таким ключом')
        return
      }

      this.$router.push('/test/'+this.searchValue)
    }
  }
}
</script>

<style scoped>

</style>