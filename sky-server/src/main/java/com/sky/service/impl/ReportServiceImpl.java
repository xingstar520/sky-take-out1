package com.sky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     *
     * @param begin 开始时间
     * @param end   结束时间
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
            Double turnover = orderMapper.sumByMap(map);
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
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 用户数据统计结果
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (begin.isBefore(end)) {
            {
                // 日期加1
                begin = begin.plusDays(1);
                dateList.add(begin);
            }
        }
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
     *
     * @param begin 开始时间
     * @param end   结束时间
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
     *
     * @param begin 开始时间
     * @param end   结束时间
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

    /**
     * 导出
     *
     * @param response 响应
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //填充营业额数据
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天概览数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //4. 关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
