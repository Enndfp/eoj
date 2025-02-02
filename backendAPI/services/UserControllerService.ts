/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_boolean_ } from "../models/BaseResponse_boolean_";
import type { BaseResponse_LoginUserVO_ } from "../models/BaseResponse_LoginUserVO_";
import type { BaseResponse_long_ } from "../models/BaseResponse_long_";
import type { BaseResponse_Page_User_ } from "../models/BaseResponse_Page_User_";
import type { BaseResponse_Page_UserVO_ } from "../models/BaseResponse_Page_UserVO_";
import type { BaseResponse_User_ } from "../models/BaseResponse_User_";
import type { BaseResponse_UserVO_ } from "../models/BaseResponse_UserVO_";
import type { DeleteRequest } from "../models/DeleteRequest";
import type { UserAddRequest } from "../models/UserAddRequest";
import type { UserLoginRequest } from "../models/UserLoginRequest";
import type { UserQueryRequest } from "../models/UserQueryRequest";
import type { UserRegisterRequest } from "../models/UserRegisterRequest";
import type { UserUpdateMyRequest } from "../models/UserUpdateMyRequest";
import type { UserUpdateRequest } from "../models/UserUpdateRequest";
import type { CancelablePromise } from "../core/CancelablePromise";
import { OpenAPI } from "../core/OpenAPI";
import { request as __request } from "../core/request";

export class UserControllerService {
  /**
   * 创建用户（仅管理员）
   * @param userAddRequest userAddRequest
   * @returns BaseResponse_long_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static addUserUsingPost(
    userAddRequest: UserAddRequest
  ): CancelablePromise<BaseResponse_long_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/add",
      body: userAddRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 删除用户（仅管理员）
   * @param deleteRequest deleteRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static deleteUserUsingPost(
    deleteRequest: DeleteRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/delete",
      body: deleteRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 根据 id 获取用户（仅管理员）
   * @param id id
   * @returns BaseResponse_User_ OK
   * @throws ApiError
   */
  public static getUserByIdUsingGet(
    id?: number
  ): CancelablePromise<BaseResponse_User_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/user/get",
      query: {
        "id": id
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 获取当前登录用户（已脱敏）
   * @returns BaseResponse_LoginUserVO_ OK
   * @throws ApiError
   */
  public static getLoginUserUsingGet(): CancelablePromise<BaseResponse_LoginUserVO_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/user/get/login",
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 根据 id 获取用户（已脱敏）
   * @param id id
   * @returns BaseResponse_UserVO_ OK
   * @throws ApiError
   */
  public static getUserVoByIdUsingGet(
    id?: number
  ): CancelablePromise<BaseResponse_UserVO_> {
    return __request(OpenAPI, {
      method: "GET",
      url: "/api/user/get/vo",
      query: {
        "id": id
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 分页获取用户列表（仅管理员）
   * @param userQueryRequest userQueryRequest
   * @returns BaseResponse_Page_User_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listUserByPageUsingPost(
    userQueryRequest: UserQueryRequest
  ): CancelablePromise<BaseResponse_Page_User_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/list/page",
      body: userQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 分页获取用户列表（已脱敏）
   * @param userQueryRequest userQueryRequest
   * @returns BaseResponse_Page_UserVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listUserVoByPageUsingPost(
    userQueryRequest: UserQueryRequest
  ): CancelablePromise<BaseResponse_Page_UserVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/list/page/vo",
      body: userQueryRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 用户登录
   * @param userLoginRequest userLoginRequest
   * @returns BaseResponse_LoginUserVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static userLoginUsingPost(
    userLoginRequest: UserLoginRequest
  ): CancelablePromise<BaseResponse_LoginUserVO_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/login",
      body: userLoginRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 用户注销
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static userLogoutUsingPost(): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/logout",
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 用户注册
   * @param userRegisterRequest userRegisterRequest
   * @returns BaseResponse_long_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static userRegisterUsingPost(
    userRegisterRequest: UserRegisterRequest
  ): CancelablePromise<BaseResponse_long_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/register",
      body: userRegisterRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 更新用户（仅管理员）
   * @param userUpdateRequest userUpdateRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static updateUserUsingPost(
    userUpdateRequest: UserUpdateRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/update",
      body: userUpdateRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }

  /**
   * 更新个人信息
   * @param userUpdateMyRequest userUpdateMyRequest
   * @returns BaseResponse_boolean_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static updateMyUserUsingPost(
    userUpdateMyRequest: UserUpdateMyRequest
  ): CancelablePromise<BaseResponse_boolean_ | any> {
    return __request(OpenAPI, {
      method: "POST",
      url: "/api/user/update/my",
      body: userUpdateMyRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`
      }
    });
  }
}
