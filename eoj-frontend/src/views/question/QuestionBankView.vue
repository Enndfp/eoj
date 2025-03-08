<template>
  <div id="questionBankView">
    <!-- 🔹 搜索表单 -->
    <a-form :model="searchParams" layout="inline" class="search-form bold-text">
      <!-- 题目搜索 -->
      <a-form-item field="title" label="题目：" tooltip="请输入要搜索的题目">
        <a-input v-model="searchParams.title" placeholder="请输入题目" />
      </a-form-item>

      <!-- 标签筛选 -->
      <a-form-item field="tags" label="标签：" tooltip="请选择要搜索的标签">
        <a-select
          v-model="searchParams.tags"
          placeholder="选择标签"
          multiple
          allow-clear
          style="min-width: 150px"
        >
          <!-- 选择不同的标签 -->
          <a-option v-for="tag in tags" :key="tag.value" :value="tag.value">
            <a-tag :color="tag.color" class="bold-text">{{ tag.name }}</a-tag>
          </a-option>
        </a-select>
      </a-form-item>

      <!-- 难度筛选 -->
      <a-form-item
        field="difficulty"
        label="难度："
        tooltip="请选择要搜索的难度"
      >
        <a-select
          v-model="searchParams.difficulty"
          placeholder="选择难度"
          style="min-width: 150px"
        >
          <!-- 难度选项 -->
          <a-option
            v-for="difficulty in difficulties"
            :key="difficulty.value"
            :value="difficulty.value"
          >
            <a-tag :color="difficulty.color" class="bold-text"
              >{{ difficulty.name }}
            </a-tag>
          </a-option>
        </a-select>
      </a-form-item>

      <!-- 搜索按钮 -->
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

      <!-- 重置按钮 -->
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
    </a-form>

    <a-divider :size="0" />

    <!-- 🔹 题目表格 -->
    <a-table
      column-resizable
      wrapper
      :ref="tableRef"
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total: Number(total),
        showJumper: true,
        showPageSize: true,
      }"
      @page-change="onPageChange"
      @pageSizeChange="onPageSizeChange"
    >
      <!-- 题目状态 -->
      <template #status="{ record }">
        <a-tooltip :content="getStatusTooltip({ questionId: record.id })">
          <div class="status-icon">
            <icon-check-circle-fill
              v-if="isQuestionPassed({ questionId: record.id })"
              style="color: #00b42a; font-size: 20px"
            />
            <icon-close-circle-fill
              v-else-if="hasAttempted({ questionId: record.id })"
              style="color: #f7ba1e; font-size: 20px"
            />
            <icon-minus-circle-fill
              v-else
              style="color: #86909c; font-size: 20px"
            />
          </div>
        </a-tooltip>
      </template>

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

      <!-- 🔹 题目标签 -->
      <template #tags="{ record }">
        <a-space wrap>
          <a-tag
            size="medium"
            v-for="(tag, index) of record.tags"
            :key="index"
            :color="getTagColor(tag)"
          >
            {{ tag }}
          </a-tag>
        </a-space>
      </template>

      <!-- 🔹 通过率 -->
      <template #acceptedRate="{ record }">
        <span>
          {{
            Math.round(
              (record.submitNum > 0
                ? (record.acceptedNum / record.submitNum) * 100
                : 0) * 100
            ) / 100
          }}%
        </span>
      </template>

      <!-- 🔹 题目难度 -->
      <template #difficulty="{ record }">
        <a-tag :color="getDifficultyColor(record.difficulty)" class="bold-text">
          {{ getDifficultyText(record.difficulty) }}
        </a-tag>
      </template>

      <!-- 🔹 操作列 -->
      <template #optional="{ record }">
        <a-space>
          <a-tooltip content="去做题">
            <a-button
              shape="circle"
              status="normal"
              type="primary"
              @click="toQuestionPage(record.id)"
            >
              <icon-edit class="bold-text" />
            </a-button>
          </a-tooltip>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watchEffect } from "vue";
import {
  LoginUserVO,
  QuestionControllerService,
  QuestionQueryRequest,
} from "../../../backendAPI";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import {
  IconCheckCircleFill,
  IconCloseCircleFill,
  IconMinusCircleFill,
} from "@arco-design/web-vue/es/icon";
import { useStore } from "vuex";

// 🔹 定义表格数据和分页信息
const tableRef = ref();
const dataList = ref([]);
const total = ref(0);

// 🔹 提交记录相关数据
const submissions = ref([]); // 存储用户提交记录
const submissionLoading = ref(false); // 加载状态

// 获取登录用户信息
const store = useStore();
const loginUser: LoginUserVO = computed(
  () => store.state.user?.loginUser
) as LoginUserVO;

// 🔹 定义搜索参数
const searchParams = ref<QuestionQueryRequest>({
  title: "",
  tags: [],
  difficulty: undefined,
  pageSize: 10,
  current: 1,
});

// 🔹 加载用户提交记录
const loadUserSubmissions = async () => {
  submissionLoading.value = true;
  try {
    // 判断 userId 是否为空
    if (!loginUser.value.id) {
      submissions.value = [];
      return;
    }

    // 获取当前页面所有题目 ID
    const questionIds = dataList.value.map((question) => question.id);
    if (questionIds.length === 0) {
      submissions.value = [];
      return;
    }

    // 调用批量查询接口
    const res =
      await QuestionControllerService.listQuestionSubmitVoByQuestionIdsUsingPost(
        {
          userId: loginUser.value.id,
          questionIds: questionIds,
        }
      );

    if (res.code === 0) {
      submissions.value = res.data || [];
    } else {
      message.error("加载提交记录失败，" + res.message);
    }
  } catch (error) {
    message.error("获取提交记录出错");
  } finally {
    submissionLoading.value = false;
  }
};

// 🔹 在加载题目后再加载提交记录
const loadData = async () => {
  const res = await QuestionControllerService.listQuestionVoByPageUsingPost({
    ...searchParams.value,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
    await loadUserSubmissions();
  } else {
    message.error("加载失败，" + res.message);
  }
};

// 🔹 判断题目是否已通过
const isQuestionPassed = ({ questionId }: { questionId: any }) => {
  return submissions.value.some((submission) => {
    // 判断是否有同一题目的提交记录，且状态为成功，且judgeInfo中message为Accepted
    return (
      submission.questionId === questionId &&
      submission.status === 2 && // 成功状态
      submission.judgeInfo &&
      submission.judgeInfo.message === "Accepted"
    );
  });
};

// 🔹 判断题目是否尝试过（提交过但未通过）
const hasAttempted = ({ questionId }: { questionId: any }) => {
  return submissions.value.some((submission) => {
    return (
      submission.questionId === questionId &&
      (!submission.judgeInfo || submission.judgeInfo.message !== "Accepted")
    );
  });
};

// 🔹 获取状态提示
const getStatusTooltip = ({ questionId }: { questionId: any }) => {
  if (isQuestionPassed({ questionId: questionId })) {
    return "已通过";
  } else if (hasAttempted({ questionId: questionId })) {
    return "尝试过";
  } else {
    return "未尝试";
  }
};

// 🔹 重置筛选条件
const resetFilters = () => {
  searchParams.value = {
    title: "",
    tags: [],
    difficulty: undefined,
    pageSize: 10,
    current: 1,
  };
  loadData();
};

// 🔹 监听加载数据
watchEffect(() => {
  loadData();
});

onMounted(() => {
  loadData();
});

// 🔹 配置标签和难度
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

const difficulties = [
  { value: 0, name: "简单", color: "green" },
  { value: 1, name: "中等", color: "orange" },
  { value: 2, name: "困难", color: "red" },
];

// 🔹 获取题目难度的文本
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

// 🔹 获取题目难度颜色
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

// 🔹 获取标签颜色
const getTagColor = (tag: string) => {
  switch (tag) {
    case "栈":
      return "darkslateblue";
    case "图":
      return "darkseagreen";
    case "数组":
      return "darkgoldenrod";
    case "链表":
      return "darkmagenta";
    case "排序":
      return "darkorange";
    case "哈希表":
      return "salmon";
    case "字符串":
      return "darkkhaki";
    case "二叉树":
      return "teal";
    case "双指针":
      return "steelblue";
    case "动态规划":
      return "chocolate";
    case "滑动窗口":
      return "indigo";
    default:
      return "gray";
  }
};

// 添加状态列作为第一列
const columns = [
  {
    title: "状态",
    slotName: "status",
    align: "center",
    width: 80,
  },
  {
    title: "题目",
    slotName: "title",
    align: "center",
  },
  {
    title: "标签",
    slotName: "tags",
    align: "center",
  },
  {
    title: "通过率",
    slotName: "acceptedRate",
    align: "center",
  },
  {
    title: "难度",
    slotName: "difficulty",
    align: "center",
  },
  {
    title: "操作",
    slotName: "optional",
    align: "center",
  },
];

// 🔹 页码变更
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

// 🔹 页大小变更
const onPageSizeChange = (size: number) => {
  searchParams.value = {
    ...searchParams.value,
    pageSize: size,
  };
};

// 🔹 跳转到做题页面
const router = useRouter();
/* 🔹 页面跳转 */
const toQuestionPage = (questionId: string) => {
  router.push({ path: `/question/practice/${questionId}` });
};

// 🔹 提交搜索
const doSubmit = () => {
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
};
</script>

<style scoped>
#questionBankView {
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

:deep(.arco-btn-icon) {
  font-size: 16px;
}

.status-icon {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
