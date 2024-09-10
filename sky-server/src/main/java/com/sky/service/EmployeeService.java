package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.EmployeeUppswdDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    void save(@RequestBody EmployeeDTO employeeDTO);


    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工状态
     * @param
     * @return
     */
    void startOrStop(Long id, Integer status);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 修改员工
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     * @param employeeUppswdDTO
     */
    void editPassword(EmployeeUppswdDTO employeeUppswdDTO);
}
