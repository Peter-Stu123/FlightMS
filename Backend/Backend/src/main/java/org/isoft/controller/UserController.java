package org.isoft.controller;

import org.isoft.Entity.User;
import org.isoft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // 注册接口
    @PostMapping("/register")
    public User register(@RequestParam String username, @RequestParam String password) {
        return userService.register(username, password);
    }

    // 登录接口
    @PostMapping("/login")
    public boolean login(@RequestParam String username, @RequestParam String password) {
        return userService.login(username, password);
    }
}
