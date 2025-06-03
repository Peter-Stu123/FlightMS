package org.isoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.isoft.Entity.User;
import org.isoft.mapper.UserMapper;
import org.isoft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Map;
@Service
public  class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User register(String username, String password) {
        // 检查用户名是否已存在
        if (userMapper.selectByMap(Map.of("username", username)).size() > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // MD5 加密（32位长度适配 VARCHAR(32)）
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptedPassword);

        userMapper.insert(user);
        return user;
    }
    // 登录方法
    public boolean login(String username, String password) {
        // 查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) return false;

        // 验证密码（MD5 加密后比对）
        String hashedInput = DigestUtils.md5DigestAsHex(password.getBytes());
        return hashedInput.equals(user.getPassword());
    }

}
