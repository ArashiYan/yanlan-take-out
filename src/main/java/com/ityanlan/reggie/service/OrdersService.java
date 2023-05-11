package com.ityanlan.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ityanlan.reggie.common.R;
import com.ityanlan.reggie.entity.Orders;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrdersService extends IService<Orders> {
    public void submit(@RequestBody Orders orders);
}
