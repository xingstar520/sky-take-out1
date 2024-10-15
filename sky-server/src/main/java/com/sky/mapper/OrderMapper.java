package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/10/10
 * @version: 1.0
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

    /**
     * 统计订单状态
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer toBeConfirmed);
}
