package com.enndfp.eojbackendfileservice.service.impl;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.enndfp.eojbackendfileservice.service.FileService;
import com.enndfp.eojbackendfileservice.utils.OssUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 文件服务实现
 *
 * @author Enndfp
 */
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private OssUtil ossUtil;

    /**
     * OSS 客户端
     */
    private final OSS ossClient;

    public FileServiceImpl(OssUtil ossUtil) {
        this.ossUtil = ossUtil;
        // 初始化 OSS 客户端
        this.ossClient = new OSSClientBuilder().build(
                ossUtil.getEndpoint(),
                ossUtil.getAccessKeyId(),
                ossUtil.getAccessKeySecret()
        );
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        // 获取上传文件的输入流
        InputStream inputStream = null;
        String fileName = null;
        try {
            // 1. 创建日期路径（按日期存储）
            String datePath = new DateTime().toString("yyyy-MM-dd");
            // 2. 获取文件名称
            String originalFileName = file.getOriginalFilename();
            // 生成新的文件名（使用时间戳和UUID避免文件名冲突）
            fileName = "eoj/avatars/" + datePath + "/" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString() + "-" + originalFileName;

            // 3. 获取文件输入流
            inputStream = file.getInputStream();

            // 4. 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setContentType(getContentType(fileName.substring(fileName.lastIndexOf(".") + 1)));
            objectMetadata.setContentDisposition("inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 5. 上传到 OSS
            String bucketName = ossUtil.getBucketName();
            ossClient.putObject(bucketName, fileName, inputStream, objectMetadata);

            // 构建文件访问的 URL
            String url = "https://img.enndfp.cn/"+ fileName;

            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 或者抛出自定义异常
        }
    }

    /**
     * 获取文件类型
     *
     * @param filenameExtension 文件扩展名
     * @return 文件类型
     */
    private String getContentType(String filenameExtension) {
        if (filenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (filenameExtension.equalsIgnoreCase("gif")) {
            return "image/gif";
        }
        if (filenameExtension.equalsIgnoreCase("jpeg") || filenameExtension.equalsIgnoreCase("jpg")
                || filenameExtension.equalsIgnoreCase("png")) {
            return "image/jpeg";
        }
        if (filenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (filenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (filenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (filenameExtension.equalsIgnoreCase("pptx") || filenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (filenameExtension.equalsIgnoreCase("docx") || filenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (filenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        return "application/octet-stream";
    }
}
