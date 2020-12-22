package org.xuyk.example.client;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.example.api.UserService;
import org.xuyk.example.common.User;
import org.xuyk.rpc.client.RpcClientProxy;

/**
 * @Author: Xuyk
 * @Description: x-rpc客户端测试类
 * @Date: 2020/12/22
 */
@Slf4j
public class RpcClientTest {

    public static void main(String[] args) {
        RpcClientProxy clientProxy = new RpcClientProxy();
        UserService userService = clientProxy.getProxy(UserService.class);
        String userId = "x-rpc";
        // 1.getUsername Test
        String username = userService.getUsername(userId);
        log.info("username:{}",username);

        // 2.getUser Test
        User user = userService.getUser(userId);
        log.info("user:{}", JSONUtil.toJsonStr(user));
    }

}
