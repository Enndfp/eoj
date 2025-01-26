<template>
  <div id="userLoginView">
    <h1 style="margin: 32px 0">用户登录</h1>
    <a-form
      style="max-width: 320px; margin: 0 auto"
      label-align="left"
      auto-label-width
      :model="form"
      @keyup.enter="handleSubmit"
    >
      <a-form-item
        required
        field="userAccount"
        label="账号"
        tooltip="账号不少于 4 位"
      >
        <a-input v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item
        required
        field="userPassword"
        tooltip="密码不少于 8 位"
        label="密码"
      >
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
        />
      </a-form-item>
    </a-form>
    <a-space wrap>
      <a-button
        size="large"
        shape="round"
        type="secondary"
        status="success"
        @click="toIndex"
      >
        首 页
      </a-button>
      <a-button
        style="width: 120px; margin: 16px"
        size="large"
        shape="round"
        type="primary"
        html-type="submit"
        @click="handleSubmit"
      >
        登 录
      </a-button>
      <a-button
        size="large"
        shape="round"
        type="outline"
        status="success"
        @click="toRegister"
      >
        注 册
      </a-button>
    </a-space>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { UserControllerService, UserLoginRequest } from "../../../backendAPI";

/**
 * 表单信息
 */
const form = reactive({
  userAccount: "",
  userPassword: "",
} as UserLoginRequest);

const router = useRouter();
const store = useStore();

const handleSubmit = async () => {
  const res = await UserControllerService.userLoginUsingPost(form);
  // 登录成功，跳转到主页
  if (res.code === 0) {
    await store.dispatch("user/getLoginUser");
    router.push({
      path: "/",
      replace: true,
    });
    message.success("登录成功！");
  } else {
    message.error("登陆失败，" + res.message);
  }
};
const toIndex = () => {
  router.push({
    path: `/`,
  });
};

const toRegister = () => {
  router.push({
    path: `/user/register`,
  });
};
</script>
