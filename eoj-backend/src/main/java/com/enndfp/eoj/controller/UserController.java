package com.enndfp.eoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.eoj.annotation.AuthCheck;
import com.enndfp.eoj.common.BaseResponse;
import com.enndfp.eoj.common.DeleteRequest;
import com.enndfp.eoj.common.ErrorCode;
import com.enndfp.eoj.common.ResultUtil;
import com.enndfp.eoj.constant.UserConstant;
import com.enndfp.eoj.exception.ThrowUtil;
import com.enndfp.eoj.model.dto.user.*;
import com.enndfp.eoj.model.entity.User;
import com.enndfp.eoj.model.vo.LoginUserVO;
import com.enndfp.eoj.model.vo.UserVO;
import com.enndfp.eoj.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 *
 * @author Enndfp
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 用户 id
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理注册逻辑
        Long userId = userService.userRegister(userRegisterRequest);

        return ResultUtil.success(userId);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          请求
     * @return 登录用户信息
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理登录逻辑
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest, request);

        return ResultUtil.success(loginUserVO);
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    @ApiOperation(value = "用户注销")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理注销逻辑
        Boolean result = userService.userLogout(request);

        return ResultUtil.success(result);
    }

    /**
     * 获取当前登录用户（已脱敏）
     *
     * @param request 请求
     * @return 登录用户信息
     */
    @GetMapping("/get/login")
    @ApiOperation(value = "获取当前登录用户（已脱敏）")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理获取登录用户逻辑
        User user = userService.getLoginUser(request);

        // 3. 处理用户脱敏逻辑
        LoginUserVO loginUserVO = userService.getLoginUserVO(user);

        return ResultUtil.success(loginUserVO);
    }

    /**
     * 创建用户（仅管理员）
     *
     * @param userAddRequest 创建请求
     * @return 用户 id
     */
    @PostMapping("/add")
    @ApiOperation(value = "创建用户（仅管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理创建用户逻辑
        Long userId = userService.addUser(userAddRequest);

        return ResultUtil.success(userId);
    }

    /**
     * 删除用户（仅管理员）
     *
     * @param deleteRequest 删除请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除用户（仅管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);

        // 2. 处理删除用户逻辑
        boolean result = userService.removeById(deleteRequest.getId());

        return ResultUtil.success(result);
    }

    /**
     * 更新用户（仅管理员）
     *
     * @param userUpdateRequest 更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新用户（仅管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理更新用户逻辑
        boolean result = userService.updateUser(userUpdateRequest);

        return ResultUtil.success(result);
    }

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest 个人信息更新请求
     * @param request             请求
     * @return 是否更新成功
     */
    @PostMapping("/update/my")
    @ApiOperation(value = "更新个人信息")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(userUpdateMyRequest == null, ErrorCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理更新个人信息逻辑
        boolean result = userService.updateMyUser(userUpdateMyRequest, request);

        return ResultUtil.success(result);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id 用户 id
     * @return 用户信息
     */
    @GetMapping("/get")
    @ApiOperation(value = "根据 id 获取用户（仅管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 2. 处理获取用户逻辑
        User user = userService.getById(id);
        ThrowUtil.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);

        return ResultUtil.success(user);
    }

    /**
     * 根据 id 获取用户（已脱敏）
     *
     * @param id 用户 id
     * @return 用户信息
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据 id 获取用户（已脱敏）")
    public BaseResponse<UserVO> getUserVOById(long id) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 2. 处理获取用户逻辑
        User user = userService.getById(id);
        ThrowUtil.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);

        // 3. 处理用户脱敏逻辑
        UserVO userVO = userService.getUserVO(user);

        return ResultUtil.success(userVO);
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest 查询请求
     * @return 用户列表
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取用户列表（仅管理员）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理分页获取用户列表逻辑
        Page<User> userPage = userService.listUserByPage(userQueryRequest);

        return ResultUtil.success(userPage);
    }

    /**
     * 分页获取用户列表（已脱敏）
     *
     * @param userQueryRequest 查询请求
     * @return 用户列表
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取用户列表（已脱敏）")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 2. 处理分页获取用户列表逻辑
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);

        return ResultUtil.success(userVOPage);
    }

}
