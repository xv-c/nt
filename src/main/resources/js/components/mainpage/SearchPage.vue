<template>
  <v-container fluid>
    <v-row align="center" justify="center" style="height: 500px">
      <v-col cols="5">
        <v-text-field v-model="searchValue"
                      placeholder="Найти тест по ключу..."
                      dense outlined
                      prepend-inner-icon="search"
                      append-outer-icon="mdi-send"/>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      searchValue: ''
    }
  }
}
</script>

<style scoped>

</style>