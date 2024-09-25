package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/24
 * @version: 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
