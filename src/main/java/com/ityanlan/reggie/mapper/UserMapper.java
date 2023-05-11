package com.ityanlan.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ityanlan.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
