package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜品数据传输对象：封装页面传递/展示所需数据
 */
@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    //菜品分类名称
    private String categoryName;

    //份数(套餐关联菜品使用)
    private Integer copies;
}
