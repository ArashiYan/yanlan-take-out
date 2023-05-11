package com.ityanlan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityanlan.reggie.common.CustomException;
import com.ityanlan.reggie.entity.Category;
import com.ityanlan.reggie.entity.Dish;
import com.ityanlan.reggie.entity.Setmeal;
import com.ityanlan.reggie.mapper.CategoryMapper;
import com.ityanlan.reggie.service.CategoryService;
import com.ityanlan.reggie.service.DishService;
import com.ityanlan.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

    @Override
    public void remove(Long id) {
        //查询当前分类是否关联菜品
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealWrapper);
        if (count1>0){
            throw new CustomException("当前分类关联了菜品");
        }
        //查询当前分类是否关联套餐
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId,id);
        int count2 = dishService.count(dishWrapper);
        if (count2>0){
            throw new CustomException("当前分类关联了套餐");
        }
        //正常删除
        super.removeById(id);
    }
}
