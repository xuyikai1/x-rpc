package org.xuyk.rpc.example.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.example.api.UserService;
import org.xuyk.rpc.example.common.User;
import org.xuyk.rpc.annotation.RpcService;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/22
 */
@Slf4j
@RpcService
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
