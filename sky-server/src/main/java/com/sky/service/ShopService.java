package com.sky.service;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/22
 * @version: 1.0
 */
public interface ShopService {

    /**
     * 设置商铺状态
     *
     * @param status
     */
    void setStatus(Integer status);


    /**
     * 获取商铺状态
     *
     * @return
     */
    Integer getStatus();
}
