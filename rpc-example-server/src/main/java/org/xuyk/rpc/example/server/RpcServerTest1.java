package org.xuyk.rpc.example.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.xuyk.rpc.annotation.RpcScan;
import org.xuyk.rpc.server.RpcServer;

/**
 * @Author: Xuyk
 * @Description: x-rpc服务端测试类
 * @Date: 2020/12/22
 */
@RpcScan(basePackage = "org.xuyk.rpc")
public class RpcServerTest1 {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(RpcServerTest1.class);
        RpcServer server = (RpcServer) applicationContext.getBean("rpcServer");
        server.startup();
    }

}
