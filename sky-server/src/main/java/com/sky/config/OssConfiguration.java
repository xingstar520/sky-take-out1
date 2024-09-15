package com.sky.config;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/13
 * @version: 1.0
 */

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean//当容器里没有指定的Bean的情况下创建该对象
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("初始化阿里云OSS配置");
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                              aliOssProperties.getAccessKeyId(),
                              aliOssProperties.getAccessKeySecret(),
                              aliOssProperties.getBucketName());
    }
}
