package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/13
 * @version: 1.0
 */
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {

    /**
     * 根据菜品id删除口味
     */
    @Delete("delete from dish_flavor where dish_id in #{ids}")
    void deleteByDishIds(@Param("ids") List<Long> ids);

}
