<template>
    <v-dialog width="260" persistent v-model="maskModel">
        <v-card class="pa-4">
            <v-card-title>
                <v-row justify="center">
                    <v-progress-circular indeterminate color="blue"/>
                    <span>&nbsp;Загрузка данных</span>
                </v-row>
            </v-card-title>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        props:['maskModel']
    }
</script>

<style scoped>
</style>