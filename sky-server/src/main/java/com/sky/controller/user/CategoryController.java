package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/24
 * @version: 1.0
 */
@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "分类管理")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 条件查询
     */
    @GetMapping("/list")
    @ApiOperation("条件查询")
    public Result<List<Category>> listCategory(Integer type) {
        log.info("条件查询：type={}", type);
        List<Category> list = categoryService.listCategory(type);
        return Result.success(list);
    }

}
