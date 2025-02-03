<template>
  <!-- Bytemd 查看器组件，展示 Markdown 内容 -->
  <Viewer :value="value" :plugins="plugins" readonly />
</template>

<script setup lang="ts">
import { Viewer } from "@bytemd/vue-next"; // 引入 Bytemd 查看器
import { defineProps, withDefaults } from "vue";
import plugins from "@/plugins/Bytemd";

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
</script>

<style></style>
