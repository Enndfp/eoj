<template>
  <div id="questionSubmitView">
    <!-- 🔹 搜索表单 -->
    <a-form :model="searchParams" layout="inline" class="search-form bold-text">
      <a-form-item
        field="title"
        label="题目名称："
        tooltip="请输入要搜索的题目名称"
      >
        <a-input v-model="searchParams.title" placeholder="请输入题目名称" />
      </a-form-item>
      <a-form-item
        field="language"
        label="编程语言："
        tooltip="请选择要搜索的编程语言"
      >
        <a-select v-model="searchParams.language" placeholder="选择编程语言">
          <a-option value="Java">
            <a-tag color="#f89820" class="bold-text">Java</a-tag>
          </a-option>
          <a-option value="C++">
            <a-tag color="#00599C" class="bold-text">C++</a-tag>
          </a-option>
          <a-option value="Go">
            <a-tag color="#00ADD8" class="bold-text">Go</a-tag>
          </a-option>
        </a-select>
      </a-form-item>
      <a-form-item
        field="status"
        label="判题状态："
        tooltip="请选择要搜索的判题状态"
      >
        <a-select v-model="searchParams.status" placeholder="选择状态">
          <a-option :value="0">
            <a-tag color="cyan" class="bold-text">待判题</a-tag>
          </a-option>
          <a-option :value="1">
            <a-tag color="gold" class="bold-text">判题中</a-tag>
          </a-option>
          <a-option :value="2">
            <a-tag color="green" class="bold-text">成功</a-tag>
          </a-option>
          <a-option :value="3">
            <a-tag color="red" class="bold-text">失败</a-tag>
          </a-option>
        </a-select>
      </a-form-item>
      <a-form-item class="button-group">
        <a-button
          type="outline"
          shape="round"
          status="normal"
          @click="doSubmit"
          class="bold-text"
          >搜索
        </a-button>
      </a-form-item>
      <a-form-item class="button-group">
        <a-button
          type="primary"
          shape="round"
          @click="resetFilters"
          class="bold-text"
          >重置
        </a-button>
      </a-form-item>
    </a-form>

    <a-divider />

    <!-- 🔹 提交记录表格 -->
    <a-table
      column-resizable
      wrapper
      :columns="columns"
      :data="dataList"
      :pagination="{
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        showTotal: true,
        total,
        showJumper: true,
        showPageSize: true,
      }"
      @page-change="onPageChange"
      @pageSizeChange="onPageSizeChange"
    >
      <!-- 提交者（头像 + 用户名） -->
      <template #submitter="{ record }">
        <a-space>
          <a-avatar :size="40" shape="circle">
            <img alt="头像" :src="record.userVO?.userAvatar" />
          </a-avatar>
          <span class="bold-text">{{
            record.userVO?.userName || "匿名用户"
          }}</span>
        </a-space>
      </template>

      <!-- 题目名称（可点击） -->
      <template #questionTitle="{ record }">
        <a-link
          status="success"
          @click="toQuestionPage(record.questionId)"
          class="bold-text"
          style="color: #1890ff"
        >
          {{ record.questionVO?.title || "未知题目" }}
        </a-link>
      </template>

      <!-- 编程语言（颜色优化） -->
      <template #language="{ record }">
        <a-tag
          :style="{
            backgroundColor: getLanguageColor(record.language),
            color: '#fff',
          }"
          class="bold-text"
        >
          {{ formatLanguage(record.language) }}
        </a-tag>
      </template>

      <!-- 判题信息（颜色优化） -->
      <template #judgeInfo="{ record }">
        <a-space wrap>
          <template v-for="(info, key) in record.judgeInfo" :key="key">
            <a-tag
              v-if="info !== null && info !== ''"
              :color="getJudgeInfoColor(key, info)"
              class="bold-text"
            >
              <span class="bold-text">{{ judgeInfoLabels[key] || key }}:</span>
              {{
                key === "memory"
                  ? `${info} KB`
                  : key === "time"
                  ? `${info} ms`
                  : info
              }}
            </a-tag>
            <a-tag v-else color="default">
              <span class="bold-text">{{ judgeInfoLabels[key] || key }}:</span>
              无数据
            </a-tag>
          </template>
        </a-space>
      </template>

      <!-- 判题状态（颜色优化） -->
      <template #status="{ record }">
        <a-tag :color="statusColors[record.status] || 'default'">
          <span class="bold-text">{{
            statusLabels[record.status] || "未知"
          }}</span>
        </a-tag>
      </template>

      <!-- 提交时间（优化格式 & 颜色） -->
      <template #createTime="{ record }">
        <span class="formatted-time">
          {{ formatDate(record.createTime) }}
        </span>
      </template>

      <!-- 查看代码按钮 -->
      <template #viewCode="{ record }">
        <a-button
          class="bold-text"
          type="outline"
          size="small"
          @click="showCode(record)"
          >查看代码
        </a-button>
      </template>
    </a-table>
    <!-- 🔹 代码查看模态框 -->
    <a-modal
      v-model:visible="isCodeModalVisible"
      title="代码查看"
      width="850px"
      class="code-modal"
    >
      <div class="code-box">
        <pre><code class="language-java" v-html="highlightedCode"></code></pre>
      </div>
    </a-modal>
  </div>
</template>

--- ## **🎨 TypeScript 代码** ```ts
<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import { useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";
import moment from "moment";
import hljs from "highlight.js";
import "highlight.js/styles/github.css";
import {
  QuestionControllerService,
  QuestionSubmitQueryRequest,
} from "../../../backendAPI";
import { useStore } from "vuex";
import AccessEnum from "@/access/accessEnum";

const router = useRouter();
const store = useStore();
const loginUser = store.state.user.loginUser;

/* 🔹 数据状态 */
const dataList = ref([]); // 存储提交记录
const total = ref(0); // 总记录数
const searchParams = ref<QuestionSubmitQueryRequest>({
  title: undefined,
  language: undefined,
  status: undefined,
  pageSize: 10,
  current: 1,
});

/* 🔹 颜色映射 */
const statusColors: Record<number, string> = {
  0: "cyan", // 待判题
  1: "gold", // 判题中
  2: "green", // 成功
  3: "red", // 失败
};
const statusLabels: Record<number, string> = {
  0: "待判题",
  1: "判题中",
  2: "成功",
  3: "失败",
};
const getLanguageColor = (lang: string) => {
  const colors: Record<string, string> = {
    java: "#f89820", // Java - 橙色（Java 官方 Logo 颜色）
    "c++": "#00599C", // C++ - 深蓝色（C++ Logo 颜色）
    go: "#00ADD8", // Go - 天蓝色（Go 语言 Logo 颜色）
  };
  return colors[lang] || "gray"; // 默认灰色，防止未匹配语言
};
const getJudgeInfoColor = (key: string, value: string) => {
  if (key === "message") {
    switch (value) {
      case "Accepted":
        return "green"; // 成功
      case "Answer Error":
        return "red"; // 答案错误
      case "Compile Error":
        return "orange"; // 编译错误
      case "Runtime Error":
        return "yellow"; // 运行错误
      case "Time Limit Exceeded":
        return "blue"; // 超时
      case "Memory Limit Exceeded":
        return "purple"; // 内存溢出
      default:
        return "gray"; // 其他情况默认灰色
    }
  }

  // 为内存字段设置绿色（代表资源管理）
  if (key === "memory") {
    return "blue"; // 蓝色
  }

  // 为时间字段设置橙色（突出性能）
  if (key === "time") {
    return "orange"; // 橙色
  }

  return "default"; // 默认为默认颜色
};

const judgeInfoLabels: Record<string, string> = {
  message: "结果",
  time: "耗时",
  memory: "内存",
};

/* 🔹 代码模态框 */
const isCodeModalVisible = ref(false);
const highlightedCode = ref("");

/* 🔹 代码查看逻辑 */
const showCode = (record: any) => {
  if (
    loginUser.userRole !== AccessEnum.ADMIN &&
    record.userId !== loginUser.id
  ) {
    Message.warning("您无权查看此代码");
    return;
  }
  highlightedCode.value = hljs.highlight(record.code, {
    language: "java",
  }).value;
  isCodeModalVisible.value = true;
};

/* 🔹 数据加载 */
const loadData = async () => {
  const res =
    await QuestionControllerService.listQuestionSubmitVoByPageUsingPost({
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    });
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
  } else {
    Message.error("加载失败：" + res.message);
  }
};

/* 🔹 重置筛选条件 */
const resetFilters = () => {
  searchParams.value = {
    language: undefined,
    title: undefined,
    status: undefined,
    pageSize: 10,
    current: 1,
  };
  loadData();
};

/* 🔹 格式化数据 */
const formatDate = (date: string) => moment(date).format("YYYY-MM-DD HH:mm");
const formatLanguage = (lang: string) =>
  lang === "cpp" ? "C++" : lang.toUpperCase();

/* 🔹 表格列 */
const columns = [
  { title: "提交者", slotName: "submitter", align: "center" },
  { title: "题目名称", slotName: "questionTitle", align: "center" },
  { title: "编程语言", slotName: "language", align: "center" },
  { title: "判题信息", slotName: "judgeInfo", align: "center" },
  { title: "判题状态", slotName: "status", align: "center" },
  { title: "提交时间", slotName: "createTime", align: "center" },
  { title: "提交代码", slotName: "viewCode", align: "center" },
];

/* 🔹 分页事件 */
const onPageChange = (page: number) => {
  searchParams.value.current = page;
};
const onPageSizeChange = (size: number) => {
  searchParams.value.pageSize = size;
};

/* 🔹 页面跳转 */
const toQuestionPage = (questionId: string) => {
  router.push({ path: `/question/view/${questionId}` });
};

/* 🔹 触发搜索 */
const doSubmit = () => {
  searchParams.value.current = 1;
  loadData();
};

/* 🔹 监听数据变化 */
watchEffect(() => {
  loadData();
});

onMounted(() => {
  loadData();
});
</script>
<style scoped>
#questionSubmitView {
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
  font-weight: bold;
}

.formatted-time {
  font-weight: bold;
  color: #333;
}
</style>
