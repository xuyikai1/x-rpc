package org.xuyk.example.server;

import cn.hutool.cron.CronUtil;
import org.xuyk.example.server.service.impl.UserServiceImpl;
import org.xuyk.rpc.server.RpcServer;

/**
 * @Author: Xuyk
 * @Description: x-rpc服务端测试类
 * @Date: 2020/12/22
 */
public class RpcServerTest1 {

    public static void main(String[] args) {
        RpcServer server = new RpcServer("127.0.0.1",8765);
        server.publishService(new UserServiceImpl());
        server.startup();

        // 定时查询server中成功连接的channel详情 每秒轮询一次
        CronUtil.schedule("0/1 * * * * ? ", (Runnable) () -> {
        });
    }

}
