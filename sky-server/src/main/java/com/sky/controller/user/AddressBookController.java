package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/10/8
 * @version: 1.0
 */
@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "通讯录")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 通讯录列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "通讯录列表")
    public Result<List<AddressBook>> list() {
        log.info("通讯录列表");
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /**
     * 保存通讯录
     */
    @PostMapping
    @ApiOperation(value = "保存通讯录")
    public Result save(@RequestBody AddressBook addressBook) {
        log.info("保存通讯录:{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    /**
     * 根据id查询通讯录
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询通讯录")
    public Result<AddressBook> getById(@PathVariable Long id) {
        log.info("根据id查询通讯录:{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 更新通讯录
     */
    @PutMapping
    @ApiOperation(value = "更新通讯录")
    public Result update(@RequestBody AddressBook addressBook) {
        log.info("更新通讯录:{}", addressBook);
        addressBookService.updateById(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除通讯录
     */
    @DeleteMapping
    @ApiOperation(value = "根据id删除通讯录")
    public Result deleteById(Long id) {
        log.info("删除通讯录:{}", id);
        addressBookService.removeById(id);
        return Result.success();
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    @ApiOperation("获取默认地址")
    public Result<AddressBook> getDefault() {
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }

        return Result.error("没有查询到默认地址");
    }
}
