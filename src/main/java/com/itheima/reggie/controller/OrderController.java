package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 后台：订单分页查询(支持订单号、下单时间范围)
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime){
        log.info("订单分页查询：page={},pageSize={},number={},beginTime={},endTime={}",
                page, pageSize, number, beginTime, endTime);

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //订单号模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        //下单时间范围
        queryWrapper.gt(beginTime != null, Orders::getOrderTime, beginTime);
        queryWrapper.lt(endTime != null, Orders::getOrderTime, endTime);
        //按下单时间倒序
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 后台：修改订单状态(派送/完成)
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        log.info("修改订单状态：{}",orders);
        orderService.updateById(orders);
        return R.success("订单状态修改成功");
    }

    /**
     * 移动端：当前用户订单分页(含订单明细)
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        log.info("查看我的订单：page={},pageSize={}",page,pageSize);
        return R.success(orderService.userPage(page, pageSize));
    }

    /**
     * 移动端：再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        log.info("再来一单：{}",orders);
        orderService.again(orders);
        return R.success("再来一单成功");
    }

    /**
     * 移动端：当前用户全部订单
     * @return
     */
    @GetMapping("/list")
    public R<List<Orders>> list(){
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(Orders::getOrderTime);
        return R.success(orderService.list(queryWrapper));
    }
}
