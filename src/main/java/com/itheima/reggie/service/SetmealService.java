package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同时需要保存套餐和菜品的关联关系(操作setmeal、setmeal_dish两张表)
    void saveWithDish(SetmealDto setmealDto);

    //修改套餐，同时更新套餐和菜品的关联关系(先删除旧关联，再保存新关联)
    void updateWithDish(SetmealDto setmealDto);

    //根据id查询套餐信息和对应的菜品关联信息(修改回显)
    SetmealDto getByIdWithDish(Long id);

    //删除套餐，同时需要删除套餐和菜品的关联数据
    void removeWithDish(List<Long> ids);

    //批量修改套餐状态(起售/停售)
    void updateStatus(Integer status, List<Long> ids);
}
