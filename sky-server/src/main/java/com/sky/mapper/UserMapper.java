package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/24
 * @version: 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 根据map动态条件查询数量
    Integer countByMap(Map map);
}
