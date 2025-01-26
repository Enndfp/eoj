<template>
  <div class="footer">
    <a :href="blogUrl" target="_blank" class="footer-blog">Enndfp's Blog</a>
    <div class="footer-links">
      <a :href="githubUrl" target="_blank" class="footer-link">
        <GithubOutlined /> &nbsp;Enndfp
      </a>
      <span class="footer-divider">|</span>
      <a
        :href="beianUrl"
        target="_blank"
        rel="noreferrer"
        class="footer-link"
        >{{ beian }}</a
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { GithubOutlined } from "@ant-design/icons-vue";
import { BEIAN, BEIAN_URL, BLOG_URL, GITHUB_URL } from "@/constant";

const beian = BEIAN;
const beianUrl = BEIAN_URL;
const blogUrl = BLOG_URL;
const githubUrl = GITHUB_URL;
</script>

<style scoped>
.footer {
  padding: 14px;
  text-align: center;
}

.footer-link {
  text-decoration: none;
  color: inherit;
  font-size: 16px;
  margin: 0 5px;
}

.footer-link:hover {
  color: #1890ff;
  text-decoration: underline;
}

.footer-divider {
  margin: 0 5px;
  color: #bbb;
}

.footer-blog {
  font-weight: bold;
  color: #1890ff;
  text-decoration: none;
  font-size: 16px;
}

.footer-blog:hover {
  text-decoration: underline;
}

.footer-links {
  margin-top: 5px;
}
</style>
