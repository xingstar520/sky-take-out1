package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jie.
 * @description: TODO
 * @date 2024/10/20
 * @version: 1.0
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额统计结果
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (begin.isBefore(end)) {
            // 日期加1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateJoin = StringUtils.join(dateList, ",");

        // 当前集合用于存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            log.info("date:{}", date);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //查询date日期的营业额数据，营业额是指状态为已完成的订单的总金额
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByNao(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        String turnoverJoin = StringUtils.join(turnoverList, ",");

        return TurnoverReportVO.builder()
                .dateList(dateJoin)
                .turnoverList(turnoverJoin)
                .build();
    }

    /**
     * 用户数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 用户数据统计结果
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (begin.isBefore(end)) {{
            // 日期加1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }}
        String dateJoin = StringUtils.join(dateList, ",");

        // 当前集合用于存放新增用户数
        List<Integer> newUserList = new ArrayList<>();
        // 当前集合用于存放总用户数
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            log.info("date:{}", date);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            //查询date日期的总用户数
            map.put("end", endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);
            //查询date日期的新增用户数
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }
        // 将集合转换为字符串
        String newUserJoin = StringUtils.join(newUserList, ",");
        String totalUserJoin = StringUtils.join(totalUserList, ",");

        return UserReportVO.builder()
                .dateList(dateJoin)
                .newUserList(newUserJoin)
                .totalUserList(totalUserJoin)
                .build();
    }

    /**
     * 订单数据统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单数据统计结果
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (begin.isBefore(end)) {
            // 日期加1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateJoin = StringUtils.join(dateList, ",");

        // 当前集合用于存放每天的订单数
        List<Integer> orderCountList = new ArrayList<>();
        // 当前集合用于存放每天的有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        // 遍历日期集合，查询每天的订单数和有效订单数
        for (LocalDate date : dateList) {
            log.info("date:{}", date);
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 查询date日期的订单数
            LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.between(Orders::getOrderTime, beginTime, endTime);
            Long orderCount = orderMapper.selectCount(queryWrapper);
            // 查询date日期的有效订单数
            queryWrapper.eq(Orders::getStatus, Orders.COMPLETED);
            Long validOrder = orderMapper.selectCount(queryWrapper);
            log.info("orderCount:{}, validOrder:{}", orderCount, validOrder);
            orderCountList.add(orderCount.intValue());
            validOrderCountList.add(validOrder.intValue());
        }
        // 将集合转换为字符串
        String orderCountJoin = StringUtils.join(orderCountList, ",");
        String validOrderCountJoin = StringUtils.join(validOrderCountList, ",");

        // 计算时间区间内的订单总金额
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        //计算时间区间内的有效订单总金额
        Integer totalValidOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        // 计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = totalValidOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        return OrderReportVO.builder()
                .dateList(dateJoin)
                .orderCountList(orderCountJoin)
                .validOrderCountList(validOrderCountJoin)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 销售额Top10统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 销售额Top10统计结果
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 当前集合用于存放商品名称
        List<String> nameList = new ArrayList<>();
        // 当前集合用于存放销量
        List<Integer> numberList = new ArrayList<>();

        List<GoodsSalesDTO> salesTop = orderMapper.getSalesTop(beginTime, endTime);
        for (GoodsSalesDTO goodsSalesDTO : salesTop) {
            log.info("goodsSalesDTO:{}", goodsSalesDTO);
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        // 将集合转换为字符串
        String nameJoin = StringUtils.join(nameList, ",");
        String numberJoin = StringUtils.join(numberList, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameJoin)
                .numberList(numberJoin)
                .build();
    }
}
