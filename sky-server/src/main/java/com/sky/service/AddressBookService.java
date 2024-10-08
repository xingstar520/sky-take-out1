package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/10/8
 * @version: 1.0
 */
public interface AddressBookService {

    /**
     * 通讯录列表
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 保存通讯录
     */
    void save(AddressBook addressBook);

    /**
     * 根据id查询通讯录
     */
    AddressBook getById(Long id);

    /**
     * 更新通讯录
     */
    void updateById(AddressBook addressBook);

    /**
     * 根据id删除通讯录
     */
    void removeById(Long id);

    /**
     * 设置默认地址
     */
    void setDefault(AddressBook addressBook);

}
