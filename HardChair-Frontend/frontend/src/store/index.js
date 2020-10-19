import Vue from 'vue'
import Vuex from 'vuex'
Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        token: localStorage.getItem('token') || null,
        userType: localStorage.getItem('userType') || null
    },
    mutations: {
        login(state, data) {
            localStorage.setItem('token', data.token)
            localStorage.setItem('userType', data.userType)
            state.userType = data.userType;
            state.token = data.token
        },
        logout(state) {
            // 移除token
            localStorage.removeItem('token')
            localStorage.removeItem('userType')
            state.userType = null
            state.token = null
        }
    },
    actions: {}
})