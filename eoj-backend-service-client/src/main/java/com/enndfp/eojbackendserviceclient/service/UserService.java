package com.enndfp.eojbackendserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enndfp.eojbackendmodel.model.dto.user.*;
import com.enndfp.eojbackendmodel.model.entity.User;
import com.enndfp.eojbackendmodel.model.vo.LoginUserVO;
import com.enndfp.eojbackendmodel.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author Enndfp
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 新用户 id
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @param request          请求
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否注销成功
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 创建用户
     *
     * @param userAddRequest 用户创建请求
     * @return 用户 id
     */
    Long addUser(UserAddRequest userAddRequest);

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @return 是否更新成功
     */
    Boolean updateUser(UserUpdateRequest userUpdateRequest);

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest 用户更新请求
     * @param request             请求
     * @return 是否更新成功
     */
    Boolean updateMyUser(UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request);

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest 查询请求
     * @return 用户列表
     */
    Page<User> listUserByPage(UserQueryRequest userQueryRequest);

    /**
     * 分页获取用户列表（已脱敏）
     *
     * @param userQueryRequest 查询请求
     * @return 用户列表
     */
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request 请求
     * @return 用户
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return 是否为管理员
     */
    Boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return 是否为管理员
     */
    Boolean isAdmin(User user);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return 脱敏的已登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return 脱敏的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息列表
     *
     * @param userList 用户列表
     * @return 脱敏的用户信息列表
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest 查询条件
     * @return 查询条件
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}

