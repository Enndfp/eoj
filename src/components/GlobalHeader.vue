<template>
  <a-row id="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <!-- 菜单栏 -->
      <a-menu
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <!-- 左侧logo和标题 -->
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar" @click="toHome">
            <img class="logo" :src="systemLogo" />
            <div class="title">Online Judge</div>
          </div>
        </a-menu-item>

        <!-- 动态渲染的菜单项，根据权限和路由显示 -->
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>

    <!-- 用户信息下拉菜单 -->
    <a-space size="large" :style="{ marginRight: '22px' }">
      <a-dropdown trigger="hover">
        <!-- 用户已登录 -->
        <template
          v-if="loginUser && loginUser.userRole !== AccessEnum.NOT_LOGIN"
        >
          <template v-if="loginUser.userAvatar">
            <!-- 用户头像 -->
            <a-avatar
              :size="48"
              shape="circle"
              :image-url="loginUser.userAvatar"
            />
          </template>
          <template v-else>
            <!-- 默认头像 -->
            <a-avatar shape="circle">
              <IconUser />
            </a-avatar>
          </template>
        </template>

        <!-- 用户未登录 -->
        <template v-else>
          <a-avatar shape="circle" :style="{ backgroundColor: '#3370ff' }">
            <IconUser />
          </a-avatar>
        </template>

        <!-- 下拉菜单内容 -->
        <template #content>
          <!-- 已登录用户 -->
          <template v-if="loginUser.userRole !== AccessEnum.NOT_LOGIN">
            <a-doption>
              <template #icon>
                <icon-idcard />
              </template>
              <template #default>
                <a-anchor-link href="/user/info">个人信息</a-anchor-link>
              </template>
            </a-doption>
            <a-doption>
              <template #icon>
                <icon-poweroff />
              </template>
              <template #default>
                <a-anchor-link @click="logout">退出登录</a-anchor-link>
              </template>
            </a-doption>
          </template>

          <!-- 未登录用户 -->
          <template v-else>
            <a-doption>
              <template #icon>
                <icon-user />
              </template>
              <template #default>
                <a-anchor-link href="/user/login">登录</a-anchor-link>
              </template>
            </a-doption>
            <a-doption>
              <template #icon>
                <icon-user-add />
              </template>
              <template #default>
                <a-anchor-link href="/user/register">注册</a-anchor-link>
              </template>
            </a-doption>
          </template>
        </template>
      </a-dropdown>
    </a-space>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "../router/routes"; // 导入路由配置
import { useRouter } from "vue-router"; // 导入vue-router钩子
import { computed, ref } from "vue"; // 导入Vue响应式API
import { useStore } from "vuex"; // 导入Vuex store
import checkAccess from "@/access/checkAccess"; // 导入权限检查函数
import ACCESS_ENUM from "@/access/accessEnum"; // 导入权限枚举
import { SYSTEM_LOGO } from "@/constant"; // 导入常量
import { LoginUserVO, UserControllerService } from "../../backendAPI"; // 导入用户信息类型和API接口
import AccessEnum from "@/access/accessEnum"; // 导入权限枚举

// 系统logo常量
const systemLogo = SYSTEM_LOGO;
// 获取路由实例
const router = useRouter();
// 获取store实例
const store = useStore();

// 默认选中的菜单项
const selectedKeys = ref(["/"]);

// 获取登录用户信息
const loginUser: LoginUserVO = computed(
  () => store.state.user?.loginUser
) as LoginUserVO;

// 用户注销函数
const logout = () => {
  UserControllerService.userLogoutUsingPost(); // 调用后端注销接口
  location.reload(); // 刷新页面
};

// 获取可以展示的路由（根据权限和meta配置）
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    if (item.meta?.hideInMenu) {
      return false; // 路由被设置为隐藏则不显示
    }
    if (!checkAccess(store.state.user.loginUser, item.meta?.access as string)) {
      return false; // 如果没有权限，则不显示
    }
    return true;
  });
});

// 路由跳转后更新菜单选中的项
router.afterEach((to) => {
  selectedKeys.value = [to.path]; // 更新选中的菜单项
});

// 菜单项点击事件
const doMenuClick = (key: string) => {
  router.push({ path: key });
};

// 点击标题栏跳转到首页
const toHome = () => {
  router.push("/");
};
</script>

<style scoped>
/* 标题栏样式 */
.title-bar {
  display: flex;
  align-items: center;
  cursor: default; /* 禁止标题栏点击时产生手型光标 */
}

/* 系统标题样式 */
.title {
  color: rgb(51, 51, 51);
  font-size: 22px;
  font-weight: bold;
  margin-left: 10px;
}

/* 系统logo样式 */
.logo {
  line-height: 48px;
  height: 48px;
}
</style>
