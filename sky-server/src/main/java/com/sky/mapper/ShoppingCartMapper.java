package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/10/7
 * @version: 1.0
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    /**
     * 查询购物车列表
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);
}
