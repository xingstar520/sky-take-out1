package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/10
 * @version: 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     */
    @Transactional
    @Override
    public void saveCategory(CategoryDTO categoryDTO) {
//        Category category = new Category();
//        BeanUtils.copyProperties(categoryDTO, category);
        // 使用建造者模式
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .id(categoryDTO.getId())
                .sort(categoryDTO.getSort())
                .type(categoryDTO.getType())
                .build();
        // 设置默认值
        category.setStatus(StatusConstant.DISABLE);
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setCreateUser(BaseContext.getCurrentId());
//        category.setUpdateUser(BaseContext.getCurrentId());
        log.info("添加分类：{}", category);
        categoryMapper.insert(category);
    }

    /**
     * 分类分页查询
     */
    @Transactional
    @Override
    public PageResult pageCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        //创建分页对象
        Page<Category> page = new Page<>(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        //查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 如果 type 不为 null，则添加 type 的过滤条件
        if (categoryPageQueryDTO.getType() != null) {
            queryWrapper.eq(Category::getType, categoryPageQueryDTO.getType());
        }
        //模糊查询
        queryWrapper.isNotNull(Category::getName);
        //参数化防止SQL注入
        if (StringUtils.isNotBlank(categoryPageQueryDTO.getName())) {
            queryWrapper.like(Category::getName, categoryPageQueryDTO.getName());
        }
        //排序
        queryWrapper.orderByDesc(Category::getSort);
        //分页查询
        Page<Category> categoryPage = categoryMapper.selectPage(page, queryWrapper);
        long total = categoryPage.getTotal();
        List<Category> records = categoryPage.getRecords();
        return new PageResult(total, records);
    }

    /**
     * 修改分类状态
     */
    @Override
    public void updateCategoryStatus(Integer status, Long id) {
        Category build = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.updateById(build);
    }

    /**
     * 删除分类
     */
    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }

    /**
     * 修改分类
     */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category build = Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .sort(categoryDTO.getSort())
                .type(categoryDTO.getType())
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.updateById(build);
    }

    /**
     * 分类列表
     */
    @Override
    public List<Category> listCategory(Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        boolean flag = type != null;
        queryWrapper.eq(flag, Category::getType, type)
                .eq(Category::getStatus, StatusConstant.ENABLE)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime);
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        log.info("分类列表：{}", categories);
        return categories;
    }
}
