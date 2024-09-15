package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/15
 * @version: 1.0
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {

    @Select.List({
            @Select("SELECT setmeal_id FROM setmeal_dish WHERE dish_id IN (#{ids})")
    })
    List<Long> getSetmealIdsByDishIds(List<Long> ids);
}
