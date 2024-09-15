package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/10
 * @version: 1.0
 */
@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     */
    @PostMapping
    @ApiOperation("添加分类")
    public Result<String> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("添加分类：{}", categoryDTO);
        categoryService.saveCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> pageCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageCategory(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改分类状态
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改分类状态")
    public Result<String> updateCategoryStatus(@PathVariable("status") Integer status, Long id) {
        log.info("修改分类状态：status={}, id={}", status, id);
        categoryService.updateCategoryStatus(status, id);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result<String> deleteCategory(Long id) {
        log.info("删除分类：id={}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分类列表
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> listCategory(Integer type) {
        log.info("根据类型查询分类：type={}", type);
        List<Category> list = categoryService.listCategory(type);
        return Result.success(list);
    }
}
