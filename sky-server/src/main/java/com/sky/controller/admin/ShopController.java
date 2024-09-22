package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/22
 * @version: 1.0
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "商铺管理")
@Slf4j
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 设置商铺状态
     *
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation(value = "设置商铺状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置商铺状态：{}", status == 1 ? "营业中" : "打烊了");
        shopService.setStatus(status);
        return Result.success();
    }

    /**
     * 获取商铺状态
     *
     * @return
     */
    @GetMapping("/status")
    @ApiOperation(value = "获取商铺状态")
    public Result<Integer> getStatus() {
        Integer status = shopService.getStatus();
        log.info("获取商铺状态：{}", status == 1 ? "营业中" : "打烊了");
        return Result.success(status);
    }
}
