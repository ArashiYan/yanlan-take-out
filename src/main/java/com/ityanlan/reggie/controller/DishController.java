package com.ityanlan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ityanlan.reggie.common.R;
import com.ityanlan.reggie.dto.DishDto;
import com.ityanlan.reggie.entity.Category;
import com.ityanlan.reggie.entity.Dish;
import com.ityanlan.reggie.entity.DishFlavor;
import com.ityanlan.reggie.service.CategoryService;
import com.ityanlan.reggie.service.DishFlavorService;
import com.ityanlan.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    DishService dishService;

    @Autowired
    DishFlavorService dishFlavorService;

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> addDish(@RequestBody DishDto dishDto){
//        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 获取菜品分页信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //条件构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageDto = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //page信息复制
        BeanUtils.copyProperties(pageInfo,pageDto,"records");

        List<Dish> dishRecords = pageInfo.getRecords();
        List<DishDto> dtoRecords = dishRecords.stream().map((item)->{
            //新建一个Dto
            DishDto dishDto = new DishDto();
            //将dish内容复制到dto中
            BeanUtils.copyProperties(item,dishDto);
            //获取类别ID
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            //如果成果获取则将类别name放入dto
            if (category!=null){
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());

        pageDto.setRecords(dtoRecords);

        return R.success(pageDto);
    }
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getDishInfoWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("更新成功");
    }

    /**
     * 起售停售实现
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Integer status,String ids){
        //分割Id
        List<String> splitId = Arrays.asList(ids.split(","));

        dishService.updateStatus(status,splitId);
        return R.success("售卖状态更新成功");
    }

    /**
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteDish(String ids){
        //分割ID
        List<String> split = Arrays.asList(ids.split(","));
        dishService.removeByIds(split);
        return R.success("菜品删除成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> listDish(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //根据传来的类别ID查询
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //查询状态为可售
//        queryWrapper.eq(Dish::getStatus,1);
//        //查询结果排序
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> listDish(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //根据传来的类别ID查询
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态为可售
        queryWrapper.eq(Dish::getStatus,1);
        //查询结果排序
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtos = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //根据dishId查询dishflavor
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(flavorLambdaQueryWrapper);

            dishDto.setFlavors(dishFlavors);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }
}
