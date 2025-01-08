import { StoreOptions } from "vuex";

export default {
  namespaced: true,
  state: {
    loginUser: { userName: "未登录", role: "notLogin" },
  },
  getters: {},
  actions: {
    getLoginUser({ commit, state }, payload) {
      commit("updateUser", payload);
    },
  },
  mutations: {
    updateUser(state, payload) {
      state.loginUser = payload;
    },
  },
} as StoreOptions<any>;
