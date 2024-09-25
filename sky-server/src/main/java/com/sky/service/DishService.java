package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/13
 * @version: 1.0
 */
public interface DishService {

    /**
     * 新增菜品和口味
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜品
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和口味
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 更新菜品和口味
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 更新菜品状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据分类id查询菜品
     */
    List<Dish> listByCategoryId(Long categoryId);

    /**
     * 根据菜品id查询菜品
     */
    List<DishVO> listWithFlavor(Dish dish);
}
