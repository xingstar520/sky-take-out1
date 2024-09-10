package com.sky.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/9/10
 * @version: 1.0
 */
@Data
public class EmployeeUppswdDTO implements Serializable {
    //员工id
    private Long empId;
    //新密码
    private String newPassword;
    //旧密码
    private String oldPassword;
}
