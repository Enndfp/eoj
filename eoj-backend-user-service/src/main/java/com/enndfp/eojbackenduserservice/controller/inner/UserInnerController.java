package com.enndfp.eojbackenduserservice.controller.inner;

import com.enndfp.eojbackendmodel.model.entity.User;
import com.enndfp.eojbackendserviceclient.service.UserFeignClient;
import com.enndfp.eojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 用户内部接口
 *
 * @author Enndfp
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    /**
     * 根据 ID 获取用户
     *
     * @param userId 用户 ID
     * @return 用户
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") Long userId) {
        return userService.getById(userId);
    }

    /**
     * 根据 ID 列表获取用户列表
     *
     * @param idList ID 列表
     * @return 用户列表
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }
}
