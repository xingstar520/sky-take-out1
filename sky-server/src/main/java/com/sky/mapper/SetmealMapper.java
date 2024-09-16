package com.sky.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/10
 * @version: 1.0
 */
/**
 * 套餐管理
 */
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {

    /**
     * 分页查询套餐
     */
    @Select("select s.*, c.name AS categoryName from setmeal s left join category c on s.category_id = c.id ${ew.customSqlSegment}")
    Page<SetmealVO> selectPage(Page<SetmealVO> page, @Param(Constants.WRAPPER) QueryWrapper<Setmeal> queryWrapper);
}
