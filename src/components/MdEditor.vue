<template>
  <!-- Bytemd 编辑器组件 -->
  <Editor
    :value="value"
    :mode="mode"
    :plugins="plugins"
    :locale="locale"
    @change="handleChange"
  />
</template>

<script setup lang="ts">
import gfm from "@bytemd/plugin-gfm"; // 引入 GFM 插件
import { Editor } from "@bytemd/vue-next"; // 引入 Bytemd 编辑器
import { withDefaults, defineProps } from "vue"; // 引入 Vue 的属性定义 API
import locale from "bytemd/locales/zh_Hans.json"; // 引入中文语言包
import gemoji from "@bytemd/plugin-gemoji"; // 引入 Gemoji 插件
import math from "@bytemd/plugin-math"; // 引入数学公式插件
import mathLocale from "@bytemd/plugin-math/locales/zh_Hans.json"; // 引入数学公式插件的中文语言包
import gfmLocale from "@bytemd/plugin-gfm/locales/zh_Hans.json"; // 引入 GFM 插件的中文语言包
import highlight from "@bytemd/plugin-highlight"; // 引入代码高亮插件

/**
 * 定义组件属性类型，外部传入数据
 */
interface Props {
  value: string; // 编辑器的内容
  mode?: string; // 编辑器模式（默认split）
  handleChange: (v: string) => void; // 内容变更的回调函数
}

// 定义插件列表
const plugins = [
  gfm({
    locale: gfmLocale, // 设置 GFM 插件的语言包
  }),
  highlight(), // 启用代码高亮插件
  math({
    locale: mathLocale, // 设置数学公式插件的语言包
  }),
  gemoji(), // 启用 Gemoji 插件
];

/**
 * 属性默认值
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "", // 默认内容为空
  mode: () => "split", // 默认模式为split
  handleChange: (v: string) => {
    console.log(v); // 默认的内容变化处理函数
    return ""; // 默认返回空字符串
  },
});
</script>

<style>
/* 去掉 GitHub 图标 */
.bytemd-toolbar-icon.bytemd-tippy.bytemd-tippy-right:last-child {
  display: none;
}
</style>
