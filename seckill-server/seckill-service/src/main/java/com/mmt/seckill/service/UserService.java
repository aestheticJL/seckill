package com.mmt.seckill.service;

import com.mmt.seckill.mapper.UserMapper;
import com.mmt.seckill.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Cacheable(cacheNames = "user", key = "#userId", unless = "#result==null")
    public UserInfo selectById(int userId) {
        return userMapper.selectById(userId);
    }
}
