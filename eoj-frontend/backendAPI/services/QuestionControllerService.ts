/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_boolean_ } from "../models/BaseResponse_boolean_";
import type { BaseResponse_long_ } from "../models/BaseResponse_long_";
import type { BaseResponse_Page_Question_ } from "../models/BaseResponse_Page_Question_";
import type { BaseResponse_Page_QuestionSubmitVO_ } from "../models/BaseResponse_Page_QuestionSubmitVO_";
import type { BaseResponse_Page_QuestionVO_ } from "../models/BaseResponse_Page_QuestionVO_";
import type { BaseResponse_Question_ } from "../models/BaseResponse_Question_";
import type { BaseResponse_QuestionVO_ } from "../models/BaseResponse_QuestionVO_";
import type { DeleteRequest } from "../models/DeleteRequest";
import type { QuestionAddRequest } from "../models/QuestionAddRequest";
import type { QuestionEditRequest } from "../models/QuestionEditRequest";
import type { QuestionQueryRequest } from "../models/QuestionQueryRequest";
import type { QuestionSubmitAddRequest } from "../models/QuestionSubmitAddRequest";
import type { QuestionSubmitQueryRequest } from "../models/QuestionSubmitQueryRequest";
import type { QuestionUpdateRequest } from "../models/QuestionUpdateRequest";
import type { CancelablePromise } from "../core/CancelablePromise";
import { OpenAPI } from "../core/OpenAPI";
import { request as __request } from "../core/request";
import { BaseResponse_List_QuestionSubmitVO_ } from "../models/BaseResponse_List_QuestionSubmitVO_";
import { BaseResponse_QuestionRunResponse_ } from "../models/BaseResponse_QuestionRunResponse_";
import { QuestionRunRequest } from "../models/QuestionRunRequest";

export class QuestionControllerService {
  /**
   * 创建题目
   * @param questionAddRequest questionAddRequest
   * @returns BaseResponse_long_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static addQuestionUsingPost(
    questionAddRequest: QuestionAddRequest
  ): CancelablePromise<BaseResponse_long_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/add",
      body: questionAddRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 删除题目
   * @param deleteRequest deleteRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static deleteQuestionUsingPost(
    deleteRequest: DeleteRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/delete",
      body: deleteRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 编辑题目（用户）
   * @param questionEditRequest questionEditRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static editQuestionUsingPost(
    questionEditRequest: QuestionEditRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/edit",
      body: questionEditRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 根据 id 获取题目
   * @param id id
   * @returns BaseResponse_Question_ OK
   * @throws ApiError
   */
  public static getQuestionByIdUsingGet(
    id?: number
  ): CancelablePromise<BaseResponse_Question_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/question/get",
      query: {
        id: id,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 根据 id 获取题目（已脱敏）
   * @param id id
   * @returns BaseResponse_QuestionVO_ OK
   * @throws ApiError
   */
  public static getQuestionVoByIdUsingGet(
    id?: number
  ): CancelablePromise<BaseResponse_QuestionVO_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/question/get/vo",
      query: {
        id: id,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 分页获取题目列表（仅管理员）
   * @param questionQueryRequest questionQueryRequest
   * @returns BaseResponse_Page_Question_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listQuestionByPageUsingPost(
    questionQueryRequest: QuestionQueryRequest
  ): CancelablePromise<BaseResponse_Page_Question_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/list/page",
      body: questionQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 分页获取题目列表（已脱敏）
   * @param questionQueryRequest questionQueryRequest
   * @returns BaseResponse_Page_QuestionVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listQuestionVoByPageUsingPost(
    questionQueryRequest: QuestionQueryRequest
  ): CancelablePromise<BaseResponse_Page_QuestionVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/list/page/vo",
      body: questionQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 分页获取当前用户的题目列表（已脱敏）
   * @param questionQueryRequest questionQueryRequest
   * @returns BaseResponse_Page_QuestionVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listMyQuestionVoByPageUsingPost(
    questionQueryRequest: QuestionQueryRequest
  ): CancelablePromise<BaseResponse_Page_QuestionVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/my/list/page/vo",
      body: questionQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 提交题目
   * @param questionSubmitAddRequest questionSubmitAddRequest
   * @returns BaseResponse_long_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static doQuestionSubmitUsingPost(
    questionSubmitAddRequest: QuestionSubmitAddRequest
  ): CancelablePromise<BaseResponse_long_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/question_submit/do",
      body: questionSubmitAddRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 运行代码
   * @param questionRunRequest questionRunRequest
   * @returns BaseResponse_QuestionRunResponse_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static doQuestionRunUsingPost(
    questionRunRequest: QuestionRunRequest
  ): CancelablePromise<BaseResponse_QuestionRunResponse_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/question_run/do",
      body: questionRunRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 分页获取题目提交列表（已脱敏）
   * @param questionSubmitQueryRequest questionSubmitQueryRequest
   * @returns BaseResponse_Page_QuestionSubmitVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listQuestionSubmitVoByPageUsingPost(
    questionSubmitQueryRequest: QuestionSubmitQueryRequest
  ): CancelablePromise<BaseResponse_Page_QuestionSubmitVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/question_submit/list/page",
      body: questionSubmitQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 根据题目ID列表获取题目提交记录（已脱敏）
   * @param questionSubmitQueryRequest questionSubmitQueryRequest
   * @returns BaseResponse_List_QuestionSubmitVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listQuestionSubmitVoByQuestionIdsUsingPost(
    questionSubmitQueryRequest: QuestionSubmitQueryRequest
  ): CancelablePromise<BaseResponse_List_QuestionSubmitVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/question_submit/list/questionIds",
      body: questionSubmitQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }

  /**
   * 更新题目（仅管理员）
   * @param questionUpdateRequest questionUpdateRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static updateQuestionUsingPost(
    questionUpdateRequest: QuestionUpdateRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/question/update",
      body: questionUpdateRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    });
  }
}
