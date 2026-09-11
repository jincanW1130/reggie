package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * 套餐数据传输对象：套餐基本信息 + 关联菜品集合 + 分类名称
 */
@Data
public class SetmealDto extends Setmeal {

    //套餐关联的菜品集合
    private List<SetmealDish> setmealDishes;

    //分类名称
    private String categoryName;
}
