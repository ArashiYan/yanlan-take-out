package com.ityanlan.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityanlan.reggie.dto.DishDto;
import com.ityanlan.reggie.entity.Dish;
import com.ityanlan.reggie.entity.DishFlavor;
import com.ityanlan.reggie.mapper.DishMapper;
import com.ityanlan.reggie.service.DishFlavorService;
import com.ityanlan.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时保存对应口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表Dish
        this.save(dishDto);

        //获得保存完后菜品的ID
        Long id = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 获取菜品信息与口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getDishInfoWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //查询dish表获取菜品信息
        Dish dishInfo = this.getById(id);
        //复制到dto里
        BeanUtils.copyProperties(dishInfo,dishDto);

        //查询flavor表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishInfo.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        //将flavor复制到dto里
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 更新菜品与口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品基本信息
        this.updateById(dishDto);
        //删除菜品口味表信息,通过菜品id删除flavor表内信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //获取菜品id
        Long dishId = dishDto.getId();

        queryWrapper.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(queryWrapper);

        //新增菜品口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        //为flavor赋上菜品Id值
        flavors = flavors.stream().peek((item)-> item.setDishId(dishId)).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 更新菜品售卖状态
     * @param status
     * @param ids
     */
    @Override
    public void updateStatus(Integer status, List<String> ids) {
        List<Dish> dishes = this.listByIds(ids);
        dishes = dishes.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());
        this.updateBatchById(dishes);
    }


}
