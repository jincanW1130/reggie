package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * 订单数据传输对象：订单基本信息 + 订单明细(移动端订单列表展示用)
 */
@Data
public class OrdersDto extends Orders {

    //订单明细
    private List<OrderDetail> orderDetails;
}
