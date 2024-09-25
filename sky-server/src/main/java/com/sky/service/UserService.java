package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/24
 * @version: 1.0
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param userLoginDTO
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
