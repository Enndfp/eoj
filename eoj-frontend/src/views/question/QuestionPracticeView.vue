<template>
  <div id="questionPracticeView">
    <a-row :gutter="[24, 24]">
      <!-- 题目信息部分 -->
      <a-col :md="12" :xs="24">
        <a-tabs default-active-key="question">
          <!-- 题目内容 -->
          <a-tab-pane key="question" title="题目">
            <a-card v-if="question" :title="question.title">
              <!-- 题目描述 -->
              <MdViewer :value="question.content || ''"></MdViewer>

              <!-- 判题用例 -->
              <a-divider />
              <h4 class="bold-text">示例：</h4>
              <a-row v-if="question.judgeCase?.length">
                <a-col
                  :span="24"
                  v-for="(item, index) in question.judgeCase"
                  :key="index"
                >
                  <a-card :title="'示例 ' + (index + 1)">
                    <p><strong>输入：</strong>{{ item.input }}</p>
                    <p><strong>输出：</strong>{{ item.output }}</p>
                  </a-card>
                </a-col>
              </a-row>

              <!-- 判题限制 -->
              <a-divider />
              <a-descriptions
                title="限制："
                :column="{ xs: 1, md: 2, lg: 3 }"
                size="small"
              >
                <a-descriptions-item label="时间限制">
                  {{ question.judgeConfig.timeLimit ?? 0 }} ms
                </a-descriptions-item>
                <a-descriptions-item label="内存限制">
                  {{ question.judgeConfig.memoryLimit ?? 0 }} KB
                </a-descriptions-item>
                <a-descriptions-item label="堆栈限制">
                  {{ question.judgeConfig.stackLimit ?? 0 }} KB
                </a-descriptions-item>
              </a-descriptions>

              <template #extra>
                <a-space wrap>
                  <a-tag
                    :color="getDifficultyColor(question.difficulty)"
                    class="bold-text"
                  >
                    {{ getDifficultyText(question.difficulty) }}
                  </a-tag>
                  <a-tag
                    v-for="(tag, index) in question.tags"
                    :key="index"
                    :color="getTagColor(tag)"
                    class="bold-text"
                  >
                    {{ tag }}
                  </a-tag>
                </a-space>
              </template>
            </a-card>
          </a-tab-pane>

          <!-- 题解区 -->
          <a-tab-pane key="answer" title="题解">
            <a-card :bordered="false" class="answer-card">
              <a-tabs
                default-active-key="java"
                type="rounded"
                size="small"
                position="left"
                class="answer-tabs"
              >
                <a-tab-pane key="java" title="Java">
                  <div class="answer-content">
                    <MdViewer
                      :value="question?.answer ?? '此题暂未提供 Java 答案'"
                      class="markdown-viewer"
                    />
                  </div>
                </a-tab-pane>
                <a-tab-pane key="cpp" title="C++">
                  <div class="answer-content">
                    <MdViewer
                      :value="'此题暂未提供 C++ 答案'"
                      class="markdown-viewer"
                    />
                  </div>
                </a-tab-pane>
                <a-tab-pane key="go" title="Go">
                  <div class="answer-content">
                    <MdViewer
                      :value="'此题暂未提供 Go 答案'"
                      class="markdown-viewer"
                    />
                  </div>
                </a-tab-pane>
              </a-tabs>
            </a-card>
          </a-tab-pane>

          <!-- 提交记录区 -->
          <a-tab-pane key="submissions" title="提交记录">
            <a-card :bordered="false">
              <a-table
                :columns="columns"
                :data="submissionRecords"
                row-key="id"
                stripe
                :bordered="true"
                class="submission-table"
                :pagination="pagination"
                @page-change="handlePageChange"
              >
                <!-- 状态列 -->
                <template #status="{ record }">
                  <a-tag
                    :color="getStatusColor(record.status)"
                    class="bold-text"
                  >
                    {{ getStatusText(record.status) }}
                  </a-tag>
                </template>

                <!-- 语言列 -->
                <template #language="{ record }">
                  <span class="bold-text">
                    {{ capitalizeFirstLetter(record.language) }}
                  </span>
                </template>

                <!-- 执行用时列 -->
                <template #judgeInfo="{ record }">
                  <span class="bold-text">
                    {{ record.judgeInfo?.time || "N/A" }} ms
                  </span>
                </template>

                <!-- 消耗内存列 -->
                <template #memory="{ record }">
                  <span class="bold-text">
                    {{ record.judgeInfo?.memory || "N/A" }} KB
                  </span>
                </template>

                <!-- 提交时间列 -->
                <template #createTime="{ record }">
                  <span class="bold-text">
                    {{ formatDateTime(record.createTime) }}
                  </span>
                </template>

                <!-- 操作列 -->
                <template #operation="{ record }">
                  <a-tooltip position="top" content="查看代码">
                    <a-button
                      type="text"
                      shape="circle"
                      @click="viewSubmissionCode(record)"
                    >
                      <template #icon>
                        <IconEye />
                      </template>
                    </a-button>
                  </a-tooltip>
                </template>

                <!-- 空数据展示 -->
                <template #empty>
                  <div class="empty-container">
                    <p>暂无提交记录，请先提交代码</p>
                  </div>
                </template>
              </a-table>
            </a-card>
          </a-tab-pane>
        </a-tabs>
      </a-col>

      <!-- 提交代码部分 -->
      <a-col :md="12" :xs="24">
        <a-card>
          <!-- 编程语言和主题选择 -->
          <a-row :gutter="24">
            <a-col :span="4">
              <a-form-item size="small">
                <!-- 计时器 -->
                <div class="startTimer" v-if="!showTimer" @click="startTimer">
                  <a-tooltip position="top" content="开始计时" mini>
                    <IconClockCircle size="30" style="margin-left: 28px" />
                  </a-tooltip>
                </div>
                <!-- 停止计时器 -->
                <div
                  class="stopTimer"
                  v-else
                  @click="stopTimer"
                  style="display: flex; align-items: center"
                >
                  <div>{{ formatTime(time) }}</div>
                  <IconLoop
                    class="stopTimerIcon"
                    size="30"
                    style="margin-left: 8px"
                  />
                </div>
              </a-form-item>
            </a-col>

            <a-col :span="10">
              <a-form-item field="language">
                <a-select v-model="form.language" placeholder="选择编程语言">
                  <a-option value="java">
                    <a-tag color="#f89820" class="bold-text">Java</a-tag>
                  </a-option>
                  <a-option value="cpp" disabled>
                    <a-tag color="#00599C" class="bold-text">C++</a-tag>
                  </a-option>
                  <a-option value="go" disabled>
                    <a-tag color="#00ADD8" class="bold-text">Go</a-tag>
                  </a-option>
                </a-select>
              </a-form-item>
            </a-col>

            <a-col :span="10">
              <a-form-item field="theme">
                <a-select v-model="form.theme" placeholder="选择主题">
                  <a-option value="vs-light">
                    <a-tag color="#ADD8E6" class="bold-text">Light</a-tag>
                  </a-option>
                  <a-option value="vs-dark">
                    <a-tag color="#1e1e1e" class="bold-text">Dark</a-tag>
                  </a-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>

          <!-- 代码编辑器 -->
          <CodeEditor
            :value="form.code"
            :language="form.language"
            :theme="form.theme"
            :handle-change="changeCode"
            style="
              border-radius: 3px;
              border: 1px solid #ccc;
              background-color: #f7f7f7;
            "
          ></CodeEditor>

          <a-divider :size="0"></a-divider>

          <a-button type="primary" style="min-width: 200px" @click="doSubmit">
            提交代码
          </a-button>
        </a-card>
      </a-col>
    </a-row>

    <!-- 代码查看模态框 -->
    <a-modal
      v-model:visible="codeModalVisible"
      title="提交代码详情"
      @cancel="codeModalVisible = false"
      :footer="false"
      width="850px"
      class="code-modal"
    >
      <div v-if="selectedSubmission" class="modal-content">
        <!-- 用户信息和判题信息 -->
        <a-row :gutter="16" class="top-section">
          <!-- 用户信息 -->
          <a-col :span="12">
            <div class="user-info">
              <a-avatar :size="48" shape="circle">
                <img alt="头像" :src="selectedSubmission.userVO?.userAvatar" />
              </a-avatar>
              <div class="user-details">
                <span class="username">{{
                  selectedSubmission.userVO.userName
                }}</span>
                <span class="profile">{{
                  selectedSubmission.userVO.userProfile
                }}</span>
              </div>
            </div>
          </a-col>

          <!-- 判题信息 -->
          <a-col :span="12">
            <div
              class="judge-message-top"
              v-if="selectedSubmission.judgeInfo?.message"
            >
              <a-alert
                :type="selectedSubmission.status === 2 ? 'success' : 'error'"
              >
                <span class="bold-text">{{
                  selectedSubmission.judgeInfo.message
                }}</span>
              </a-alert>
            </div>
          </a-col>
        </a-row>

        <!-- 提交信息 -->
        <div class="submission-info">
          <a-descriptions
            :column="{ xs: 1, sm: 2, md: 3 }"
            size="small"
            bordered
          >
            <a-descriptions-item label="状态">
              <a-tag
                :color="getStatusColor(selectedSubmission.status)"
                class="bold-text"
              >
                {{ getStatusText(selectedSubmission.status) }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="语言">
              <span class="bold-text">{{
                capitalizeFirstLetter(selectedSubmission.language)
              }}</span>
            </a-descriptions-item>
            <a-descriptions-item label="执行用时">
              <span class="bold-text"
                >{{ selectedSubmission.judgeInfo?.time || "N/A" }} ms</span
              >
            </a-descriptions-item>
            <a-descriptions-item label="消耗内存">
              <span class="bold-text"
                >{{ selectedSubmission.judgeInfo?.memory || "N/A" }} KB</span
              >
            </a-descriptions-item>
            <a-descriptions-item label="提交时间">
              <span class="bold-text">{{
                formatDateTime(selectedSubmission.createTime)
              }}</span>
            </a-descriptions-item>
          </a-descriptions>
        </div>

        <!-- 提交代码内容 -->
        <div class="code-container">
          <CodeEditor
            :value="selectedSubmission.code"
            :language="selectedSubmission.language.toLowerCase()"
            :theme="form.theme"
            readonly
            style="height: 500px; border-radius: 3px; border: 1px solid #ccc"
          ></CodeEditor>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
/* ---------------------- 导入依赖 ---------------------- */
import {
  computed,
  defineProps,
  onMounted,
  onUnmounted,
  ref,
  watch,
  withDefaults,
} from "vue";
import {
  LoginUserVO,
  QuestionControllerService,
  QuestionSubmitAddRequest,
  QuestionVO,
} from "../../../backendAPI";
import { Message } from "@arco-design/web-vue";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";
import { useStore } from "vuex";
import { IconEye } from "@arco-design/web-vue/es/icon";
import { IconClockCircle, IconLoop } from "@arco-design/web-vue/es/icon";
import moment from "moment/moment";

/* ---------------------- 状态管理 ---------------------- */

// 题目相关状态
const question = ref<QuestionVO>(); // 题目信息

// 提交记录相关状态
const submissionRecords = ref([]); // 提交记录列表
const currentPage = ref(1); // 当前页码
const pageSize = ref(10); // 每页记录数
const total = ref(0); // 总记录数
const codeModalVisible = ref(false); // 控制代码查看模态框显示
const selectedSubmission = ref(null); // 当前查看的提交记录

// 提交表单相关状态
const form = ref<QuestionSubmitAddRequest>({
  language: "java", // 默认语言为 Java
  code: `/*
 * 请根据题目要求编写解题代码
 * 1. 无需导入 package
 * 2. 类名必须是 Main，不能修改
 * 3. 请从命令行读取数据并输出结果
 */
public class Main {
    public static void main(String[] args) {
        // 在此编写您的解题代码
    }
}
  `,
  theme: "vs-light", // 默认主题为 Light
});

// 计时器相关状态
const showTimer = ref(false); // 是否显示计时器
const time = ref(0); // 计时器时间（秒）
let intervalId: any = null; // 计时器 interval ID

/* ---------------------- 表格和分页配置 ---------------------- */

// 提交记录表格列定义
const columns = [
  {
    title: "状态",
    dataIndex: "status",
    width: 120,
    slotName: "status",
    align: "center",
  },
  {
    title: "语言",
    dataIndex: "language",
    width: 100,
    slotName: "language",
    align: "center",
  },
  {
    title: "执行用时",
    dataIndex: "judgeInfo",
    width: 100,
    slotName: "judgeInfo",
    align: "center",
  },
  {
    title: "消耗内存",
    dataIndex: "memory",
    width: 100,
    slotName: "memory",
    align: "center",
  },
  {
    title: "提交时间",
    dataIndex: "createTime",
    width: 180,
    slotName: "createTime",
    align: "center",
  },
  {
    title: "操作",
    dataIndex: "operation",
    width: 80,
    fixed: "right",
    slotName: "operation",
    align: "center",
  },
];

// 分页配置
const pagination = computed(() => ({
  total: total.value,
  pageSize: pageSize.value,
  current: currentPage.value,
  showTotal: true,
  showPageSize: true,
  pageSizeOptions: [5, 10, 20, 50],
  onPageSizeChange: (size: number) => {
    pageSize.value = size;
    loadSubmitRecords(currentPage.value);
  },
}));

/* ---------------------- 工具函数 ---------------------- */

// 获取状态颜色
const getStatusColor = (status: any): string => {
  const numericStatus = Number(status);
  const statusColors: { [key: number]: string } = {
    0: "red", // 解答错误
    1: "red", // 解答错误
    2: "green", // 通过
    3: "red", // 解答错误
  };
  return statusColors[numericStatus] || "gray";
};

// 获取状态文本
const getStatusText = (status: any): string => {
  const numericStatus = Number(status);
  const statusTexts: { [key: number]: string } = {
    0: "错误",
    1: "错误",
    2: "通过",
    3: "错误",
  };
  return statusTexts[numericStatus] || "未知";
};

// 首字母大写
const capitalizeFirstLetter = (str: string): string => {
  if (!str) return str;
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

// 格式化日期时间
const formatDateTime = (date: string) =>
  moment(date).format("YYYY-MM-DD HH:mm:ss");

// 格式化计时器时间
const formatTime = (time: number): string => {
  const hours = Math.floor(time / 3600);
  const minutes = Math.floor((time % 3600) / 60);
  const seconds = time % 60;
  return `${hours < 10 ? "0" + hours : hours}:${
    minutes < 10 ? "0" + minutes : minutes
  }:${seconds < 10 ? "0" + seconds : seconds}`;
};

// 获取题目标签颜色
const getTagColor = (tag: string): string => {
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

// 获取题目难度颜色
const getDifficultyColor = (difficulty: number): string => {
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

// 获取题目难度文本
const getDifficultyText = (difficulty: number): string => {
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

/* ---------------------- 计时器逻辑 ---------------------- */

// 开始计时
const startTimer = () => {
  showTimer.value = true;
};

// 停止计时
const stopTimer = () => {
  showTimer.value = false;
  time.value = 0;
};

// 监听计时器状态变化
watch(
  () => showTimer.value,
  (newVal: boolean) => {
    if (newVal) {
      intervalId = setInterval(() => {
        time.value++;
      }, 1000);
    } else {
      clearInterval(intervalId);
    }
  }
);

// 组件卸载时清除计时器
onUnmounted(() => {
  clearInterval(intervalId);
});

/* ---------------------- 数据加载和提交逻辑 ---------------------- */

// 定义 Props 接口
interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

// 获取登录用户信息
const store = useStore();
const loginUser: LoginUserVO = computed(
  () => store.state.user?.loginUser
) as LoginUserVO;

// 加载题目信息
const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
  } else {
    Message.error("加载失败：" + res.message);
  }
};

// 加载提交记录
const loadSubmitRecords = async (page = 1) => {
  try {
    currentPage.value = page;

    const res =
      await QuestionControllerService.listQuestionSubmitVoByPageUsingPost({
        questionId: props.id as any,
        userId: loginUser.value?.id,
        pageSize: pageSize.value,
        current: page,
        sortField: "createTime",
        sortOrder: "descend",
      });

    if (res.code === 0) {
      submissionRecords.value = res.data.records || [];
      total.value = Number(res.data.total) || 0;
    } else {
      Message.error("获取提交记录失败：" + res.message);
    }
  } catch (error) {
    Message.error("加载提交记录出错");
  }
};

// 处理分页切换
const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadSubmitRecords(page);
};

// 提交代码
const doSubmit = async () => {
  if (!question.value?.id) return;
  const res = await QuestionControllerService.doQuestionSubmitUsingPost({
    ...form.value,
    questionId: question.value.id,
  });
  if (res.code === 0) {
    Message.success("提交成功");
    loadSubmitRecords(currentPage.value);
  } else {
    Message.error("提交失败，" + res.message);
  }
};

// 查看提交代码
const viewSubmissionCode = (record: any) => {
  selectedSubmission.value = record;
  codeModalVisible.value = true;
};

// 代码编辑器内容变更
const changeCode = (value: string) => {
  form.value.code = value;
};

/* ---------------------- 生命周期钩子 ---------------------- */

// 组件挂载时加载数据
onMounted(() => {
  loadData();
  loadSubmitRecords();
});
</script>

<style scoped>
/* ---------------------- 页面整体样式 ---------------------- */
#questionPracticeView {
  max-width: 1600px;
  margin: 0 auto;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

/* ---------------------- 题解卡片样式 ---------------------- */
.answer-card {
  background-color: #fafafa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 3px;
  height: 100%;
}

.answer-tabs {
  height: 70vh;
}

.answer-content {
  height: 100%;
  overflow: auto;
  padding: 16px;
  background-color: #fff;
  border-radius: 6px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.markdown-viewer {
  font-size: 14px;
  line-height: 1.8;
}

/* ---------------------- 提交记录表格样式 ---------------------- */
.submission-table {
  margin-top: 8px;
}

.empty-container {
  text-align: center;
  padding: 20px;
}

.empty-container p {
  margin-top: 10px;
  color: #888;
}

/* ---------------------- 模态框样式 ---------------------- */
.code-modal {
  border-radius: 8px;
}

.modal-content {
  padding: 16px; /* 减少顶部和底部内边距，使内容整体向上移动 */
}

.top-section {
  margin-bottom: 16px; /* 减少底部外边距，整体向上 */
}

.user-info {
  display: flex;
  align-items: center;
  min-height: 60px; /* 确保左侧和右侧高度对齐 */
}

.user-info .arco-avatar {
  margin-right: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  font-size: 16px;
  color: #1e1e1e;
}

.profile {
  font-size: 12px;
  color: #666;
}

.judge-message-top {
  min-height: 60px; /* 确保右侧和左侧高度对齐 */
  display: flex;
  align-items: center;
}

.submission-info {
  margin-bottom: 16px; /* 减少底部外边距，整体向上 */
}

.code-container {
  border: 1px solid #ccc;
  border-radius: 3px;
}

/* ---------------------- 通用样式 ---------------------- */
.bold-text {
  font-weight: bold;
}
</style>
