// GFM 插件 - 支持 GitHub 风格的 Markdown
import gfm from "@bytemd/plugin-gfm";
// 数学公式插件
import math from "@bytemd/plugin-math";
import "katex/dist/katex.css"; // 引入 KaTeX 样式
// 代码高亮插件
import highlight from "@bytemd/plugin-highlight";
import "highlight.js/styles/default.css"; // 引入代码高亮样式
// 图表插件（Mermaid）
import mermaid from "@bytemd/plugin-mermaid";
// 图片插件（支持缩放）
import mediumZoom from "@bytemd/plugin-medium-zoom";
// 支持中断的插件（允许换行）
import breaks from "@bytemd/plugin-breaks";
// 解析前言并格式化插件
import frontmatter from "@bytemd/plugin-frontmatter";
// 表情插件（支持 GitHub 风格的表情符号）
import gemoji from "@bytemd/plugin-gemoji";
// 主题插件（允许切换主题）
import theme from "bytemd-plugin-theme";
// 代码高亮插件（加强版）
import highLightPlugin from "bytemd-plugin-highlight";
// 对齐插件（支持文本对齐）
import alignPlugin from "bytemd-plugin-align";
// 图片缩放插件
import imagePlugin from "bytemd-plugin-image";
// 代码复制插件
import copyCode from "bytemd-plugin-copy-code";
import "bytemd-plugin-copy-code/dist/style/index.css"; // 引入复制代码样式

const plugins = [
  // 启用 GFM 插件
  gfm(),

  // 启用数学公式插件
  math(),

  // 启用换行符插件
  breaks(),

  // 启用前言解析插件
  frontmatter(),

  // 启用表情插件
  gemoji(),

  // 启用对齐插件
  alignPlugin(),

  // 启用图片缩放插件
  mediumZoom(),

  // 启用 Mermaid 插件
  mermaid(),

  // 启用代码高亮插件
  highlight(),

  // 启用主题插件
  theme(),

  // 启用增强版代码高亮插件
  highLightPlugin(),

  // 启用图片上传插件
  imagePlugin(),

  // 启用代码复制插件
  copyCode({
    copySuccess: (text) => {
      console.log("复制成功");
    },
    copyError: (err) => {
      console.error("复制失败", err);
    },
    copyRight: "", // 版权信息
  }),
];

export default plugins;
