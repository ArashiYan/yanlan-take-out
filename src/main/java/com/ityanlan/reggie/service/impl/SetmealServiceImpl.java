package com.ityanlan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityanlan.reggie.common.CustomException;
import com.ityanlan.reggie.dto.SetmealDto;
import com.ityanlan.reggie.entity.Setmeal;
import com.ityanlan.reggie.entity.SetmealDish;
import com.ityanlan.reggie.mapper.SetmealMapper;
import com.ityanlan.reggie.service.SetmealDishService;
import com.ityanlan.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveSetmealWithDish(SetmealDto setmealDto) {
        //操作setmeal表保存套餐
        this.save(setmealDto);
        //操作setmealDish表保存套餐绑定的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //向List中传入setmeal的ID
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐以及菜品
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //查询所有id包含在ids中的套餐
        queryWrapper.in(Setmeal::getId,ids);
        //查询在售的套餐
        queryWrapper.eq(Setmeal::getStatus, 1);
        //统计要删除的在售套餐数量，看是否能删除
        int count = this.count(queryWrapper);
        if (count>0){
            throw new CustomException("无法删除在售套餐");
        }
        //根据id删除setmeal表内容
        this.removeByIds(ids);

        //统计套餐id在ids中的setmeal_dish,并删除
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}
