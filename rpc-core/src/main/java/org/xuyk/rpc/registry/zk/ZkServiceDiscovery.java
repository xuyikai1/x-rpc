package org.xuyk.rpc.registry.zk;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.enums.RpcErrorMessageEnum;
import org.xuyk.rpc.exception.RpcException;
import org.xuyk.rpc.registry.ServiceDiscovery;
import org.xuyk.rpc.registry.zk.client.ZookeeperClient;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Xuyk
 * @Description: zookeeper服务发现
 * @Date: 2020/12/29
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private volatile AtomicInteger index = new AtomicInteger();

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        // 1.获取指定服务的地址列表
        List<String> childrenNodes = ZookeeperClient.getChildrenNodes(serviceName);
        if(CollectionUtil.isEmpty(childrenNodes)){
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, serviceName);
        }
        // 2.根据默认的【轮询】负载均衡策略
        int size = childrenNodes.size();
        String targetServiceUrl = childrenNodes.get(((index.getAndAdd(1) + size) % size));
        log.info("Successfully found the service address:[{}]", targetServiceUrl);

        // 3.拼接InetSocketAddress
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }

}
