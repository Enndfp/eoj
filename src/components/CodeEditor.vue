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
  theme: string; // 主题
}

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  language: () => "java", // 默认编程语言为 Java
  theme: () => "vs-light", // 默认主题
  handleChange: (v: string) => {
    // console.log(v); // 默认的变更处理函数
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

// 监听主题变化，更新 Monaco 编辑器的主题
watch(
  () => props.theme,
  (newTheme) => {
    if (codeEditor.value) {
      monaco.editor.setTheme(newTheme); // 设置新的主题
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
    // 基本设置
    value: props.value, // 初始代码内容
    language: props.language, // 编程语言
    theme: props.theme, // 编辑器主题
    readOnly: false, // 可编辑模式
    lineNumbers: "on", // 显示行号
    lineNumbersMinChars: 1, // 行号最小字符数
    scrollBeyondLastLine: false, // 禁止滚动超过最后一行
    colorDecorators: true, // 颜色装饰器（语法高亮）

    // 自动布局和折叠
    automaticLayout: true, // 自动布局
    folding: true, // 启用代码折叠功能
    foldingHighlight: true, // 折叠区域高亮
    foldingStrategy: "indentation", // 使用缩进折叠策略
    showFoldingControls: "always", // 始终显示折叠控件

    // 小地图
    minimap: {
      enabled: true, // 启用小地图
      size: "fill", // 小地图填充
      maxColumn: 50, // 最大列数
    },

    // 剪切板配置（如果需要的话）
    emptySelectionClipboard: false, // 空选择时不复制内容
    selectionClipboard: false, // 选择时不复制内容

    // 代码提示与自动补全
    quickSuggestions: true, // 启用智能建议
    suggestOnTriggerCharacters: true, // 在触发字符时显示建议
    acceptSuggestionOnEnter: "on", // 按回车时接受建议

    // 代码镜头与辅助功能
    codeLens: false, // 禁用代码镜头（OJ平台不需要）
    accessibilitySupport: "off", // 禁用辅助功能（除非特别需要）

    // 性能优化
    disableLayerHinting: true, // 禁用层提示以提高性能
  });

  // 编辑器内容变化时，触发 handleChange 函数
  codeEditor.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(codeEditor.value).getValue()); // 传递新的代码内容
  });
});
</script>

<style scoped></style>
