<template>
  <div id="questionEditorView">
    <div class="page-title">
      <!-- 根据路由判断显示不同的标题 -->
      <template v-if="route.path.startsWith('/question/update')">
        修改题目
      </template>
      <template v-else>创建题目</template>
    </div>

    <!-- 表单组件，包含题目信息与判题配置 -->
    <a-form
      :model="form"
      size="medium"
      label-align="left"
      style="font-weight: bold; margin: 0 auto; max-width: 800px"
    >
      <!-- 题目输入 -->
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

      <!-- 题目标签 -->
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

      <!-- 题目内容 -->
      <a-form-item
        field="content"
        label="题目内容："
        tooltip="请填写题目内容"
        required
        :rules="[{ required: true, message: '题目内容是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <MdEditor :value="form.content" :handle-change="onContentChange" />
      </a-form-item>

      <!-- 题目答案 -->
      <a-form-item
        field="answer"
        label="题目答案："
        tooltip="请填写题目答案"
        required
        :rules="[{ required: true, message: '题目答案是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <MdEditor :value="form.answer" :handle-change="onAnswerChange" />
      </a-form-item>

      <!-- 判题配置 -->
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

      <!-- 测试用例配置 -->
      <a-divider :margin="20" />
      <a-form-item
        label="测试用例配置："
        tooltip="请填写测试用例配置"
        :content-flex="false"
        :merge-props="false"
        required
        :rules="[{ required: true, message: '测试用例配置是必填项！' }]"
        style="margin-bottom: 20px"
      >
        <!-- 动态生成测试用例表单项 -->
        <a-form-item
          v-for="(judgeCaseItem, index) of form.judgeCase"
          :key="index"
          no-style
        >
          <a-space
            direction="vertical"
            style="width: 100%; margin-bottom: 10px"
          >
            <!-- 输入用例 -->
            <a-form-item
              :field="`form.judgeCase[${index}].input`"
              :label="`第${index + 1}个输入用例:`"
              tooltip="请输入测试输入用例（例如：输入数据）"
              style="margin-bottom: 10px"
            >
              <a-input
                v-model="judgeCaseItem.input"
                placeholder="请输入测试输入用例"
                style="width: 100%"
              />
            </a-form-item>

            <!-- 输出用例 -->
            <a-form-item
              :field="`form.judgeCase[${index}].output`"
              :label="`第${index + 1}个输出用例:`"
              tooltip="请输入测试输出用例（例如：预期结果）"
              style="margin-bottom: 10px"
            >
              <a-input
                v-model="judgeCaseItem.output"
                placeholder="请输入测试输出用例"
                style="width: 100%"
              />
            </a-form-item>
          </a-space>

          <!-- 删除测试用例按钮 -->
          <a-space direction="vertical" size="large">
            <a-button
              type="outline"
              status="danger"
              @click="handleDelete(index)"
              shape="round"
              class="bold-text"
            >
              删除用例
            </a-button>
          </a-space>
        </a-form-item>

        <!-- 新增测试用例按钮 -->
        <div style="margin-top: 10px">
          <a-button
            @click="handleAdd"
            type="outline"
            status="success"
            shape="round"
            class="bold-text"
            style="width: 100%"
          >
            新增测试用例
          </a-button>
        </div>
      </a-form-item>

      <!-- 提交按钮 -->
      <a-divider :margin="20" />
      <a-form-item>
        <a-button
          type="primary"
          style="width: 100%; min-width: 150px; margin-top: 20px"
          shape="round"
          @click="doSubmit"
          class="bold-text"
        >
          {{ updatePage ? "提交并更新" : "提交并创建" }}
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService } from "../../../backendAPI/index";
import message from "@arco-design/web-vue/es/message";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();
// 判断是否为更新页面
const updatePage = route.path.includes("update");

// 定义表单模型
let form = ref({
  title: "",
  tags: [],
  answer: "",
  content: "",
  judgeConfig: {
    memoryLimit: 1000,
    stackLimit: 1000,
    timeLimit: 1000,
  },
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
});

// 定义标签选项
const tagOptions = [
  { value: "栈", label: "栈", color: "blue" },
  { value: "链表", label: "链表", color: "pink" },
  { value: "二叉树", label: "二叉树", color: "teal" },
  { value: "图", label: "图", color: "purple" },
  { value: "动态规划", label: "动态规划", color: "cyan" },
  { value: "简单", label: "简单", color: "green" },
  { value: "中等", label: "中等", color: "orange" },
  { value: "困难", label: "困难", color: "red" },
];

/**
 * 加载编辑页面数据
 */
const loadData = async () => {
  const id = route.query.id;
  if (!id) {
    return;
  }
  const res = await QuestionControllerService.getQuestionByIdUsingGet(
    id as any
  );
  if (res.code === 0) {
    form.value = res.data as any;

    // 处理 judgeCase 和 judgeConfig 的初始化
    form.value.judgeCase = form.value.judgeCase
      ? JSON.parse(form.value.judgeCase as any)
      : [{ input: "", output: "" }];
    form.value.judgeConfig = form.value.judgeConfig
      ? JSON.parse(form.value.judgeConfig as any)
      : {
          memoryLimit: 1000,
          stackLimit: 1000,
          timeLimit: 1000,
        };
    form.value.tags = form.value.tags ? JSON.parse(form.value.tags as any) : [];
  } else {
    message.error("加载失败，" + res.message);
  }
};

onMounted(() => {
  loadData();
});

/**
 * 提交表单数据
 */
const doSubmit = async () => {
  if (updatePage) {
    const res = await QuestionControllerService.updateQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("更新成功");
      await router.push({ path: "/question/manage", replace: true });
    } else {
      message.error("更新失败，" + res.message);
    }
  } else {
    const res = await QuestionControllerService.addQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("创建成功");
      // 创建成功后清空表单
      form.value = {
        title: "",
        tags: [],
        answer: "",
        content: "",
        judgeConfig: { memoryLimit: 1000, stackLimit: 1000, timeLimit: 1000 },
        judgeCase: [{ input: "", output: "" }],
      };
    } else {
      message.error("创建失败，" + res.message);
    }
  }
};

/**
 * 新增判题用例
 */
const handleAdd = () => {
  form.value.judgeCase.push({ input: "", output: "" });
};

/**
 * 删除判题用例
 */
const handleDelete = (index: number) => {
  form.value.judgeCase.splice(index, 1);
};

const onContentChange = (value: string) => {
  form.value.content = value;
};

const onAnswerChange = (value: string) => {
  form.value.answer = value;
};
</script>

<style scoped>
#questionEditorView {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  border-radius: 12px;
  background: #f8f9fa;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 32px;
  text-align: center;
  font-weight: bold;
  margin-bottom: 20px;
}

:deep(.bytemd-fullscreen.bytemd) {
  z-index: 100;
}
</style>
