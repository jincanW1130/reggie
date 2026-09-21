package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    //用户下单
    void submit(Orders orders);

    //移动端：当前用户订单分页(含订单明细)
    Page<OrdersDto> userPage(int page, int pageSize);

    //移动端：再来一单(把订单明细重新加入购物车)
    void again(Orders orders);
}
