<template>
  <div id="userManageView">
    <!-- 🔹 搜索表单 -->
    <a-form :model="searchParams" layout="inline" class="search-form bold-text">
      <a-form-item
        field="userName"
        label="用户名称："
        tooltip="请输入要搜索的用户名称"
      >
        <a-input v-model="searchParams.userName" placeholder="请输入用户名称" />
      </a-form-item>
      <a-form-item
        field="userProfile"
        label="用户简介："
        tooltip="请输入要搜索的用户简介"
      >
        <a-input
          v-model="searchParams.userProfile"
          placeholder="请输入用户简介"
        />
      </a-form-item>
      <a-form-item
        field="userRole"
        label="用户角色："
        tooltip="请选择要搜索的用户角色"
      >
        <a-select v-model="searchParams.userRole" placeholder="选择用户角色">
          <!-- 普通用户和管理员的颜色标签 -->
          <a-option value="user">
            <a-tag color="arcoblue" class="bold-text">普通用户</a-tag>
          </a-option>
          <a-option value="admin">
            <a-tag color="green" class="bold-text">管理员</a-tag>
          </a-option>
        </a-select>
      </a-form-item>
      <a-form-item class="button-group">
        <a-button
          type="primary"
          shape="round"
          status="normal"
          @click="doSubmit"
          class="bold-text"
        >
          搜 索
        </a-button>
      </a-form-item>
      <a-form-item class="button-group">
        <a-button
          type="outline"
          shape="round"
          @click="resetFilters"
          class="bold-text"
        >
          重 置
        </a-button>
      </a-form-item>
    </a-form>

    <a-divider dashed />

    <!-- 🔹 用户记录表格 -->
    <a-table
      column-resizable
      :ref="tableRef"
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
        showJumper: true,
        showPageSize: true,
      }"
      @page-change="onPageChange"
      @pageSizeChange="onPageSizeChange"
    >
      <!-- 用户头像 -->
      <template #userAvatar="{ record }">
        <a-avatar :size="60" shape="circle">
          <img alt="userAvatar" :src="record.userAvatar" />
        </a-avatar>
      </template>

      <!-- 用户角色 -->
      <template #userRole="{ record }">
        <!-- 用标签显示用户角色 -->
        <a-tag
          v-if="record.userRole === ACCESS_ENUM.USER"
          color="arcoblue"
          class="bold-text"
        >
          普通用户
        </a-tag>
        <a-tag
          v-if="record.userRole === ACCESS_ENUM.ADMIN"
          color="green"
          class="bold-text"
        >
          管理员
        </a-tag>
      </template>

      <!-- 加粗时间信息 -->
      <template #createTime="{ record }">
        <span class="formatted-time bold-text">
          {{ moment(record.createTime).format("YYYY-MM-DD HH:mm") }}
        </span>
      </template>

      <template #updateTime="{ record }">
        <span class="formatted-time bold-text">
          {{ moment(record.updateTime).format("YYYY-MM-DD HH:mm") }}
        </span>
      </template>

      <!-- 操作 -->
      <template #optional="{ record }">
        <a-space>
          <a-button
            shape="round"
            type="outline"
            @click="openModalForm(record.id)"
            class="bold-text"
          >
            修改
          </a-button>
          <a-popconfirm
            content="确定要删除此用户吗?"
            type="error"
            okText="是"
            cancelText="否"
            @cancel="
              () => {
                console.log('已取消');
              }
            "
            @ok="doDelete(record)"
          >
            <a-button
              shape="round"
              type="outline"
              status="danger"
              class="bold-text"
            >
              删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </template>
    </a-table>

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
        <a-upload
          action="/"
          :fileList="file ? [file] : []"
          :show-file-list="false"
          @change="onChange"
          :custom-request="uploadAvatar"
        >
          <template #upload-button>
            <a-avatar :size="70" shape="circle">
              <img alt="头像" :src="userInfo?.userAvatar" />
            </a-avatar>
          </template>
        </a-upload>
      </div>

      <a-form label-align="right" style="max-width: 480px; margin: 0 auto">
        <a-form-item field="用户名称" label="用户名称 :" class="bold-text">
          <a-input v-model="userInfo.userName" placeholder="请输入用户名称" />
        </a-form-item>
        <a-form-item field="用户账号" label="用户账号 :" class="bold-text">
          <a-input
            v-model="userInfo.userAccount"
            placeholder="请输入用户账号"
          />
        </a-form-item>
        <a-form-item field="用户角色" label="用户角色 :" class="bold-text">
          <a-select v-model="userInfo.userRole" placeholder="请选择用户角色">
            <a-option value="user">
              <a-tag color="arcoblue" class="bold-text">普通用户</a-tag>
            </a-option>
            <a-option value="admin">
              <a-tag color="green" class="bold-text">管理员</a-tag>
            </a-option>
          </a-select>
        </a-form-item>
        <a-form-item field="用户简介" label="用户简介 :" class="bold-text">
          <a-textarea
            v-model="userInfo.userProfile"
            placeholder="请输入用户简介"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import {
  FileControllerService,
  User,
  UserControllerService,
} from "../../../backendAPI/index";
import message from "@arco-design/web-vue/es/message";
import moment from "moment";
import { useRouter } from "vue-router";
import { FileItem, Message } from "@arco-design/web-vue";
import ACCESS_ENUM from "@/access/accessEnum";

const router = useRouter();
const tableRef = ref();
const file = ref();
const visible = ref(false);
const userInfo = ref<User>();
const dataList = ref([]);
const total = ref(0);
const searchParams = ref({
  userName: "",
  userProfile: "",
  userRole: "",
  pageSize: 10,
  current: 1,
});

// 加载用户数据
const loadData = async () => {
  const res = await UserControllerService.listUserByPageUsingPost({
    ...searchParams.value,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    message.error("加载失败，" + res.message);
  }
};

// 监听数据变化
watchEffect(() => {
  loadData();
});

onMounted(() => {
  loadData();
});

// 表格列配置
const columns = [
  { title: "用户账号", dataIndex: "userAccount", align: "center" },
  { title: "用户名称", dataIndex: "userName", align: "center" },
  { title: "用户头像", slotName: "userAvatar", align: "center" },
  {
    title: "用户简介",
    dataIndex: "userProfile",
    align: "center",
    ellipsis: true,
    tooltip: true,
    width: 180,
  },
  { title: "用户角色", slotName: "userRole", align: "center" },
  { title: "创建时间", slotName: "createTime", align: "center" },
  { title: "更新时间", slotName: "updateTime", align: "center" },
  { title: "操作", slotName: "optional", align: "center" },
];

// 分页操作
const onPageChange = (page: number) => {
  searchParams.value = { ...searchParams.value, current: page };
};

const onPageSizeChange = (size: number) => {
  searchParams.value = { ...searchParams.value, pageSize: size };
};

// 打开用户修改模态框
const openModalForm = async (userId: any) => {
  const res = await UserControllerService.getUserByIdUsingGet(userId);
  userInfo.value = res.data;
  visible.value = true;
};

// 重置搜索条件
const resetFilters = () => {
  searchParams.value = {
    userName: "",
    userProfile: "",
    userRole: "",
    pageSize: 10,
    current: 1,
  };
  loadData();
};

// 删除用户
const doDelete = async (user: User) => {
  const res = await UserControllerService.deleteUserUsingPost({ id: user.id });
  if (res.code === 0) {
    message.success("删除成功");
    loadData();
  } else {
    message.error("删除失败");
  }
};

// 提交搜索请求
const doSubmit = () => {
  searchParams.value = { ...searchParams.value, current: 1 };
};

// 上传头像
let userAvatarImg = userInfo.value?.userAvatar;

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

// 更新用户信息
const handleOk = async () => {
  const res = await UserControllerService.updateUserUsingPost({
    ...userInfo.value,
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

// 关闭模态框
const closeModel = () => {
  visible.value = false;
};

// 获取上传的文件
const onChange = async (_: never, currentFile: FileItem) => {
  file.value = { ...currentFile };
};
</script>

<style>
#userManageView {
  max-width: 1280px;
  margin: 0 auto;
  padding: 20px;
  border-radius: 12px;
  background: #f8f9fa;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  margin-bottom: 15px;
}

.button-group {
  margin-left: 10px;
}

.bold-text {
  font-weight: bold !important;
}

/*全局表格内容加粗*/
.arco-table-td-content {
  font-weight: bold;
}

.arco-modal-header .arco-modal-close-btn {
  position: absolute;
  right: 13px;
  top: 14px;
}

.formatted-time {
  font-weight: bold;
  color: #333;
}
</style>
