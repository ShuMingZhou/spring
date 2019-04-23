package com.spring.demo.controller;

import com.spring.demo.entity.User;
import com.spring.demo.services.UserService;
import com.spring.servlet.annotation.MAutowired;
import com.spring.servlet.annotation.MController;
import com.spring.servlet.annotation.MRequestMapping;
import com.spring.servlet.annotation.MResponseBody;

/**
 * 访问路径http://localhost:8080/web/user
 * 注意tomcat默认路径设置为：/
 * Created By Rick 2019/4/23
 */
@MController
@MRequestMapping("web")
public class UserController {
    @MAutowired
    private UserService userService;

    @MRequestMapping("/user")
    @MResponseBody
    public User getUser() {
        return userService.getUser();
    }
    //返回JSP页面出现500问题
    @MRequestMapping("/hello")
    public String hello(String name) {
        return "hello";
    }
}
