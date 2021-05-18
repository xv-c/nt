<template>
  <div>
    <v-card-text>
      <v-text-field
          v-model="username"
          placeholder="Электронная почта"
          clearable
          dense solo single-line
          prepend-inner-icon="mail"
      />
      <v-text-field
          v-model="password"
          placeholder="Пароль"
          dense solo single-line
          hide-details
          prepend-inner-icon="lock"
          :append-icon="show ? 'mdi-eye' : 'mdi-eye-off'"
          :type="show ? 'text' : 'password'"
          @click:append="show = !show"
      />
    </v-card-text>

    <v-card-actions>
      <v-spacer/>
      <v-btn :ripple="false" @click="login" outlined color="blue">Войти</v-btn>
      <v-spacer/>
    </v-card-actions>
  </div>
</template>

<script>
import {mapActions} from "vuex"
import api from "../../use/api"
import endpoints from "../../use/endpoints";

export default {
  components: {},
  data() {
    return {
      username: '',
      password: '',
      show: false,
    }
  },
  methods: {
    ...mapActions('app', ["showMessage"]),
    login() {
      let formData = new FormData();
      formData.append("username", this.username)
      formData.append("password", this.password)

      api.post(endpoints.login, formData)
          .then(data => {
            if (!data.success)
              this.showMessage(data.message)
            else
              this.$router.go()
          })
    }
  }
}
</script>

<style scoped>

</style>