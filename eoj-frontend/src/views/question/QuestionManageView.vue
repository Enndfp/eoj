<template>
  <div id="questionManageView">
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
        field="tags"
        label="题目标签："
        tooltip="请选择要搜索的题目标签"
      >
        <a-select
          v-model="searchParams.tags"
          placeholder="选择题目标签"
          multiple
          allow-clear
          style="min-width: 150px"
        >
          <a-option v-for="tag in tags" :key="tag.value" :value="tag.value">
            <a-tag :color="tag.color" class="bold-text">{{ tag.name }}</a-tag>
          </a-option>
        </a-select>
      </a-form-item>

      <a-form-item
        field="difficulty"
        label="题目难度："
        tooltip="请选择要搜索的题目难度"
      >
        <a-select
          v-model="searchParams.difficulty"
          placeholder="选择题目难度"
          style="min-width: 150px"
        >
          <a-option value="0">
            <a-tag color="green" class="bold-text">简单</a-tag>
          </a-option>
          <a-option value="1">
            <a-tag color="orange" class="bold-text">中等</a-tag>
          </a-option>
          <a-option value="2">
            <a-tag color="red" class="bold-text">困难</a-tag>
          </a-option>
        </a-select>
      </a-form-item>

      <a-form-item
        field="creator"
        label="创建者："
        tooltip="请输入要搜索的创建者"
      >
        <a-input v-model="searchParams.creator" placeholder="请输入创建者" />
      </a-form-item>

      <a-form-item class="button-group">
        <a-button
          type="primary"
          shape="round"
          status="normal"
          @click="doSubmit"
          class="bold-text"
        >
          搜索
        </a-button>
      </a-form-item>
      <a-form-item class="button-group">
        <a-button
          type="outline"
          shape="round"
          @click="resetFilters"
          class="bold-text"
        >
          重置
        </a-button>
      </a-form-item>
      <a-form-item class="button-group">
        <a-button
          type="primary"
          shape="round"
          @click="goToAddQuestionPage"
          class="bold-text"
        >
          创建题目
        </a-button>
      </a-form-item>
    </a-form>

    <a-divider />

    <!-- 🔹 题目列表表格 -->
    <a-table
      :column-resizable="true"
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
        pageSizeOptions: [5, 10, 15, 20, 25],
      }"
      @page-change="onPageChange"
      @pageSizeChange="onPageSizeChange"
    >
      <!-- 题目名称（可点击） -->
      <template #title="{ record }">
        <a-link
          status="success"
          @click="toQuestionPage(record.id)"
          style="color: #1890ff"
        >
          {{ record.title || "未知题目" }}
        </a-link>
      </template>

      <!-- 题目标签 -->
      <template #tags="{ record }">
        <a-space>
          <a-tag
            v-for="(tag, index) in record.tags"
            :key="index"
            :color="getTagColor(tag)"
            class="bold-text"
          >
            {{ tag }}
          </a-tag>
        </a-space>
      </template>

      <!-- 题目难度 -->
      <template #difficulty="{ record }">
        <a-tag :color="getDifficultyColor(record.difficulty)" class="bold-text">
          {{ getDifficultyLabel(record.difficulty) }}
        </a-tag>
      </template>

      <!-- 题目答案展示 -->
      <template #answer="{ record }">
        <a-tooltip content="查看答案">
          <a-button
            class="bold-text"
            shape="circle"
            type="outline"
            size="small"
            @click="showCode(record)"
          >
            <icon-code />
          </a-button>
        </a-tooltip>
      </template>

      <!-- 判题配置展示 -->
      <template #judgeConfig="{ record }">
        <a-space wrap>
          <a-tag
            v-for="(config, key) in record.judgeConfig"
            :key="key"
            :color="getConfigTagColor(key)"
            class="bold-text"
            style="text-align: center"
          >
            {{ getConfigLabel(key) }}：{{ config }}
          </a-tag>
        </a-space>
      </template>

      <!-- 判题用例展示 -->
      <template #judgeCase="{ record }">
        <div
          style="
            text-align: center;
            max-height: 80px;
            overflow-y: auto;
            padding: 5px;
          "
        >
          <span
            v-for="(caseData, index) in record.judgeCase"
            :key="index"
            style="display: block; margin: 8px auto; cursor: pointer"
          >
            <a-tooltip :content="`查看用例 ${index + 1}`">
              <a-button
                shape="circle"
                type="outline"
                size="small"
                @click="showJudgeCase(caseData)"
                class="bold-text"
              >
                <icon-file />
              </a-button>
            </a-tooltip>
          </span>
        </div>
      </template>

      <!-- 创建时间 -->
      <template #createTime="{ record }">
        <span>{{ formatDate(record.createTime) }}</span>
      </template>

      <!-- 用户信息（头像和昵称） -->
      <template #creator="{ record }">
        <a-space>
          <a-avatar :size="40" shape="circle">
            <img alt="头像" :src="record.userVO?.userAvatar" />
          </a-avatar>
          <span class="bold-text">{{
            record.userVO?.userName || "匿名用户"
          }}</span>
        </a-space>
      </template>

      <!-- 操作按钮 -->
      <template #optional="{ record }">
        <a-space>
          <a-button
            shape="round"
            type="outline"
            @click="doUpdate(record)"
            class="bold-text"
          >
            修改
          </a-button>
          <a-popconfirm
            content="确定要删除此题目吗?"
            type="error"
            okText="是"
            cancelText="否"
            @cancel="() => console.log('已取消')"
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

    <!-- 🔹 代码查看模态框 -->
    <a-modal
      v-model:visible="isCodeModalVisible"
      title="题目答案"
      width="900px"
      class="code-modal"
      :footer="false"
    >
      <a-tabs
        default-active-key="java"
        type="rounded"
        size="small"
        position="top"
        class="answer-tabs"
      >
        <a-tab-pane key="java" title="Java">
          <div class="code-box">
            <MdViewer
              v-if="answerMap.java"
              :value="answerMap.java"
              style="max-height: 500px; overflow-y: auto"
            />
            <a-empty v-else description="此题暂未提供 Java 答案" />
          </div>
        </a-tab-pane>
        <a-tab-pane key="cpp" title="C++">
          <div class="code-box">
            <MdViewer
              v-if="answerMap.cpp"
              :value="answerMap.cpp"
              style="max-height: 500px; overflow-y: auto"
            />
            <a-empty v-else description="此题暂未提供 C++ 答案" />
          </div>
        </a-tab-pane>
        <a-tab-pane key="go" title="Go">
          <div class="code-box">
            <MdViewer
              v-if="answerMap.go"
              :value="answerMap.go"
              style="max-height: 500px; overflow-y: auto"
            />
            <a-empty v-else description="此题暂未提供 Go 答案" />
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-modal>

    <!-- 🔹 判题用例模态框 -->
    <a-modal
      v-model:visible="isJudgeCaseModalVisible"
      title="判题用例"
      width="600px"
    >
      <div v-html="judgeCaseContent"></div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { Message } from "@arco-design/web-vue";
import MdViewer from "@/components/MdViewer.vue";
import {
  Question,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../backendAPI";
import moment from "moment";

// Router for navigation
const router = useRouter();

// 🔹 数据状态
const tableRef = ref();
const dataList = ref([]);
const total = ref(0);
const searchParams = ref<QuestionQueryRequest>({
  title: "",
  tags: [],
  difficulty: undefined,
  creator: "",
  pageSize: 5,
  current: 1,
});

// 🔹 代码查看模态框状态
const isCodeModalVisible = ref(false);
const answerMap = ref<Record<string, string>>({
  java: "",
  cpp: "",
  go: "",
});

// 🔹 判题用例模态框状态
const isJudgeCaseModalVisible = ref(false);
const judgeCaseContent = ref("");

// 🔹 解析答案代码块
const parseAnswerCodeBlocks = (markdown: string | undefined) => {
  if (!markdown || typeof markdown !== "string") {
    console.warn("答案为空或无效，设置为默认占位符");
    answerMap.value = {
      java: "",
      cpp: "",
      go: "",
    };
    return;
  }

  // 初始化 answerMap
  answerMap.value = {
    java: "",
    cpp: "",
    go: "",
  };

  // 使用正则表达式分割 Java, C++, Go 答案
  const codeBlockRegex = /```(java|cpp|go)\n([\s\S]*?)\n```/g;
  let match;
  while ((match = codeBlockRegex.exec(markdown)) !== null) {
    const language = match[1];
    const code = match[2].trim();
    if (language in answerMap.value) {
      answerMap.value[language] = `\`\`\`${language}\n${code}\n\`\`\``;
    }
  }
};

// 🔹 获取并显示代码
const showCode = (record: any) => {
  if (!record || !record.answer) {
    parseAnswerCodeBlocks("");
  } else {
    parseAnswerCodeBlocks(record.answer);
  }
  isCodeModalVisible.value = true;
};

// 🔹 显示判题用例的详细信息
const showJudgeCase = (caseData: any) => {
  judgeCaseContent.value = `<strong>输入：</strong>${caseData.input}<br /><strong>输出：</strong>${caseData.output}`;
  isJudgeCaseModalVisible.value = true;
};

// 🔹 数据加载
const loadData = async () => {
  try {
    const res = await QuestionControllerService.listQuestionVoByPageUsingPost({
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    });
    if (res.code === 0) {
      dataList.value = res.data.records || [];
      total.value = res.data.total || 0;
    } else {
      Message.error("加载失败，" + res.message);
    }
  } catch (error) {
    Message.error("加载题目出错");
  }
};

/* 🔹 重置筛选条件 */
const resetFilters = () => {
  searchParams.value = {
    title: "",
    tags: [],
    difficulty: undefined,
    creator: "",
    pageSize: 5,
    current: 1,
  };
  loadData();
};

// 🔹 导航到创建题目页面
const goToAddQuestionPage = () => {
  router.push({ path: "/question/add" });
};

// 🔹 格式化日期
const formatDate = (date: string) => {
  return date ? moment(date).format("YYYY-MM-DD HH:mm") : "";
};

const tags = [
  { value: "栈", name: "栈", color: "darkslateblue" },
  { value: "图", name: "图", color: "darkseagreen" },
  { value: "数组", name: "数组", color: "darkgoldenrod" },
  { value: "链表", name: "链表", color: "darkmagenta" },
  { value: "排序", name: "排序", color: "darkorange" },
  { value: "哈希表", name: "哈希表", color: "salmon" },
  { value: "字符串", name: "字符串", color: "darkkhaki" },
  { value: "二叉树", name: "二叉树", color: "teal" },
  { value: "双指针", name: "双指针", color: "steelblue" },
  { value: "动态规划", name: "动态规划", color: "chocolate" },
  { value: "滑动窗口", name: "滑动窗口", color: "indigo" },
];

// 🔹 获取题目标签的颜色
const getTagColor = (tag: string) => {
  const tagColors: { [key: string]: string } = {
    栈: "darkslateblue",
    图: "darkseagreen",
    数组: "darkgoldenrod",
    链表: "darkmagenta",
    排序: "darkorange",
    哈希表: "salmon",
    字符串: "darkkhaki",
    二叉树: "teal",
    双指针: "steelblue",
    动态规划: "chocolate",
    滑动窗口: "indigo",
  };
  return tagColors[tag] || "gray";
};

// 获取题目难度标签颜色
const getDifficultyColor = (difficulty: number) => {
  switch (difficulty) {
    case 0:
      return "green";
    case 1:
      return "orange";
    case 2:
      return "red";
    default:
      return "gray";
  }
};

// 获取题目难度标签名称
const getDifficultyLabel = (difficulty: number) => {
  switch (difficulty) {
    case 0:
      return "简单";
    case 1:
      return "中等";
    case 2:
      return "困难";
    default:
      return "未知";
  }
};

// 🔹 判题配置字段颜色和标签
const getConfigTagColor = (key: string) => {
  switch (key) {
    case "timeLimit":
      return "orange";
    case "memoryLimit":
      return "blue";
    case "stackLimit":
      return "purple";
    default:
      return "grey";
  }
};

const getConfigLabel = (key: string) => {
  switch (key) {
    case "timeLimit":
      return "时间(ms)";
    case "memoryLimit":
      return "内存(KB)";
    case "stackLimit":
      return "堆栈(KB)";
    default:
      return "配置";
  }
};

// 🔹 表格列定义
const columns = [
  {
    title: "题目名称",
    slotName: "title",
    align: "center",
    width: 150,
  },
  {
    title: "题目内容",
    dataIndex: "content",
    align: "center",
    ellipsis: true,
    tooltip: true,
    width: 250,
  },
  {
    title: "题目标签",
    slotName: "tags",
    align: "center",
    ellipsis: true,
    tooltip: true,
    width: 250,
  },
  { title: "题目难度", slotName: "difficulty", align: "center", width: 100 },
  { title: "题目答案", slotName: "answer", align: "center", width: 120 },
  { title: "提交数", dataIndex: "submitNum", align: "center", width: 100 },
  { title: "通过数", dataIndex: "acceptedNum", align: "center", width: 100 },
  { title: "判题配置", slotName: "judgeConfig", align: "center", width: 380 },
  { title: "判题用例", slotName: "judgeCase", align: "center", width: 130 },
  { title: "创建者", slotName: "creator", align: "center", width: 150 },
  {
    title: "创建时间",
    slotName: "createTime",
    align: "center",
    width: 160,
    sortable: {
      sortDirections: ["ascend", "descend"],
    },
  },
  { title: "操作", slotName: "optional", align: "center", width: 160 },
];

// 🔹 分页操作
const onPageChange = (page: number) => {
  searchParams.value.current = page;
  loadData();
};

const onPageSizeChange = (size: number) => {
  searchParams.value.pageSize = size;
  searchParams.value.current = 1;
  loadData();
};

// 🔹 删除操作
const doDelete = async (question: Question) => {
  try {
    const res = await QuestionControllerService.deleteQuestionUsingPost({
      id: question.id,
    });
    if (res.code === 0) {
      Message.success("删除成功");
      loadData();
    } else {
      Message.error("删除失败，" + res.message);
    }
  } catch (error) {
    Message.error("删除出错");
    console.error("删除失败:", error); // 调试：记录错误
  }
};

// 🔹 修改操作
const doUpdate = (question: Question) => {
  router.push({
    path: "/question/update",
    query: { id: question.id },
  });
};

// 🔹 页面跳转
const toQuestionPage = (questionId: string) => {
  router.push({ path: `/question/practice/${questionId}` });
};

// 🔹 提交搜索
const doSubmit = () => {
  searchParams.value.current = 1;
  loadData();
};

// 🔹 初始化加载
onMounted(() => {
  loadData();
});
</script>

<style scoped>
#questionManageView {
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

.bold-text {
  font-weight: bold;
}

.button-group {
  margin-left: 10px;
}

.code-modal {
  border-radius: 8px;
}

.code-box {
  max-height: 500px;
  border-radius: 3px;
  border: 1px solid #ccc;
  background-color: #f7f7f7;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.code-box :deep(.markdown-body) {
  background-color: transparent;
  padding: 0;
}

.code-box :deep(.markdown-body pre) {
  border-radius: 3px;
  padding: 12px;
}

.code-box :deep(.markdown-body h3) {
  margin-top: 16px;
  margin-bottom: 8px;
}

.answer-tabs {
  margin-top: 8px;
}
</style>
