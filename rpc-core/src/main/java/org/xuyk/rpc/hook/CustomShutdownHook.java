package org.xuyk.rpc.hook;

import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.registry.zk.client.ZookeeperClient;
import org.xuyk.rpc.utils.ThreadPoolExecutorUtils;

/**
 * @Author: Xuyk
 * @Description: 自定义钩子 用于程序优雅关闭时释放资源 + 清空zk上的注册信息
 * @Date: 2020/12/31
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void releaseResources() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 1.删除服务端注册信息
            ZookeeperClient.clearRegistry();
            // 2.关闭zookeeper客户端
            ZookeeperClient.close();
            // 3.关闭所有线程池
            ThreadPoolExecutorUtils.shutDownAllThreadPool();
        }));
    }

}
