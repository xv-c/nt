export default {
    namespaced: true,
    state: {
        profile: user,
        isAuthFormOpen: false
    },
    mutations: {
        SET_AUTH_FORM_STATE(state, value){
            state.isAuthFormOpen = value;
        },
        CHANGE_AUTH_FORM_STATE(state){
            state.isAuthFormOpen = !state.isAuthFormOpen;
        }
    },
    actions: {
        closeAuthForm({commit}){
            commit('SET_AUTH_FORM_STATE', false)
        },
        openAuthForm({commit}){
            commit('SET_AUTH_FORM_STATE', true)
        },
        setAuthForm({commit}, value){
            commit('CHANGE_AUTH_FORM_STATE', value)
        }
    }
};