package com.sky.service.impl;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/16
 * @version: 1.0
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 新增套餐
     */
    @Override
    @Transactional
    public void saveWithDishes(SetmealDTO setmealDTO) {
        // 向套餐表插入一条数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        setmealMapper.selectById(setmeal.getId());// 获取插入后的主键值
        Long setmealId = setmeal.getId();// 获取套餐id
        // 获取套餐菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);// 设置套餐id
            });
            //批量插入套餐菜品
            MybatisBatch<SetmealDish> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, setmealDishes);
            MybatisBatch.Method<SetmealDish> method = new MybatisBatch.Method<>(SetmealDishMapper.class);
            mybatisBatch.execute(method.insert());
        }
    }

    /**
     * 分页查询套餐
     */
    @Override
    @Transactional
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        Page<SetmealVO> page = new Page<>(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("s.name");
        // 如果提供了名称，则添加模糊查询条件
        if (StringUtils.isNotBlank(setmealPageQueryDTO.getName())) {
            queryWrapper.like("s.name", setmealPageQueryDTO.getName());
        }
        // 如果提供了分类id，则添加等值查询条件
        if (setmealPageQueryDTO.getCategoryId() != null) {
            queryWrapper.eq("s.category_id", setmealPageQueryDTO.getCategoryId());
        }
        // 如果提供了状态，则添加等值查询条件
        if (setmealPageQueryDTO.getStatus() != null) {
            queryWrapper.eq("s.status", setmealPageQueryDTO.getStatus());
        }
        // 按创建时间降序排序
        queryWrapper.orderByDesc("s.create_time");
        // 执行查询
        System.out.println(queryWrapper.getCustomSqlSegment());

        Page<SetmealVO> setmealVOPage = setmealMapper.selectPage(page, queryWrapper);
        long total = setmealVOPage.getTotal();
        List<SetmealVO> setmealVOList = setmealVOPage.getRecords();
        return new PageResult(total, setmealVOList);
    }

    /**
     * 删除套餐
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断是否存在起售中的套餐
        List<Setmeal> setmeals = setmealMapper.selectBatchIds(ids);
        for (Setmeal setmeal : setmeals) {
            if (setmeal.getStatus() == 1) {
                throw new RuntimeException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        //删除套餐
        setmealMapper.deleteBatchIds(ids);
        //删除套餐菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 根据id查询套餐
     */
    @Override
    @Transactional
    public SetmealVO getByIdWithDish(Long id) {
        // 查询套餐
        Setmeal setmeal = setmealMapper.selectById(id);
        // 查询套餐菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(lambdaQueryWrapper);
        // 封装数据
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 更新套餐
     */
    @Override
    @Transactional
    public void updateWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.updateById(setmeal);
        //删除原有套餐菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        setmealDishMapper.delete(lambdaQueryWrapper);
        //插入新的套餐菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmeal.getId());
            });
            //批量插入套餐菜品
            MybatisBatch<SetmealDish> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, setmealDishes);
            MybatisBatch.Method<SetmealDish> method = new MybatisBatch.Method<>(SetmealDishMapper.class);
            mybatisBatch.execute(method.insert());
        }
    }

    /**
     * 更新套餐状态
     */
    @Override
    @Transactional
    public void updateStatus(Integer status, Long id) {
        //判断是否存在未启售的菜品
        if (status == 1) {
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
            List<SetmealDish> setmealDishes = setmealDishMapper.selectList(lambdaQueryWrapper);
            if (setmealDishes.isEmpty()) {
                throw new RuntimeException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        //更新套餐状态
        Setmeal setmeal = setmealMapper.selectById(id);
        if (setmeal == null) {
            throw new RuntimeException(MessageConstant.SETMEAL_NOT_FOUND);
        }
        setmeal.setStatus(status);
        setmealMapper.updateById(setmeal);


    }

    /**
     * 查询套餐列表
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        return setmealMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据套餐id查询包含的菜品列表
     */
    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
