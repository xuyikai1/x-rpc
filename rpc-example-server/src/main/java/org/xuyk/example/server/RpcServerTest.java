package org.xuyk.example.server;

import org.xuyk.example.server.service.impl.UserServiceImpl;
import org.xuyk.rpc.server.RpcServer;

/**
 * @Author: Xuyk
 * @Description: x-rpc服务端测试类
 * @Date: 2020/12/22
 */
public class RpcServerTest {

    public static void main(String[] args) {
        RpcServer server = new RpcServer("127.0.0.1",8765);
        server.publishService(new UserServiceImpl());
        server.startup();
    }

}
