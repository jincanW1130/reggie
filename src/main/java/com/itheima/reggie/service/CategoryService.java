package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    //根据ID删除分类（删除前校验是否关联菜品/套餐）
    void remove(Long id);
}
