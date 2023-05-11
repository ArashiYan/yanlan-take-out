package com.ityanlan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityanlan.reggie.entity.ShoppingCart;
import com.ityanlan.reggie.mapper.ShoppingCartMapper;
import com.ityanlan.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
