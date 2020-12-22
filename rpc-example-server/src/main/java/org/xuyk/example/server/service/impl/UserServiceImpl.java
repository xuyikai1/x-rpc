package org.xuyk.example.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.xuyk.example.api.UserService;
import org.xuyk.example.common.User;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/22
 */
@Slf4j
public class UserServiceImpl implements UserService {

    @Override
    public String getUsername(String userId) {
        return String.format("%s:%s", "hello ", userId);
    }

    @Override
    public User getUser(String userId) {
        String username = String.format("%s %s", "hello", userId);
        return new User(userId, username);
    }

}
