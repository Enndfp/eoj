<template>
  <div id="questionPracticeView">
    <a-row :gutter="[24, 24]">
      <!-- 题目信息部分 -->
      <a-col :md="12" :xs="24">
        <a-tabs default-active-key="question">
          <a-tab-pane key="question" title="题目">
            <a-card v-if="question" :title="question.title">
              <!-- 题目内容展示 -->
              <MdViewer :value="question.content || ''"></MdViewer>

              <!-- 判题用例展示 -->
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

              <!-- 判题条件展示 -->
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

          <!-- 评论区 -->
          <a-tab-pane key="comment" title="评论" disabled> 评论区</a-tab-pane>

          <!-- 答案区 -->
          <a-tab-pane key="answer" title="答案" disabled>
            暂时无法查看答案
          </a-tab-pane>
        </a-tabs>
      </a-col>

      <!-- 提交代码部分 -->
      <a-col :md="12" :xs="24">
        <a-card>
          <!-- 编程语言选择和主题选择放一起，并增加间隔 -->
          <a-row :gutter="24">
            <a-col :span="4">
              <a-form-item size="small">
                <!-- 计时器 -->
                <div class="startTimer" v-if="!showTimer" @click="startTimer">
                  <a-tooltip position="top" content="开始计时" mini>
                    <IconClockCircle size="30" style="margin-left: 28px" />
                  </a-tooltip>
                </div>
                <!-- 停止计时器按钮，显示在计时器后面 -->
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
  </div>
</template>

<script setup lang="ts">
import {
  defineProps,
  onMounted,
  onUnmounted,
  ref,
  watch,
  withDefaults,
} from "vue";
import {
  QuestionControllerService,
  QuestionSubmitAddRequest,
  QuestionVO,
} from "../../../backendAPI";
import { Message } from "@arco-design/web-vue";
import { useRouter } from "vue-router";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";

const router = useRouter();
const question = ref<QuestionVO>();
const form = ref<QuestionSubmitAddRequest>({
  language: "java", // 默认设置为Java
  code: `public class Main {
    public static void main(String[] args) {
        // 编写解题函数
    }
}
  `, // Java 默认代码模板
  theme: "vs-light", // 默认主题为 Light
});

const showTimer = ref(false);
const time = ref(0);
let intervalId: any = null;

const formatTime = (time: number): string => {
  const hours = Math.floor(time / 3600);
  const minutes = Math.floor((time % 3600) / 60);
  const seconds = time % 60;

  return `${hours < 10 ? "0" + hours : hours}:${
    minutes < 10 ? "0" + minutes : minutes
  }:${seconds < 10 ? "0" + seconds : seconds}`;
};

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

interface Props {
  id: string;
}

// 获取题目标签颜色
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

// 获取题目难度颜色
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

// 获取题目难度文本
const getDifficultyText = (difficulty: number) => {
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

// 提交代码
const doSubmit = async () => {
  if (!question.value?.id) return;
  const res = await QuestionControllerService.doQuestionSubmitUsingPost({
    ...form.value,
    questionId: question.value.id,
  });
  if (res.code === 0) {
    Message.success("提交成功");
  } else {
    Message.error("提交失败，" + res.message);
  }
};

// 加载题目信息
const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

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

onMounted(() => {
  loadData();
});

// 代码编辑器内容变更
const changeCode = (value: string) => {
  form.value.code = value;
};
</script>

<style scoped>
#questionPracticeView {
  max-width: 1600px;
  margin: 0 auto;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
}
</style>
