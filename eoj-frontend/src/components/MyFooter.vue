<template>
  <div class="footer">
    <!-- 博客链接 -->
    <a :href="blogUrl" target="_blank" class="footer-blog">Enndfp's Blog</a>
    <div class="footer-links">
      <!-- GitHub 链接 -->
      <a :href="githubUrl" target="_blank" class="footer-link">
        <GithubOutlined /> &nbsp;Enndfp
      </a>
      <!-- 分隔符 -->
      <span class="footer-divider">|</span>
      <!-- 网站备案链接 -->
      <a :href="beianUrl" target="_blank" rel="noreferrer" class="footer-link">
        {{ beian }}
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
import { GithubOutlined } from "@ant-design/icons-vue"; // 引入 GitHub 图标
import { BEIAN, BEIAN_URL, BLOG_URL, GITHUB_URL } from "@/constant"; // 引入常量

// 定义常量
const beian = BEIAN;
const beianUrl = BEIAN_URL;
const blogUrl = BLOG_URL;
const githubUrl = GITHUB_URL;
</script>

<style scoped>
.footer {
  padding: 14px;
  text-align: center; /* 居中对齐 */
}

.footer-link {
  text-decoration: none; /* 移除默认下划线 */
  color: inherit; /* 继承父元素的颜色 */
  font-size: 16px;
  margin: 0 5px; /* 左右间距 */
}

.footer-link:hover {
  color: #1890ff; /* 鼠标悬浮时改变颜色 */
  text-decoration: underline; /* 鼠标悬浮时添加下划线 */
}

.footer-divider {
  margin: 0 5px;
  color: #bbb; /* 灰色分隔符 */
}

.footer-blog {
  font-weight: bold; /* 加粗 */
  color: #1890ff; /* 蓝色文字 */
  text-decoration: none; /* 移除默认下划线 */
  font-size: 16px;
}

.footer-blog:hover {
  text-decoration: underline; /* 鼠标悬浮时添加下划线 */
}

.footer-links {
  margin-top: 5px; /* 顶部间距 */
}
</style>
