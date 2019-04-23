package com.spring.demo.services;

import com.spring.demo.entity.User;
import com.spring.servlet.annotation.MService;

/**
 * Created By Rick 2019/4/23
 */
@MService
public class UserServiceImpl implements UserService {

    @Override
    public User getUser() {
        User user = new User("1", "Rick",18);
        return user;
    }
}
