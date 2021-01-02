package org.xuyk.rpc.hook;

import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.factory.SingletonFactory;
import org.xuyk.rpc.registry.zk.client.ZookeeperClient;
import org.xuyk.rpc.server.RpcServer;
import org.xuyk.rpc.utils.ThreadPoolExecutorUtils;

/**
 * @Author: Xuyk
 * @Description: 自定义服务端钩子 用于程序优雅关闭时释放资源 + 清空zk上的注册信息
 * @Date: 2020/12/31
 */
@Slf4j
public class ServerCustomShutdownHook {

    private static final ServerCustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new ServerCustomShutdownHook();

    private static final RpcServer RPC_SERVER = SingletonFactory.getInstance(RpcServer.class);

    public static ServerCustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void releaseResources() {
        log.info("addShutdownHook for server clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 1.优雅关闭netty服务端
            RPC_SERVER.close();
            // 2.删除服务端注册信息
            ZookeeperClient.clearRegistry();
            // 3.关闭zookeeper客户端
            ZookeeperClient.close();
            // 4.关闭所有线程池
            ThreadPoolExecutorUtils.shutDownAllThreadPool();
        }));
    }

}
