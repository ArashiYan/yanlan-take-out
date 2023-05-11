package com.ityanlan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityanlan.reggie.entity.OrderDetail;
import com.ityanlan.reggie.mapper.OrderDetailMapper;
import com.ityanlan.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
