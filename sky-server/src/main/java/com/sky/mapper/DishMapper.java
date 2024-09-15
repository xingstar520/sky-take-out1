package com.sky.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 分页查询菜品
     */
    @Select("SELECT d.*, c.name AS categoryName FROM dish d LEFT JOIN category c ON d.category_id = c.id ${ew.customSqlSegment}")
    Page<DishVO> selectPage(Page<DishVO> page, @Param(Constants.WRAPPER) QueryWrapper<Dish> queryWrapper);
}