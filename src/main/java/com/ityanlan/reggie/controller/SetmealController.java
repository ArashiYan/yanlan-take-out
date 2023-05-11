package com.ityanlan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ityanlan.reggie.common.R;
import com.ityanlan.reggie.dto.SetmealDto;
import com.ityanlan.reggie.entity.Category;
import com.ityanlan.reggie.entity.Setmeal;
import com.ityanlan.reggie.service.CategoryService;
import com.ityanlan.reggie.service.SetmealDishService;
import com.ityanlan.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @Autowired
    SetmealDishService setmealDishService;

    @Autowired
    CategoryService categoryService;

    @PostMapping
    public R<String> sava(@RequestBody SetmealDto setmealDto){
        setmealService.saveSetmealWithDish(setmealDto);
        return R.success("保存成功");
    }

    /**
     * 创建套餐分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize, String name){
        //创建分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        //分页构造器信息复制
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        //根据name模糊查询与排序
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //获取page信息
        pageInfo = setmealService.page(pageInfo,queryWrapper);

        //原page中提取记录，复制到新dto page中
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //普通属性复制
            BeanUtils.copyProperties(item,setmealDto);

            //根据类别id查询类名赋予dto对象中
            Category category = categoryService.getById(item.getCategoryId());
            if (category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //dto记录放入dtopage
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);

    }
}
