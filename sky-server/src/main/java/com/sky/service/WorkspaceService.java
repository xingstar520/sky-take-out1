package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/10/21
 * @version: 1.0
 */
public interface WorkspaceService {

    /**
     * 查询今日运营数据
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品管理数据
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询套餐管理数据
     */
    SetmealOverViewVO getSetmealOverView();
}
