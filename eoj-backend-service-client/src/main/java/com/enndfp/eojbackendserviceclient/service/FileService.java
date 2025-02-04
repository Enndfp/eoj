package com.enndfp.eojbackendserviceclient.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 *
 * @author Enndfp
 */
public interface FileService {

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像文件路径
     */
    String uploadAvatar(MultipartFile file);
}
