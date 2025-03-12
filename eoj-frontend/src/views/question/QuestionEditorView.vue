<template>
  <div id="questionEditorView">
    <!-- 页面标题：根据页面类型显示“创建题目”或“修改题目” -->
    <div class="page-title nowrap">
      {{ updatePage ? "修改题目" : "创建题目" }}
    </div>

    <!-- 步骤导航：使用 a-steps 显示五个步骤 -->
    <a-steps :current="currentStep" style="margin: 20px 0; max-width: 1000px">
      <a-step
        v-for="(step, index) in steps"
        :key="index"
        :title="step.title"
        :description="step.description"
        :status="getStepStatus(index)"
      >
        <template #icon>
          <component :is="step.icon" />
        </template>
      </a-step>
    </a-steps>

    <!-- 步骤 0：基本信息表单 -->
    <a-form
      :model="form"
      size="medium"
      label-align="left"
      style="font-weight: bold; margin: 0 auto; max-width: 1000px"
      v-show="currentStep === 0"
    >
      <!-- 题目名称输入 -->
      <a-form-item
        field="title"
        label="题目名称："
        tooltip="请填写题目名称"
        required
        :rules="[{ required: true, message: '题目名称是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <a-input
          v-model="form.title"
          placeholder="请输入题目名称"
          style="max-width: 100%"
        />
      </a-form-item>

      <!-- 题目标签选择 -->
      <a-form-item
        field="tags"
        label="题目标签："
        tooltip="请选择题目标签"
        required
        :rules="[{ required: true, message: '题目标签是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <a-select
          v-model="form.tags"
          placeholder="选择题目标签"
          multiple
          allow-clear
          style="max-width: 100%"
        >
          <a-option
            v-for="(tag, index) in tagOptions"
            :key="index"
            :value="tag.value"
          >
            <a-tag :color="tag.color" class="bold-text">{{ tag.label }}</a-tag>
          </a-option>
        </a-select>
      </a-form-item>

      <!-- 题目难度选择 -->
      <a-form-item
        field="difficulty"
        label="题目难度："
        tooltip="请选择题目难度"
        required
        :rules="[{ required: true, message: '题目难度是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <a-select
          v-model="form.difficulty"
          placeholder="选择题目难度"
          style="max-width: 100%"
        >
          <a-option
            v-for="(difficulty, index) in difficultyOptions"
            :key="index"
            :value="difficulty.value"
          >
            <a-tag :color="difficulty.color" class="bold-text">
              {{ difficulty.label }}
            </a-tag>
          </a-option>
        </a-select>
      </a-form-item>
    </a-form>

    <!-- 步骤 1：题目描述表单 -->
    <a-form
      :model="form"
      size="medium"
      label-align="left"
      style="font-weight: bold; margin: 0 auto; max-width: 1000px"
      v-show="currentStep === 1"
    >
      <a-form-item
        field="content"
        label="题目内容："
        tooltip="请填写题目内容"
        required
        :rules="[{ required: true, message: '题目内容是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <MdEditor
          :value="form.content"
          :handle-change="onContentChange"
          style="width: 100%; min-height: 400px"
          ref="contentEditorRef"
        />
      </a-form-item>
    </a-form>

    <!-- 步骤 2：题目答案表单 -->
    <a-form
      :model="form"
      size="medium"
      label-align="left"
      style="font-weight: bold; margin: 0 auto; max-width: 1000px"
      v-show="currentStep === 2"
    >
      <a-form-item
        field="answer"
        label="题目答案："
        tooltip="请填写题目答案"
        required
        :rules="[{ required: true, message: '题目答案是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <MdEditor
          :value="form.answer"
          :handle-change="onAnswerChange"
          style="width: 100%; min-height: 400px"
          ref="answerEditorRef"
        />
      </a-form-item>
    </a-form>

    <!-- 步骤 3：判题配置表单 -->
    <a-form
      :model="form"
      size="medium"
      label-align="left"
      style="font-weight: bold; margin: 0 auto; max-width: 1000px"
      v-show="currentStep === 3"
    >
      <a-divider :margin="20" />
      <a-form-item
        label="判题配置："
        tooltip="请填写判题配置"
        :content-flex="false"
        :merge-props="false"
        required
        :rules="[{ required: true, message: '判题配置是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <a-space direction="vertical" style="min-width: 100%">
          <!-- 时间限制 -->
          <a-form-item
            field="judgeConfig.timeLimit"
            label="时间限制："
            tooltip="请输入时间限制（单位：ms）"
            style="margin-bottom: 10px"
          >
            <a-input-number
              v-model="form.judgeConfig.timeLimit"
              placeholder="请输入时间限制"
              mode="button"
              min="0"
              size="large"
              style="width: 100%"
            />
          </a-form-item>

          <!-- 内存限制 -->
          <a-form-item
            field="judgeConfig.memoryLimit"
            label="内存限制："
            tooltip="请输入内存限制（单位：KB）"
            style="margin-bottom: 10px"
          >
            <a-input-number
              v-model="form.judgeConfig.memoryLimit"
              placeholder="请输入内存限制"
              mode="button"
              min="0"
              size="large"
              style="width: 100%"
            />
          </a-form-item>

          <!-- 堆栈限制 -->
          <a-form-item
            field="judgeConfig.stackLimit"
            label="堆栈限制："
            tooltip="请输入堆栈限制（单位：KB）"
            style="margin-bottom: 10px"
          >
            <a-input-number
              v-model="form.judgeConfig.stackLimit"
              placeholder="请输入堆栈限制"
              mode="button"
              min="0"
              size="large"
              style="width: 100%"
            />
          </a-form-item>
        </a-space>
      </a-form-item>
    </a-form>

    <!-- 步骤 4：判题用例配置 -->
    <a-form
      :model="form"
      size="medium"
      label-align="left"
      style="font-weight: bold; margin: 0 auto; max-width: 1000px"
      v-show="currentStep === 4"
    >
      <a-divider :margin="10" />
      <a-form-item
        label="判题用例配置："
        tooltip="请填写判题用例配置"
        :content-flex="false"
        :merge-props="false"
        required
        :rules="[{ required: true, message: '判题用例配置是必填项！' }]"
        style="margin-bottom: 10px"
      >
        <!-- 判题用例表格 -->
        <a-table
          :data="form.judgeCase"
          :bordered="false"
          class="judge-case-table"
          :pagination="false"
        >
          <template #columns>
            <a-table-column title="输入用例" data-index="input" align="center">
              <template #cell="{ record }">
                <a-input
                  v-model="record.input"
                  placeholder="请输入测试的输入用例"
                  style="width: 100%"
                />
              </template>
            </a-table-column>
            <a-table-column title="输出用例" data-index="output" align="center">
              <template #cell="{ record }">
                <a-input
                  v-model="record.output"
                  placeholder="请输入测试的输出用例"
                  style="width: 100%"
                />
              </template>
            </a-table-column>
            <a-table-column title="操作" width="60px" align="center">
              <template #cell="{ rowIndex }">
                <a-button
                  type="text"
                  status="danger"
                  @click="handleDeleteJudgeCase(rowIndex)"
                  shape="circle"
                  class="delete-button"
                >
                  <icon-delete size="20" />
                </a-button>
              </template>
            </a-table-column>
          </template>
        </a-table>

        <!-- 新增测试用例按钮 -->
        <div style="margin-top: 10px">
          <a-button
            @click="handleAddJudgeCase"
            type="outline"
            status="success"
            shape="round"
            style="width: 100%; min-width: 150px"
            class="add-button"
          >
            + 新增测试用例
          </a-button>
        </div>
      </a-form-item>
    </a-form>

    <!-- 步骤导航按钮 -->
    <div style="margin-top: 20px; text-align: center">
      <a-space>
        <a-button
          v-if="currentStep > 0"
          @click="prevStep"
          shape="round"
          style="min-width: 100px"
        >
          上一步
        </a-button>
        <a-button
          v-if="currentStep < steps.length - 1"
          type="primary"
          @click="nextStep"
          shape="round"
          style="min-width: 100px"
        >
          下一步
        </a-button>
        <a-button
          v-if="currentStep === steps.length - 1"
          type="primary"
          @click="submitForm"
          shape="round"
          style="min-width: 100px"
        >
          {{ updatePage ? "提交并更新" : "提交并创建" }}
        </a-button>
      </a-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, nextTick, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { QuestionControllerService } from "../../../backendAPI/index";
import message from "@arco-design/web-vue/es/message";
import MdEditor from "@/components/MdEditor.vue";
import {
  IconHome,
  IconFile,
  IconCheckCircle,
  IconSettings,
  IconList,
  IconDelete,
} from "@arco-design/web-vue/es/icon";

// --- 类型定义 ---

/** 判题配置接口 */
interface JudgeConfig {
  timeLimit: number;
  memoryLimit: number;
  stackLimit: number;
}

/** 判题用例接口 */
interface JudgeCase {
  input: string;
  output: string;
}

/** 题目表单数据接口 */
interface QuestionForm {
  id: string | null;
  title: string;
  tags: string[];
  answer: string;
  content: string;
  difficulty: number | null;
  judgeConfig: JudgeConfig;
  judgeCase: JudgeCase[];
}

/** 标签选项接口 */
interface TagOption {
  value: string;
  label: string;
  color: string;
}

/** 难度选项接口 */
interface DifficultyOption {
  value: number;
  label: string;
  color: string;
}

/** 步骤导航配置接口 */
interface Step {
  title: string;
  description: string;
  icon: any;
}

// --- 状态定义 ---

// 当前步骤（0-4）
const currentStep = ref(0);

// 路由实例
const router = useRouter();
const route = useRoute();

// 是否为更新页面
const updatePage = ref(route.path.includes("update"));

// Markdown 编辑器引用，用于强制刷新
const contentEditorRef = ref(null);
const answerEditorRef = ref(null);

// 表单数据
const form = ref<QuestionForm>({
  id: null,
  title: "",
  tags: [],
  answer: "",
  content: "",
  difficulty: null,
  judgeConfig: {
    timeLimit: 1000,
    memoryLimit: 1000,
    stackLimit: 1000,
  },
  judgeCase: [{ input: "", output: "" }],
});

// 标签选项
const tagOptions: TagOption[] = [
  { value: "栈", label: "栈", color: "darkslateblue" },
  { value: "图", label: "图", color: "darkseagreen" },
  { value: "数组", label: "数组", color: "darkgoldenrod" },
  { value: "链表", label: "链表", color: "darkmagenta" },
  { value: "排序", label: "排序", color: "darkorange" },
  { value: "哈希表", label: "哈希表", color: "salmon" },
  { value: "字符串", label: "字符串", color: "darkkhaki" },
  { value: "二叉树", label: "二叉树", color: "teal" },
  { value: "双指针", label: "双指针", color: "steelblue" },
  { value: "动态规划", label: "动态规划", color: "chocolate" },
  { value: "滑动窗口", label: "滑动窗口", color: "indigo" },
];

// 难度选项
const difficultyOptions: DifficultyOption[] = [
  { value: 0, label: "简单", color: "green" },
  { value: 1, label: "中等", color: "orange" },
  { value: 2, label: "困难", color: "red" },
];

// 步骤导航配置
const steps: Step[] = [
  {
    title: "基本信息",
    description: "输入题目名称、标签和难度",
    icon: IconHome,
  },
  { title: "题目描述", description: "填写题目内容", icon: IconFile },
  { title: "题目答案", description: "填写题目答案", icon: IconCheckCircle },
  {
    title: "判题配置",
    description: "设置时间、内存和堆栈限制",
    icon: IconSettings,
  },
  { title: "判题用例", description: "添加输入输出用例", icon: IconList },
];

// --- 工具函数 ---

/**
 * 获取步骤状态
 * @param index 步骤索引
 * @returns 步骤状态（"finish", "process", "wait"）
 */
const getStepStatus = (index: number): string => {
  if (currentStep.value > index) return "finish";
  if (currentStep.value === index) return "process";
  return "wait";
};

/**
 * 强制刷新 Markdown 编辑器内容
 * 用于解决编辑器内容在切换步骤时可能不渲染的问题
 */
const forceRefreshEditors = () => {
  nextTick(() => {
    if (currentStep.value === 1 && contentEditorRef.value) {
      const tempContent = form.value.content;
      form.value.content = "";
      nextTick(() => {
        form.value.content = tempContent;
      });
    } else if (currentStep.value === 2 && answerEditorRef.value) {
      const tempAnswer = form.value.answer;
      form.value.answer = "";
      nextTick(() => {
        form.value.answer = tempAnswer;
      });
    }
  });
};

// --- 数据加载 ---

/**
 * 加载题目数据（用于编辑模式）
 * 从后端获取题目详情并填充表单
 */
const loadData = async () => {
  const id = route.query.id;
  if (!id) return;

  try {
    const res = await QuestionControllerService.getQuestionByIdUsingGet(
      id as any
    );
    if (res.code === 0 && res.data) {
      form.value = {
        ...form.value,
        id: res.data.id || null,
        title: res.data.title || "",
        tags:
          typeof res.data.tags === "string"
            ? JSON.parse(res.data.tags)
            : res.data.tags || [],
        content: res.data.content || "",
        answer: res.data.answer || "",
        difficulty:
          res.data.difficulty !== undefined ? res.data.difficulty : null,
        judgeConfig:
          typeof res.data.judgeConfig === "string"
            ? JSON.parse(res.data.judgeConfig)
            : res.data.judgeConfig || {
                timeLimit: 1000,
                memoryLimit: 1000,
                stackLimit: 1000,
              },
        judgeCase:
          typeof res.data.judgeCase === "string"
            ? JSON.parse(res.data.judgeCase)
            : res.data.judgeCase || [{ input: "", output: "" }],
      };
      setTimeout(() => forceRefreshEditors(), 100);
    } else {
      message.error("加载失败，" + (res.message || "未知错误"));
    }
  } catch (error) {
    message.error("加载数据时发生错误：" + (error as Error).message);
  }
};

// --- 事件处理 ---

/**
 * 处理题目内容变化
 * @param value 新的内容
 */
const onContentChange = (value: string) => {
  form.value.content = value;
};

/**
 * 处理题目答案变化
 * @param value 新的答案
 */
const onAnswerChange = (value: string) => {
  form.value.answer = value;
};

/**
 * 新增判题用例
 */
const handleAddJudgeCase = () => {
  form.value.judgeCase.push({ input: "", output: "" });
};

/**
 * 删除判题用例
 * @param index 要删除的用例索引
 */
const handleDeleteJudgeCase = (index: number) => {
  form.value.judgeCase.splice(index, 1);
};

/**
 * 切换到上一步
 */
const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
};

/**
 * 切换到下一步
 */
const nextStep = () => {
  if (currentStep.value < steps.length - 1) {
    currentStep.value++;
  }
};

/**
 * 提交表单数据
 * 根据 updatePage 决定是创建还是更新题目
 */
const submitForm = async () => {
  const submitForm = {
    ...form.value,
    difficulty: form.value.difficulty,
    judgeCase: form.value.judgeCase,
    judgeConfig: form.value.judgeConfig,
    tags: form.value.tags,
  };

  if (updatePage.value) {
    if (!submitForm.id) {
      message.error("更新失败：缺少题目 ID");
      return;
    }
    const res = await QuestionControllerService.updateQuestionUsingPost(
      submitForm
    );
    if (res.code === 0) {
      message.success("更新成功");
      await router.push({ path: "/question/manage", replace: true });
    } else {
      message.error("更新失败，" + res.message);
    }
  } else {
    const { id, ...createForm } = submitForm;
    const res = await QuestionControllerService.addQuestionUsingPost(
      createForm
    );
    if (res.code === 0) {
      message.success("创建成功");
      await router.push({ path: "/question/manage", replace: true });
      form.value = {
        id: null,
        title: "",
        tags: [],
        answer: "",
        content: "",
        difficulty: null,
        judgeConfig: { timeLimit: 1000, memoryLimit: 1000, stackLimit: 1000 },
        judgeCase: [{ input: "", output: "" }],
      };
    } else {
      message.error("创建失败，" + res.message);
    }
  }
};

// --- 生命周期和监听 ---

// 组件挂载时加载数据
onMounted(() => {
  loadData();
});

// 监听步骤变化，切换到编辑器步骤时刷新编辑器
watch(currentStep, (newVal) => {
  if (newVal === 1 || newVal === 2) {
    setTimeout(() => forceRefreshEditors(), 100);
  }
});
</script>

<style scoped>
/* 页面容器样式 */
#questionEditorView {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  border-radius: 12px;
  background: #f8f9fa;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 页面标题样式 */
.page-title {
  font-size: 32px;
  text-align: center;
  font-weight: bold;
  margin-bottom: 20px;
}

/* 防止标题换行 */
.nowrap {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 判题用例表格样式 */
.judge-case-table {
  border: 2px solid #f8f9fa;
  background: #f7f9fc;
}

/* 表格头样式 */
:deep(.arco-table-th) {
  background-color: #f0f2f5;
  font-weight: bold;
  color: #303133;
  border-bottom: 1px solid #e8e8e8;
}

/* 表格单元格样式 */
:deep(.arco-table-td) {
  background-color: #f7f9fc;
  border-bottom: 1px solid #e8e8e8;
  padding: 8px !important;
}

/* 输入框样式 */
:deep(.arco-input) {
  background-color: #ffffff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  transition: border-color 0.3s ease;
}

/* 删除按钮样式 */
.delete-button {
  margin-left: 10px;
  transition: transform 0.2s ease;
}

.delete-button:hover {
  transform: scale(1.2);
}

/* 新增按钮样式 */
.add-button {
  border-color: #52c41a;
  color: #52c41a;
  font-weight: bold;
  transition: all 0.3s ease;
}

.add-button:hover {
  background-color: rgba(82, 196, 26, 0.1);
  color: #73d13d;
}

/* Markdown 编辑器样式 */
:deep(.bytemd) {
  width: 100%;
  min-height: 400px;
}

/* 全屏编辑器样式 */
:deep(.bytemd-fullscreen.bytemd) {
  z-index: 100;
}

/* 步骤导航样式 */
:deep(.arco-steps) {
  max-width: 1000px;
  margin: 0 auto;
}

/* 步骤项样式 */
:deep(.arco-step) {
  flex: 1;
  text-align: center;
}
</style>
