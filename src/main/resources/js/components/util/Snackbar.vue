<template>
  <v-snackbar :timeout="-1" top v-model="snackModel" tile>
    {{ snackbarMessage }}
    <template v-slot:action="{ attrs }">
      <v-btn icon
             v-bind="attrs"
             @click="snackModel = false">
        <v-icon>close</v-icon>
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script>
import {mapState} from "vuex";

export default {
  data() {
    return {
      snackModel: false,
    }
  },
  computed: {
    ...mapState("app", ["snackbarMessage", "snackbarTime"])
  },
  watch: {
    snackbarTime: function (value) {
      this.snackModel = true;
    },
  },
}
</script>

<style scoped>

</style>