<template>
    <v-snackbar :timeout="0" top v-model="snackModel" text tile>
        {{ snackbarMessage }}
        <template v-slot:action="{ attrs }">
            <v-btn icon
                   v-bind="attrs"
                   @click="hideSnackbar">
                <v-icon>close</v-icon>
            </v-btn>
        </template>
    </v-snackbar>
</template>

<script>
    export default {
        props: ['snackbarMessage', 'snackTime'],
        created() {
            this.snackModel = this.model;
        },
        data() {
            return {
                snackModel: false,
            }
        },
        methods: {
            hideSnackbar() {
                this.snackModel = false
            }
        },
        watch: {
            snackTime: function (value) {
                this.snackModel = true;
            },
        },
    }
</script>

<style scoped>

</style>