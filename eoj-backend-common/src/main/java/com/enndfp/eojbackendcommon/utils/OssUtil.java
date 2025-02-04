package com.enndfp.eojbackendcommon.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 工具类
 *
 * @author Enndfp
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss.file")
public class OssUtil {

    /**
     * 阿里云 OSS 端点
     */
    private String endpoint;

    /**
     * 阿里云 OSS 访问密钥 ID
     */
    private String accessKeyId;

    /**
     * 阿里云 OSS 访问密钥 Secret
     */
    private String accessKeySecret;

    /**
     * 阿里云 OSS 存储空间名
     */
    private String bucketName;

}
