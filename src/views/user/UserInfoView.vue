<template>
  <div id="userInfoView">
    <!-- 🔹 用户信息展示区域 -->
    <a-descriptions-item>
      <a-avatar :size="100" shape="circle">
        <img alt="头像" :src="loginUser.userAvatar" />
      </a-avatar>
    </a-descriptions-item>

    <!-- 🔹 用户信息卡片 -->
    <a-card
      title="我的信息"
      :style="{ paddingLeft: '33px', marginTop: '20px' }"
    >
      <a-descriptions :data="data" size="large" column="1" bordered />
      <!-- 显示在线状态 -->
      <template #extra>
        <a-badge status="success" text="在线" />
      </template>
    </a-card>

    <!-- 🔹 个人信息修改模态框 -->
    <a-modal
      width="30%"
      :visible="visible"
      placement="right"
      @ok="handleOk"
      @cancel="closeModel"
      unmountOnClose
    >
      <div style="text-align: center; padding-bottom: 20px">
        <!-- 🔹 头像上传组件 -->
        <a-upload
          action="/"
          :fileList="file ? [file] : []"
          :show-file-list="false"
          @change="onChange"
          :custom-request="uploadAvatar"
        >
          <template #upload-button>
            <!-- 自定义头像上传按钮 -->
            <div
              class="arco-upload-list-picture custom-upload-avatar"
              v-if="updateForm.userAvatar"
            >
              <a-avatar :size="70" shape="circle">
                <img alt="头像" :src="userAvatarImg" />
              </a-avatar>
              <div class="arco-upload-list-picture-mask">
                <!-- 编辑图标 -->
                <IconEdit />
              </div>
            </div>
          </template>
        </a-upload>
      </div>

      <!-- 🔹 用户信息编辑表单 -->
      <a-form
        :model="loginUser"
        label-align="right"
        title="个人信息"
        style="max-width: 480px; margin: 0 auto"
      >
        <a-form-item field="用户名称" label="用户名称 :">
          <a-input v-model="updateForm.userName" placeholder="请输入用户名称" />
        </a-form-item>

        <a-form-item field="userProfile" label="个人简介 :">
          <a-textarea
            v-model="updateForm.userProfile"
            placeholder="请输入个人简介"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 🔹 按钮区域 -->
    <div>
      <!-- 首页按钮 -->
      <a-button
        shape="round"
        status="success"
        size="small"
        type="outline"
        style="margin: 10px"
      >
        <a-link @click="toIndex">首页</a-link>
      </a-button>
      <!-- 修改用户信息按钮 -->
      <a-button
        shape="round"
        status="normal"
        size="medium"
        type="outline"
        style="margin: 10px"
        @click="openModalForm"
      >
        修改用户信息
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useStore } from "vuex";
import {
  FileControllerService,
  UserControllerService,
  UserUpdateMyRequest,
} from "../../../backendAPI";
import { ref } from "vue";
import { FileItem, Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import moment from "moment";

// Router for navigation
const router = useRouter();
const file = ref();

// 🔹 获取用户信息
const store = useStore();
let loginUser = store.state.user.loginUser;

// 🔹 用户信息数据
const data = [
  { label: "用户名称：", value: loginUser.userName },
  {
    label: "个人简介：",
    value: loginUser.userProfile?.length > 0 ? loginUser.userProfile : "暂无",
  },
  {
    label: "用户角色：",
    value: loginUser.userRole === "user" ? "普通用户" : "管理员",
  },
  {
    label: "创建时间：",
    value: moment(loginUser.createTime).format("YYYY-MM-DD HH:mm:ss"),
  },
  {
    label: "修改时间：",
    value: moment(loginUser.updateTime).format("YYYY-MM-DD HH:mm:ss"),
  },
];

// 🔹 表单的显示状态
const visible = ref(false);

// 🔹 更新表单数据
const updateForm = ref<UserUpdateMyRequest>({ ...store.state.user?.loginUser });

// 🔹 用户头像
let userAvatarImg = updateForm.value.userAvatar;

// 🔹 上传头像方法
const uploadAvatar = async () => {
  const res = await FileControllerService.uploadAvatarUsingPost(
    file?.value.file
  );
  if (res.code === 0) {
    userAvatarImg = res.data;
    Message.success("上传成功，点击确认即可修改头像");
  } else {
    Message.error("上传失败！" + res.data);
  }
};

// 🔹 打开编辑弹窗
const openModalForm = () => {
  visible.value = true;
};

// 🔹 确定修改按钮逻辑
const handleOk = async () => {
  const res = await UserControllerService.updateMyUserUsingPost({
    ...updateForm.value,
    userAvatar: userAvatarImg,
  });
  if (res.code === 0) {
    Message.success("更新成功！");
    visible.value = false;
    location.reload();
  } else {
    Message.error("更新失败！", res.msg);
  }
};

// 🔹 关闭模态框
const closeModel = () => {
  visible.value = false;
};

// 🔹 返回首页
const toIndex = () => {
  router.push({ path: `/` });
};

// 🔹 获取上传的文件
const onChange = async (_: never, currentFile: FileItem) => {
  file.value = { ...currentFile };
};
</script>

<style scoped>
/* 页面容器样式 */
#userInfoView {
  margin: 0 auto;
  padding: 20px;
  max-width: 820px;
  border-radius: 10px;
  background-color: #f8f9fa;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 头像上传区域样式 */
.arco-upload-list-picture.custom-upload-avatar {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.arco-upload-list-picture-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.arco-upload-list-picture-mask svg {
  fill: white;
}
</style>
