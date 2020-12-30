package org.xuyk.rpc.registry.zk;

import org.xuyk.rpc.registry.ServiceRegistry;
import org.xuyk.rpc.registry.zk.client.ZookeeperClient;

import java.net.InetSocketAddress;

/**
 * @Author: Xuyk
 * @Description: zookeeper服务注册
 * @Date: 2020/12/29
 */
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        ZookeeperClient.createPersistentNode(serviceName, inetSocketAddress);
    }

}
