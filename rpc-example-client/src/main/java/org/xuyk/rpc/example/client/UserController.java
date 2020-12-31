package org.xuyk.rpc.example.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xuyk.rpc.annotation.RpcReference;
import org.xuyk.rpc.example.api.UserService;
import org.xuyk.rpc.example.common.User;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/30
 */
@Slf4j
@Component
public class UserController {

    @RpcReference
    private UserService userService;

    public User getUser(String userId){
        return userService.getUser(userId);
    }

    public String getUsername(String userId){
        return userService.getUsername(userId);
    }

}
