package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/10
 * @version: 1.0
 */
public interface CategoryService {

    /**
     * 添加分类
     */
    void saveCategory(@RequestBody CategoryDTO categoryDTO);

    /**
     * 分类分页查询
     */
    PageResult pageCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 修改分类状态
     */
    void updateCategoryStatus(Integer status, Long id);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 修改分类
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 分类列表
     */
    void listCategory(Integer type);
}
