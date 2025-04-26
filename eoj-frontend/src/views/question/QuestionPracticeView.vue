<template>
  <div id="questionPracticeView">
    <a-row :gutter="[24, 24]">
      <!-- 题目信息部分 -->
      <a-col :md="12" :xs="24">
        <a-tabs v-model:activeKey="activeTab" default-active-key="question">
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
                    <MdViewer :value="answerMap.java" class="markdown-viewer" />
                  </div>
                </a-tab-pane>
                <a-tab-pane key="cpp" title="C++">
                  <div class="answer-content">
                    <MdViewer :value="answerMap.cpp" class="markdown-viewer" />
                  </div>
                </a-tab-pane>
                <a-tab-pane key="go" title="Go">
                  <div class="answer-content">
                    <MdViewer :value="answerMap.go" class="markdown-viewer" />
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
                    {{ getLanguageDisplayName(record.language) }}
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
                  <a-option value="cpp">
                    <a-tag color="#00599C" class="bold-text">C++</a-tag>
                  </a-option>
                  <a-option value="go">
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
            :style="{
              borderRadius: '3px',
              border: '1px solid #ccc',
              backgroundColor: '#f7f7f7',
              height: editorHeight,
            }"
          ></CodeEditor>

          <!-- 美化后的控制台 -->
          <div class="console-container" v-show="consoleVisible">
            <a-tabs v-model="activeConsoleTab" type="card" :animated="false">
              <a-tab-pane key="testcase" title="测试用例">
                <div class="console-input">
                  <div v-if="firstTestCase" class="test-case-content">
                    <div class="case-body">
                      <div class="case-item">
                        <span class="label">输入:</span>
                        <div class="value">{{ firstTestCase.input }}</div>
                      </div>
                      <div class="case-item">
                        <span class="label">输出:</span>
                        <div class="value">{{ firstTestCase.output }}</div>
                      </div>
                    </div>
                  </div>
                </div>
              </a-tab-pane>
              <a-tab-pane key="result" title="执行结果">
                <div class="console-result">
                  <div v-if="testResult" class="result-panel">
                    <div class="result-header">
                      <a-tag
                        :color="isPassed ? '#52c41a' : '#ff4d4f'"
                        class="status-tag"
                      >
                        {{ isPassed ? "通过" : "未通过" }}
                      </a-tag>
                    </div>
                    <div class="result-body">
                      <div class="result-item">
                        <span class="label">输入:</span>
                        <div class="value">{{ testResult.input || "无" }}</div>
                      </div>
                      <div class="result-item">
                        <span class="label">输出:</span>
                        <div class="value">{{ testResult.output || "无" }}</div>
                      </div>
                      <div class="result-item">
                        <span class="label">预期结果:</span>
                        <div class="value">
                          {{ firstTestCase?.output || "无" }}
                        </div>
                      </div>
                      <div class="result-item" v-if="testResult.judgeInfo">
                        <span class="label">内存:</span>
                        <div class="value">
                          {{ testResult.judgeInfo.memory }} KB
                        </div>
                      </div>
                      <div class="result-item" v-if="testResult.judgeInfo">
                        <span class="label">时间:</span>
                        <div class="value">
                          {{ testResult.judgeInfo.time }} ms
                        </div>
                      </div>
                    </div>
                  </div>
                  <div v-else-if="testRunning" class="running-state">
                    <a-spin>运行中...</a-spin>
                    <p class="running-text">代码正在执行，请稍候...</p>
                  </div>
                  <div v-else class="empty-state">
                    <a-empty description="请先运行代码" />
                  </div>
                </div>
              </a-tab-pane>
            </a-tabs>
          </div>

          <!-- 控制台按钮和操作按钮 -->
          <a-divider :size="0"></a-divider>
          <div class="console-footer">
            <a-button type="text" @click="toggleConsole">
              控制台
              <IconDown v-if="consoleVisible" />
              <IconUp v-else />
            </a-button>
            <a-space>
              <a-button type="outline" @click="runCode" :loading="testRunning"
                >运行
              </a-button>
              <a-button type="primary" @click="doSubmit">提交</a-button>
            </a-space>
          </div>
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
                getLanguageDisplayName(selectedSubmission.language)
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
import {
  IconClockCircle,
  IconDown,
  IconEye,
  IconLoop,
  IconUp,
} from "@arco-design/web-vue/es/icon";
import moment from "moment/moment";

/* ---------------------- 状态管理 ---------------------- */

// 题目相关状态
const question = ref<QuestionVO>();
const activeTab = ref("question");

// 提交记录相关状态
const submissionRecords = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const codeModalVisible = ref(false);
const selectedSubmission = ref(null);

// 定义语言初始模板
const languageTemplates = {
  java: `/*
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
  cpp: `/*
 * 请根据题目要求编写解题代码
 * 1. 从命令行参数读取数据
 * 2. 输出结果到标准输出
 */
#include <iostream>

using namespace std;

int main(int argc, char* argv[]) {
    // 在此编写您的解题代码
    return 0;
}
  `,
  go: `/*
 * 请根据题目要求编写解题代码
 * 1. 从命令行参数读取数据
 * 2. 输出结果到标准输出
 */
package main

import (
    "fmt"
    "os"
    "strconv"
)

func main() {
    // 在此编写您的解题代码
}
  `,
};

// 提交表单相关状态
const form = ref<QuestionSubmitAddRequest>({
  language: "java",
  code: languageTemplates.java,
  theme: "vs-light",
});

// 计时器相关状态
const showTimer = ref(false);
const time = ref(0);
let intervalId: any = null;

// 控制台相关状态
const consoleVisible = ref(false);
const activeConsoleTab = ref("testcase");
const testResult = ref<any>(null);
const testRunning = ref(false);
const editorHeight = ref("66.7vh");

// 答案映射状态
const answerMap = ref<Record<string, string>>({
  java: "此题暂未提供 Java 答案",
  cpp: "此题暂未提供 C++ 答案",
  go: "此题暂未提供 Go 答案",
});

// 从题目中读取第一个测试用例
const firstTestCase = computed(() => {
  if (question.value?.judgeCase && question.value.judgeCase.length > 0) {
    return question.value.judgeCase[0];
  }
  return null;
});

// 判断是否通过
const isPassed = computed(() => {
  if (!testResult.value || !firstTestCase.value) return false;
  return testResult.value.output === firstTestCase.value.output;
});

// 切换控制台显示状态
const toggleConsole = () => {
  consoleVisible.value = !consoleVisible.value;
  editorHeight.value = consoleVisible.value ? "calc(66.7vh - 250px)" : "66.7vh";
};

// 解析答案代码块
const parseAnswerCodeBlocks = (markdown: string | undefined) => {
  if (!markdown) {
    answerMap.value = {
      java: "此题暂未提供 Java 答案",
      cpp: "此题暂未提供 C++ 答案",
      go: "此题暂未提供 Go 答案",
    };
    return;
  }

  // 初始化 answerMap
  answerMap.value = {
    java: "此题暂未提供 Java 答案",
    cpp: "此题暂未提供 C++ 答案",
    go: "此题暂未提供 Go 答案",
  };

  // 正则表达式匹配 ```language ... ``` 代码块
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

// 监听 question.answer 变化，解析代码块
watch(
  () => question.value?.answer,
  (newAnswer) => {
    parseAnswerCodeBlocks(newAnswer);
  },
  { immediate: true }
);

// 运行代码
const runCode = async () => {
  if (!question.value?.id || !form.value.code) {
    Message.error("请确保题目已加载且代码不为空");
    return;
  }
  if (!firstTestCase.value) {
    Message.error("无可用测试用例");
    return;
  }

  testRunning.value = true;
  testResult.value = null;
  consoleVisible.value = true;
  editorHeight.value = "calc(66.7vh - 250px)";
  activeConsoleTab.value = "result";

  try {
    const res = await QuestionControllerService.doQuestionRunUsingPost({
      code: form.value.code,
      language: form.value.language,
      input: firstTestCase.value.input || "",
    });

    if (res.code === 0) {
      testResult.value = res.data;
      Message.success("运行成功");
    } else {
      Message.error(`运行失败: ${res.message || "未知错误"}`);
    }
  } catch (error: any) {
    Message.error(`运行出错: ${error.message || "未知错误"}`);
  } finally {
    testRunning.value = false;
  }
};

/* ---------------------- 表格和分页配置 ---------------------- */
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
const getStatusColor = (status: any): string => {
  if (status === "pending") return "blue";
  const numericStatus = Number(status);
  const statusColors: { [key: number]: string } = {
    0: "red",
    1: "red",
    2: "green",
    3: "red",
  };
  return statusColors[numericStatus] || "gray";
};

const getStatusText = (status: any): string => {
  if (status === "pending") return "判题中";
  const numericStatus = Number(status);
  const statusTexts: { [key: number]: string } = {
    0: "错误",
    1: "错误",
    2: "通过",
    3: "错误",
  };
  return statusTexts[numericStatus] || "未知";
};

const capitalizeFirstLetter = (str: string): string => {
  if (!str) return str;
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

const formatDateTime = (date: string) =>
  moment(date).format("YYYY-MM-DD HH:mm:ss");

const formatTime = (time: number): string => {
  const hours = Math.floor(time / 3600);
  const minutes = Math.floor((time % 3600) / 60);
  const seconds = time % 60;
  return `${hours < 10 ? "0" + hours : hours}:${
    minutes < 10 ? "0" + minutes : minutes
  }:${seconds < 10 ? "0" + seconds : seconds}`;
};

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

// 格式化语言显示名称
const getLanguageDisplayName = (language: string): string => {
  const displayNames: { [key: string]: string } = {
    java: "Java",
    cpp: "C++",
    go: "Go",
  };
  return displayNames[language] || capitalizeFirstLetter(language);
};

// 格式化主题显示名称
const getThemeDisplayName = (theme: string): string => {
  const displayNames: { [key: string]: string } = {
    "vs-light": "Light",
    "vs-dark": "Dark",
  };
  return displayNames[theme] || capitalizeFirstLetter(theme);
};

/* ---------------------- 计时器逻辑 ---------------------- */
const startTimer = () => {
  showTimer.value = true;
};

const stopTimer = () => {
  showTimer.value = false;
  time.value = 0;
};

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

onUnmounted(() => {
  clearInterval(intervalId);
});

/* ---------------------- 语言和主题切换逻辑 ---------------------- */
watch(
  () => form.value.language,
  (newLanguage, oldLanguage) => {
    const newTemplate = languageTemplates[newLanguage];
    if (!newTemplate) {
      Message.error(`语言 ${getLanguageDisplayName(newLanguage)} 暂无模板`);
      return;
    }
    form.value.code = newTemplate;
    if (oldLanguage) {
      Message.success(`已切换到 ${getLanguageDisplayName(newLanguage)} 模板`);
    }
  },
  { immediate: true }
);

watch(
  () => form.value.theme,
  (newTheme, oldTheme) => {
    if (oldTheme) {
      Message.success(`已切换到 ${getThemeDisplayName(newTheme)} 主题`);
    }
  },
  { immediate: true }
);

/* ---------------------- 数据加载和提交逻辑 ---------------------- */
interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

const store = useStore();
const loginUser: LoginUserVO = computed(
  () => store.state.user?.loginUser
) as LoginUserVO;

const loadData = async () => {
  try {
    const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
      props.id as any
    );
    if (res.code === 0) {
      question.value = res.data;
    } else {
      Message.error("加载失败：" + res.message);
    }
  } catch (error) {
    Message.error("加载题目出错");
  }
};

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

const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadSubmitRecords(page);
};

const doSubmit = async () => {
  if (!question.value?.id) return;

  try {
    const res = await QuestionControllerService.doQuestionSubmitUsingPost({
      ...form.value,
      questionId: question.value.id,
    });

    if (res.code === 0) {
      Message.success("提交成功");
      activeTab.value = "submissions";

      const tempSubmission = {
        id: Date.now(),
        status: "pending",
        language: form.value.language,
        code: form.value.code,
        createTime: new Date().toISOString(),
        judgeInfo: { time: "N/A", memory: "N/A" },
        userVO: loginUser.value,
      };
      submissionRecords.value = [tempSubmission, ...submissionRecords.value];

      setTimeout(async () => {
        await loadSubmitRecords(1);
      }, 2000);
    } else {
      Message.error("提交失败，" + res.message);
    }
  } catch (error: any) {
    Message.error("提交出错: " + error.message);
  }
};

const viewSubmissionCode = (record: any) => {
  selectedSubmission.value = record;
  codeModalVisible.value = true;
};

const changeCode = (value: string) => {
  form.value.code = value;
};

/* ---------------------- 生命周期钩子 ---------------------- */
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
  padding: 16px;
}

.top-section {
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  min-height: 60px;
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
  min-height: 60px;
  display: flex;
  align-items: center;
}

.submission-info {
  margin-bottom: 16px;
}

.code-container {
  border: 1px solid #ccc;
  border-radius: 3px;
}

/* ---------------------- 控制台样式优化 ---------------------- */
.console-container {
  margin-top: 16px;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 16px;
  transition: all 0.3s ease;
  min-height: 200px;
}

.console-input {
  padding: 12px;
  background-color: #fff;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.test-case-content {
  padding: 8px;
}

.case-body {
  padding: 8px;
}

.case-item {
  margin-bottom: 12px;
}

.case-item:last-child {
  margin-bottom: 0;
}

.case-item .label {
  font-weight: 600;
  color: #555;
  margin-right: 20px;
  display: inline-block;
  text-align: center;
  width: 60px;
}

.case-item .value {
  display: inline-block;
  padding: 8px;
  background-color: #f5f7fa;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  font-family: "Courier New", Courier, monospace;
  color: #333;
  word-break: break-all;
  vertical-align: middle;
}

.console-result {
  padding: 2.5px;
  background-color: #fff;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.result-panel {
  padding: 8px;
}

.result-header {
  text-align: left;
  margin-bottom: 12px;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
}

.result-body {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.result-item {
  margin-bottom: 12px;
  display: flex;
  align-items: flex-start;
}

.result-item:last-child {
  margin-bottom: 0;
}

.result-item .label {
  font-weight: 600;
  color: #555;
  min-width: 80px;
  text-align: center;
  padding-top: 8px;
}

.result-item .value {
  font-family: "Courier New", Courier, monospace;
  color: #333;
  word-break: break-all;
  flex: 1;
  padding: 8px;
  background-color: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.running-state {
  text-align: center;
  padding: 20px;
  color: #666;
}

.running-text {
  margin-top: 8px;
  font-size: 14px;
  color: #888;
}

.empty-state {
  text-align: center;
  padding: 20px;
  color: #888;
}

.console-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 13px 0;
}

.console-footer .arco-btn-text {
  display: flex;
  align-items: center;
  color: #666;
}

.console-footer .arco-btn-text svg {
  margin-left: 5px;
}

/* ---------------------- 通用样式 ---------------------- */
.bold-text {
  font-weight: bold;
}
</style>
