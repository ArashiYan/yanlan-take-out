package com.ityanlan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ityanlan.reggie.dto.DishDto;
import com.ityanlan.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
    public DishDto getDishInfoWithFlavor(Long id);
    public void updateWithFlavor(DishDto dishDto);

    public void updateStatus(Integer status, List<String> ids);
}
