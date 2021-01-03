package org.xuyk.rpc.example.client;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.xuyk.rpc.annotation.RpcScan;
import org.xuyk.rpc.example.common.User;

/**
 * @Author: Xuyk
 * @Description: x-rpc客户端测试类
 * @Date: 2020/12/22
 */
@Slf4j
@RpcScan(basePackage = "org.xuyk.rpc")
public class RpcClientTest {

    public static void main(String[] args) {
        // spring 依赖查找
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(RpcClientTest.class);
        UserController userController = (UserController) applicationContext.getBean("userController");

        String userId = "x-rpc-01";
        User user = userController.getUser(userId);
        log.info("【RpcServer response】:{}", JSONUtil.toJsonStr(user));

        ThreadUtil.sleep(6000);

        String username = userController.getUsername(userId);
        log.info("【RpcServer response】:{}",username);

        // 旧版 代理调用服务
        /*RpcClientProxy clientProxy = new RpcClientProxy();
        UserService userService = clientProxy.getProxy(UserService.class);
        String userId = "x-rpc";
        // 1.getUsername Test
        String username = userService.getUsername(userId);
        log.info("username:{}",username);

        // 2.getUser Test
        User user = userService.getUser(userId);
        log.info("user:{}", JSONUtil.toJsonStr(user));

        clientProxy.getRpcClient().close();*/
    }

}
