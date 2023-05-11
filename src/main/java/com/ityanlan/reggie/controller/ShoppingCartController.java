package com.ityanlan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ityanlan.reggie.common.BaseContext;
import com.ityanlan.reggie.common.R;
import com.ityanlan.reggie.entity.ShoppingCart;
import com.ityanlan.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper添加用户id条件
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        Long dishId = shoppingCart.getDishId();
        //看添加为菜品还是套餐
        if (dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查看是否已经存在
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);
        if (shoppingCartOne!=null){
            //存在则num+1
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartOne);
        }else{
            //不存在则num为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartOne = shoppingCart;
            shoppingCartService.save(shoppingCartOne);
        }
        return R.success(shoppingCartOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //获取当前ID
        Long currentId = BaseContext.getCurrentId();
        //根据ID查询
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        shoppingCartService.remove(queryWrapper);

        return R.success("购物车清空完成");
    }
}
