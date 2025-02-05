package com.enndfp.eojbackendfileservice.controller;

import com.enndfp.eojbackendcommon.common.BaseResponse;
import com.enndfp.eojbackendcommon.common.ErrorCode;
import com.enndfp.eojbackendcommon.common.ResultUtil;
import com.enndfp.eojbackendcommon.exception.ThrowUtil;
import com.enndfp.eojbackendfileservice.service.FileService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 文件接口
 *
 * @author Enndfp
 */
@Slf4j
@RestController
@RequestMapping("/")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @ApiOperation(value = "上传头像")
    public BaseResponse<String> uploadAvatar(@RequestPart("file") MultipartFile file) {
        // 1. 校验请求参数
        ThrowUtil.throwIf(file.isEmpty(), ErrorCode.NULL_ERROR, "文件为空");

        // 2. 处理上传头像逻辑
        String avatarUrl = fileService.uploadAvatar(file);

        return ResultUtil.success(avatarUrl);
    }
}
