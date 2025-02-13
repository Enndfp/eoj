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
  const updateTime = () => {
    const timeString = new Date().toLocaleTimeString();
    console.clear(); // 清空控制台，避免信息堆积
    console.log(
      "%c🎉 欢迎来到 EOJ 算法竞技场！ 🎉 %c\n" +
        "✦•------------------------✦\n" +
        "🌟 准备好挑战你的极限了吗？\n" +
        "🚀 起航吧！\n" +
        "✦•------------------------✦\n" +
        "%c[ ⏳ 每日挑战倒计时: " +
        timeString +
        " ]",
      "background: linear-gradient(90deg, #FF6347, #FF1493); color: white; padding:4px 8px; border-radius:3px;",
      "",
      "color: #2F4F4F; font-weight:bold; font-size: 16px;"
    );
  };

  // 每秒更新一次时间
  setInterval(updateTime, 1000);
};

onMounted(() => {
  doInit();
});
</script>

<style>
.bold-text {
  font-weight: bold;
}

.arco-space-item {
  margin-bottom: 0 !important;
}

.arco-card-header-title,
.arco-descriptions-title,
.arco-card-size-medium .arco-card-body {
  font-weight: 700 !important;
}
</style>
