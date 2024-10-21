package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据状态和订单时间查询订单
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
     * 营业额统计数据
     */
    Double sumByNao(Map map);

    /**
     * 获取指定时间内Top10
     */
    List<GoodsSalesDTO> getSalesTop(LocalDateTime begin, LocalDateTime end);
}
