package com.sky.service.impl;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/13
 * @version: 1.0
 */
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 新增菜品和口味
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        dishMapper.selectById(dish.getId()); // 获取插入后的主键值
        //获取菜品id
        Long dishId = dish.getId();
        //向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            // 设置 dishId
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            // 批量插入
            MybatisBatch<DishFlavor> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, flavors);
            MybatisBatch.Method<DishFlavor> method = new MybatisBatch.Method<>(DishFlavorMapper.class);
            mybatisBatch.execute(method.insert());
        }
    }

    /**
     * 分页查询菜品
     */
    @Override
    @Transactional
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 创建分页对象
        Page<DishVO> page = new Page<>(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        // 创建查询条件包装器
        QueryWrapper<Dish> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("d.name");
        // 如果提供了名称，则添加模糊查询条件
        if (StringUtils.isNotBlank(dishPageQueryDTO.getName())) {
            queryWrapper.like("d.name", dishPageQueryDTO.getName());
        }
        // 如果提供了分类id，则添加等值查询条件
        if (dishPageQueryDTO.getCategoryId() != null) {
            queryWrapper.eq("d.category_id", dishPageQueryDTO.getCategoryId());
        }
        // 如果提供了状态，则添加等值查询条件
        if (dishPageQueryDTO.getStatus() != null) {
            queryWrapper.eq("d.status", dishPageQueryDTO.getStatus());
        }
        // 按创建时间降序排序
        queryWrapper.orderByDesc("d.create_time");
        // 执行查询
        System.out.println(queryWrapper.getCustomSqlSegment());
        Page<DishVO> dishVOPage = dishMapper.selectPage(page, queryWrapper);
        log.info("分页查询菜品:{}", dishVOPage);
        // 获取总记录数和记录列表
        long total = dishVOPage.getTotal();
        List<DishVO> records = dishVOPage.getRecords();
        // 返回分页结果
        return new PageResult(total, records);
    }

    /**
     * 删除菜品
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断当前菜品是否可以删除-是否存在启售菜品
        List<Dish> dishes = dishMapper.selectBatchIds(ids);
        for (Dish dish : dishes) {
            if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                throw new RuntimeException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 判断当前菜品是否可以删除-是否存在套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new RuntimeException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 删除菜品
        dishMapper.deleteBatchIds(ids);
        // 删除口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorMapper.delete(queryWrapper);
    }

    /**
     * 根据id查询菜品和口味
     */
    @Override
    @Transactional
    public DishVO getByIdWithFlavor(Long id) {
        // 查询菜品
        Dish dish = dishMapper.selectById(id);
        // 查询口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorMapper.selectList(queryWrapper);
        // 封装结果
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 更新菜品和口味
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateById(dish);
        // 删除原有口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        dishFlavorMapper.delete(queryWrapper);
        // 插入新口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            // 批量插入
            MybatisBatch<DishFlavor> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, flavors);
            MybatisBatch.Method<DishFlavor> method = new MybatisBatch.Method<>(DishFlavorMapper.class);
            mybatisBatch.execute(method.insert());
        }
    }
}