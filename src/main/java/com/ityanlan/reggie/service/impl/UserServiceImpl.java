package com.ityanlan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityanlan.reggie.entity.User;
import com.ityanlan.reggie.mapper.UserMapper;
import com.ityanlan.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
