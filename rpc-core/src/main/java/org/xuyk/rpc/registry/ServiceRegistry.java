package org.xuyk.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @Author: Xuyk
 * @Description: 服务注册
 * @Date: 2020/12/29
 */
public interface ServiceRegistry {

    /**
     * 服务注册
     * @param serviceName serviceName
     * @param inetSocketAddress address
     */
    void registerService(String serviceName, InetSocketAddress inetSocketAddress);

}
