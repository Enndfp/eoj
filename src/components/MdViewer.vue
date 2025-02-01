<template>
  <!-- Bytemd 查看器组件，展示 Markdown 内容 -->
  <Viewer :value="value" :plugins="plugins" readonly />
</template>

<script setup lang="ts">
import gfm from "@bytemd/plugin-gfm"; // 引入 GFM 插件
import highlight from "@bytemd/plugin-highlight"; // 引入代码高亮插件
import { Viewer } from "@bytemd/vue-next"; // 引入 Bytemd 查看器
import { defineProps, withDefaults } from "vue"; // 引入 Vue 属性相关 API

/**
 * 定义组件的属性类型，接收 Markdown 内容
 */
interface Props {
  value: string;
}

// 默认值配置
const props = withDefaults(defineProps<Props>(), {
  value: () => "", // 默认内容为空
});

// 插件配置
const plugins = [
  gfm(), // 启用 GFM 插件
  highlight(), // 启用代码高亮插件
  // 可以在此添加更多插件
];
</script>

<style></style>
