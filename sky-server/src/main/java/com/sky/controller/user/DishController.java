package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
 * @date 2024/9/25
 * @version: 1.0
 */
@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 根据分类id查询菜品
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);
        List<DishVO> list = dishService.listWithFlavor(dish);
        return Result.success(list);
    }
}
