<template>
  <div id="app">
    <BasicLayOut />
  </div>
</template>

<style></style>
<script setup lang="ts">
import BasicLayOut from "@/layouts/BasicLayout.vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { onMounted } from "vue";

/**
 * 全局初始化函数，有全局单次调用的代码
 */
const doInit = () => {
  console.log("hello 欢迎来到我的项目");
};

onMounted(() => {
  doInit();
});

const router = useRouter();
const store = useStore();
router.beforeEach((to, from, next) => {
  // 仅管理员可见，判断当前用户是否有权限
  if (to.meta.access === "canAdmin") {
    if (store.state.loginUser?.role !== "admin") {
      next("/noAuth");
      return;
    }
  }
  next();
});
</script>
