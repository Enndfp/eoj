<template>
  <!-- Bytemd 查看器组件，展示 Markdown 内容 -->
  <div class="viewer-container">
    <Viewer :value="value" :plugins="plugins" readonly />
  </div>
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

<style scoped>
.viewer-container {
  max-height: 60vh;
  height: 100%;
  overflow-y: auto;
  background-color: #ffffff;
  padding: 16px;
  border: 1px solid #e5e6eb;
  border-radius: 4px;
}

/* 覆盖 Bytemd 的默认样式 */
:deep(.bytemd) {
  background-color: #ffffff;
  color: #333333;
}

/* Markdown 内容区域样式 */
:deep(.markdown-body) {
  background-color: #ffffff;
}

/* 标题样式 */
:deep(.markdown-body h1),
:deep(.markdown-body h2),
:deep(.markdown-body h3),
:deep(.markdown-body h4),
:deep(.markdown-body h5),
:deep(.markdown-body h6) {
  color: #1f2328;
  font-weight: 600;
}

/* 代码块样式 */
:deep(.markdown-body pre) {
  background-color: #f8faf9; /* 极浅绿灰 */
  border: 1px solid #e5e7eb; /* 更柔和的边框 */
  border-radius: 4px;
  padding: 18px;
  margin: 12px 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03); /* 增强阴影 */
  font-size: 14px;
}

/* 内联代码样式 */
:deep(.markdown-body code) {
  background-color: #f8faf9;
  color: #007acc;
  padding: 3px 8px;
  border-radius: 4px;
  font-family: "Consolas", "Courier New", monospace;
  font-size: 14px;
}

/* 语法高亮优化 */
:deep(.markdown-body pre code) {
  /* 关键字 */

  .keyword {
    color: #007acc;
  }

  /* 字符串 */

  .string {
    color: #098658;
  }

  /* 注释 */

  .comment {
    color: #4a5568;
  }

  /* 更深的灰色 */
  /* 变量/类型 */

  .variable,
  .type {
    color: #267f99;
  }

  /* 数字 */

  .number {
    color: #b8860b;
  }
}

/* 引用块样式 */
:deep(.markdown-body blockquote) {
  background-color: #f7f9fb; /* 选择一个背景色 */
  border-left: 4px solid #bfbfbf; /* 选择一个边框色 */
  color: #4a5568; /* 选择一个文字色 */
  padding: 8px 16px;
  border-radius: 0 4px 4px 0; /* 可选：保持圆角风格 */
}

/* 链接样式 */
:deep(.markdown-body a) {
  color: #007acc;
  text-decoration: none;
}

:deep(.markdown-body a:hover) {
  color: #005a9e;
  text-decoration: underline;
}

/* 表格样式 */
:deep(.markdown-body table) {
  border-collapse: collapse;
  background-color: #ffffff;
}

:deep(.markdown-body th),
:deep(.markdown-body td) {
  border: 1px solid #d1d5db;
  padding: 10px 16px;
}

:deep(.markdown-body th) {
  background-color: #f0f0f0;
  color: #333333;
}

/* 分隔线样式 */
:deep(.markdown-body hr) {
  border-color: #d1d5db;
}

/* 美化滚动条 */
.viewer-container::-webkit-scrollbar {
  width: 8px;
}

.viewer-container::-webkit-scrollbar-track {
  background: #f8faf9; /* 与代码块一致 */
  border-radius: 4px;
}

.viewer-container::-webkit-scrollbar-thumb {
  background: #a0aec0;
  border-radius: 4px;
  border: 2px solid #f8faf9;
}

.viewer-container::-webkit-scrollbar-thumb:hover {
  background: #718096;
}
</style>
