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
  console.log(
    "        ,--,                           \n" +
      "      _/_/|                            \n" +
      '     (o o |           .""".          \n' +
      "      `-\\ |          /  - -\\         \n" +
      "        `\\\\        |  o   o |        \n" +
      "          `\\\\ /----\\  \\  _ /       \n" +
      "            \\        \\  \\_/ |      \n" +
      "            |         |             \n" +
      "            |  |  |  |              \n" +
      "            |  |  |  |              \n" +
      "           _|  |  |  |_             \n" +
      "          (___(_____)___)           \n"
  );
};

onMounted(() => {
  doInit();
});
</script>

<style>
.bold-text {
  font-weight: bold;
}
</style>
