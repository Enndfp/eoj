<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <img class="logo" :src="systemLogo" />
        </a-space>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <MyFooter />
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { SYSTEM_LOGO } from "@/constant";
import MyFooter from "@/components/MyFooter.vue";

const systemLogo = SYSTEM_LOGO;
</script>

<style scoped>
#userLayout {
  text-align: center;
  background: url(@/assets/background.png) 0% 0% / 100% 100%;
}

#userLayout .logo {
  width: 64px;
  height: 64px;
}

#userLayout .header {
  margin-top: 16px;
}

#userLayout .content {
  margin-bottom: 16px;
  padding: 20px;
}

#userLayout .footer {
  padding: 6px;
  position: sticky;
  bottom: 0;
  right: 0;
  left: 0;
  text-align: center;
}
</style>
