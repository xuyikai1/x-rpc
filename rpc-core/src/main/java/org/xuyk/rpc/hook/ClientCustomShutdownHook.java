package org.xuyk.rpc.hook;

import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.client.RpcClient;
import org.xuyk.rpc.factory.SingletonFactory;

/**
 * @Author: Xuyk
 * @Description: 自定义客户端钩子 用于程序优雅关闭时释放资源
 * @Date: 2021/1/2
 */
@Slf4j
public class ClientCustomShutdownHook {

    private static final ClientCustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new ClientCustomShutdownHook();

    private static final RpcClient RPC_CLIENT = SingletonFactory.getInstance(RpcClient.class);

    public static ClientCustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void releaseResources() {
        log.info("addShutdownHook for client clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(RPC_CLIENT::close));
    }

}
