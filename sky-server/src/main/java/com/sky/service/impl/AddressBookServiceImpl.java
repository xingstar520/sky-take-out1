package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/10/8
 * @version: 1.0
 */
@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 通讯录列表
     */
    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        if (addressBook.getUserId() != null) {
            queryWrapper.eq(AddressBook::getUserId, addressBook.getUserId());
        }
        if (addressBook.getPhone() != null) {
            queryWrapper.eq(AddressBook::getPhone, addressBook.getPhone());
        }
        if (addressBook.getIsDefault() != null) {
            queryWrapper.eq(AddressBook::getIsDefault, addressBook.getIsDefault());
        }
        return addressBookMapper.selectList(queryWrapper);
    }

    /**
     * 保存通讯录
     */
    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据id查询通讯录
     */
    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.selectById(id);
    }

    /**
     * 更新通讯录
     */
    @Override
    public void updateById(AddressBook addressBook) {
        addressBookMapper.updateById(addressBook);
    }

    /**
     * 根据id删除通讯录
     */
    @Override
    public void removeById(Long id) {
        addressBookMapper.deleteById(id);
    }

    /**
     * 设置默认地址
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookMapper.update(addressBook, queryWrapper);

        // 设置默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.updateById(addressBook);
    }
}
