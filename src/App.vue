<template>
  <div id="app">
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else>
      <BasicLayout />
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useRoute } from "vue-router";
import BasicLayout from "@/layouts/BasicLayout.vue";

const route = useRoute();

/**
 * 全局初始化函数，有全局单次调用的代码
 */
const doInit = () => {
  console.log("Hello 欢迎来到我的项目!");
};

onMounted(() => {
  doInit();
});
</script>

<style></style>
