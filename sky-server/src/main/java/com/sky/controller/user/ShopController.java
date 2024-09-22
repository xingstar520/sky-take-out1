package com.sky.controller.user;

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
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "商铺管理")
@Slf4j
public class ShopController {

    @Autowired
    private ShopService shopService;

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
