package com.enndfp.eojbackendserviceclient.service;

import cn.hutool.core.bean.BeanUtil;
import com.enndfp.eojbackendcommon.common.ErrorCode;
import com.enndfp.eojbackendcommon.exception.BusinessException;
import com.enndfp.eojbackendmodel.model.entity.User;
import com.enndfp.eojbackendmodel.model.enums.UserRoleEnum;
import com.enndfp.eojbackendmodel.model.vo.LoginUserVO;
import com.enndfp.eojbackendmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.enndfp.eojbackendcommon.constant.UserConstant.USER_LOGIN_STATUS;

/**
 * 用户服务
 *
 * @author Enndfp
 */
@FeignClient(name = "eoj-backend-user-service",path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 根据 ID 获取用户
     *
     * @param userId 用户 ID
     * @return 用户
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") Long userId);

    /**
     * 根据 ID 列表获取用户列表
     *
     * @param idList ID 列表
     * @return 用户列表
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 用户
     */
    default User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        LoginUserVO loginUserVO = (LoginUserVO) userObj;
        User currentUser = new User();
        BeanUtil.copyProperties(loginUserVO, currentUser);
        if (currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return 是否为管理员
     */
    default Boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return 脱敏的用户信息
     */
    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return userVO;
    }

}

