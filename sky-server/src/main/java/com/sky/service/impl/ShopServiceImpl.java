package com.sky.service.impl;

import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/22
 * @version: 1.0
 */
@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    public static final String SHOP_STATUS = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置商铺状态
     *
     * @param status
     */
    @Override
    public void setStatus(Integer status) {
        redisTemplate.opsForValue().set(SHOP_STATUS, status);
    }

    /**
     * 获取商铺状态
     *
     * @return
     */
    @Override
    public Integer getStatus() {
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        return shopStatus == null ? 0 : shopStatus;
    }
}
