package com.ityanlan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ityanlan.reggie.dto.SetmealDto;
import com.ityanlan.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveSetmealWithDish(SetmealDto setmealDto);
    public void removeWithDish(List<Long> ids);
}
