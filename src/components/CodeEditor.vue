<template>
  <!-- 代码编辑器容器 -->
  <div
    id="code-editor"
    ref="codeEditorRef"
    style="min-height: 400px; height: 70vh"
  ></div>
</template>

<script setup lang="ts">
import * as monaco from "monaco-editor"; // 导入 Monaco Editor 库
import { defineProps, onMounted, ref, toRaw, watch, withDefaults } from "vue"; // 导入 Vue 相关 API

/**
 * 定义组件属性类型
 */
interface Props {
  value: string; // 代码内容
  language: string; // 编程语言
  handleChange: (v: string) => void; // 代码变更处理函数
}

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  language: () => "java", // 默认编程语言为 Java
  handleChange: (v: string) => {
    console.log(v); // 默认的变更处理函数
  },
});

const codeEditorRef = ref(); // 代码编辑器容器的引用
const codeEditor = ref(); // 代码编辑器实例的引用

// 监听语言变化，更新 Monaco 编辑器的语言
watch(
  () => props.language,
  () => {
    if (codeEditor.value) {
      monaco.editor.setModelLanguage(
        toRaw(codeEditor.value).getModel(), // 获取当前模型
        props.language // 设置新的语言
      );
    }
  }
);

// 初始化 Monaco 编辑器
onMounted(() => {
  if (!codeEditorRef.value) {
    return; // 如果容器不存在，直接返回
  }

  // 创建 Monaco 编辑器实例
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value, // 初始代码内容
    language: props.language, // 语言
    automaticLayout: true, // 自动调整布局
    minimap: {
      enabled: true, // 启用代码小地图
    },
    readOnly: false, // 可编辑
    theme: "vs-dark", // 使用暗色主题
  });

  // 编辑器内容变化时，触发 handleChange 函数
  codeEditor.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(codeEditor.value).getValue()); // 传递新的代码内容
  });
});
</script>

<style scoped></style>
