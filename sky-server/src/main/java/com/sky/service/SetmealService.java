package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/16
 * @version: 1.0
 */
public interface SetmealService {

    /**
     * 新增套餐
     */
    void saveWithDishes(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除套餐
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 更新套餐
     */
    void updateWithDishes(SetmealDTO setmealDTO);

    /**
     * 更新套餐状态
     */
    void updateStatus(Integer status, Long id);
}
