import { RouteRecordRaw } from "vue-router";
import NoAccessView from "@/views/state/NoAccessView.vue";
import AccessEnum from "@/access/accessEnum";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import QuestionEditorView from "@/views/question/QuestionEditorView.vue";
import QuestionManageView from "@/views/question/QuestionManageView.vue";
import QuestionBankView from "@/views/question/QuestionBankView.vue";
import QuestionPracticeView from "@/views/question/QuestionPracticeView.vue";
import QuestionSubmitView from "@/views/question/QuestionSubmitView.vue";
import UserInfoView from "@/views/user/UserInfoView.vue";
import NotFoundView from "@/views/state/NotFoundView.vue";
import UserManageView from "@/views/user/UserManageView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    meta: {
      hideInMenu: true,
    },
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: UserLoginView,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterView,
      },
      {
        path: "/user/info",
        name: "用户信息",
        component: UserInfoView,
      },
    ],
  },
  {
    path: "/",
    name: "题库",
    component: QuestionBankView,
  },
  {
    path: "/question_submit",
    name: "提交记录",
    component: QuestionSubmitView,
    meta: {
      access: AccessEnum.USER,
    },
  },
  {
    path: "/question/practice/:id",
    name: "在线做题",
    props: true,
    component: QuestionPracticeView,
    meta: {
      hideInMenu: true,
      access: AccessEnum.USER,
    },
  },
  {
    path: "/question/add",
    name: "创建题目",
    component: QuestionEditorView,
    meta: {
      access: AccessEnum.ADMIN,
    },
  },
  {
    path: "/question/update",
    name: "更新题目",
    component: QuestionEditorView,
    meta: {
      hideInMenu: true,
      access: AccessEnum.USER,
    },
  },
  {
    path: "/question/manage",
    name: "题目管理",
    component: QuestionManageView,
    meta: {
      access: AccessEnum.ADMIN,
    },
  },
  {
    path: "/manage/user",
    name: "用户管理",
    component: UserManageView,
    meta: {
      access: AccessEnum.ADMIN,
    },
  },
  {
    path: "/noAccess",
    name: "无权限",
    component: NoAccessView,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/notFound",
    name: "未找到",
    component: NotFoundView,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/:pathMatch(.*)", // 匹配所有路由
    redirect: "/notFound",
  },
];
