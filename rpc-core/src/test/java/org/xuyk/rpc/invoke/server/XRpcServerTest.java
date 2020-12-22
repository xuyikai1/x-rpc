package org.xuyk.rpc.invoke.server;


import cn.hutool.core.thread.ThreadUtil;
import org.xuyk.rpc.invoke.client.HelloService;
import org.xuyk.rpc.server.RpcServer;

public class XRpcServerTest {

	public static void main(String[] args) {
        ThreadUtil.execute(() -> {
            RpcServer server = new RpcServer("127.0.0.1",8765);
            server.startup();

            // 注册/发布服务
            HelloService helloService = new HelloServiceImpl();
            server.publishService(helloService);
        });
	}
}
