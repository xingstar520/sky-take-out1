package com.sky.service.impl;

import com.alibaba.druid.util.Utils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.EmployeeUppswdDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.text.Utilities;
import java.time.LocalDateTime;
import java.util.Base64;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    private static final SecureRandom RANDOM = new SecureRandom();//随机数生成器
    private static final int SALT_LENGTH = 16;//盐的长度

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */


    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        // mybatis-plus的方法
        log.info("username:{}", username);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        Employee employee = employeeMapper.selectOne(queryWrapper);
        log.info("employee:{}", employee);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传来的密码进行md5加盐处理，然后再进行比对
        String salt = employee.getSalt();//盐
        String hashedPassword = hashPassword(password, salt);//加密后的密码

        if (!hashedPassword.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    //对密码进行加密
    private String hashPassword(String password, String salt) {
        String saltedPassword = salt + password;
        return DigestUtils.md5DigestAsHex(saltedPassword.getBytes());
    }

    /**
     * 添加员工
     *
     * @param employeeDTO
     * @return
     */
    @Transactional
    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setStatus(StatusConstant.ENABLE);//默认启用
        //生成盐
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        employee.setSalt(Base64.getEncoder().encodeToString(salt));
        //对密码进行加密,默认密码为123456
        String password = hashPassword(PasswordConstant.DEFAULT_PASSWORD, employee.getSalt());
        employee.setPassword(password);
        //设置创建时间和更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //设置创建人和更新人
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询员工
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Transactional
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 创建分页对象
        Page<Employee> page = new Page<>(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        // 创建查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 排除姓名为null的记录
        queryWrapper.isNotNull(Employee::getName);
        // 使用like方法进行模糊查询，参数化防止SQL注入
        if (StringUtils.isNotBlank(employeePageQueryDTO.getName())) {
            queryWrapper.like(Employee::getName, employeePageQueryDTO.getName());
        }
        // 根据创建时间降序排序
        queryWrapper.orderByDesc(Employee::getCreateTime);
        // 执行分页查询
        Page<Employee> employeePage = employeeMapper.selectPage(page, queryWrapper);
        // 提取结果
        long total = employeePage.getTotal();
        List<Employee> records = employeePage.getRecords();
        // 返回结果
        return new PageResult(total, records);
    }

    /**
     * 修改员工状态
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public void startOrStop(Long id, Integer status) {
//        Employee employee = new Employee();
//        employee.setId(id);
//        employee.setStatus(status);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        employeeMapper.updateById(employee);
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        employee.setPassword("********");
        return employee;
    }

    /**
     * 修改员工
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateById(employee);
    }

    /**
     * 修改密码
     *
     * @param employeeUppswdDTO
     */
    @Override
    public void editPassword(EmployeeUppswdDTO employeeUppswdDTO) {
        //1.根据id查询员工
        Employee employee = employeeMapper.selectById(employeeUppswdDTO.getEmpId());
        if (employee == null) {
            throw new AccountNotFoundException("Employee not found");
        }
        //2.对原密码进行加密
        String oldPassword = hashPassword(employeeUppswdDTO.getOldPassword(), employee.getSalt());
        //3.比对原密码是否正确
        if (!oldPassword.equals(employee.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        //4.对新密码进行加密
        String newPassword = hashPassword(employeeUppswdDTO.getNewPassword(), employee.getSalt());
        //5.修改密码
        employee.setPassword(newPassword);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateById(employee);
    }
}
