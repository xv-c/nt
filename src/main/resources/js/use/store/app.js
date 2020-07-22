export default {
    namespaced: true,
    state: {
        profile: user,
        isAuthFormOpen: false,
        snackbarMessage: '',
        snackbarTime: 0,
    },
    mutations: {
        SET_AUTH_FORM_STATE(state, value) {
            state.isAuthFormOpen = value;
        },
        CHANGE_AUTH_FORM_STATE(state) {
            state.isAuthFormOpen = !state.isAuthFormOpen;
        },
        CHANGE_SNACKBAR_MESSAGE(state, value) {
            state.snackbarMessage = value;
            state.snackbarTime++
        }
    },
    actions: {
        closeAuthForm({commit}) {
            commit('SET_AUTH_FORM_STATE', false)
        },
        openAuthForm({commit}) {
            commit('SET_AUTH_FORM_STATE', true)
        },
        setAuthForm({commit}, value) {
            commit('CHANGE_AUTH_FORM_STATE', value)
        },
        showMessage({commit}, value) {
            commit('CHANGE_SNACKBAR_MESSAGE', value)
        }
    }
};