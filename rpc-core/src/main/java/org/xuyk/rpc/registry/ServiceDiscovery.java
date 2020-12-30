package org.xuyk.rpc.registry;

import java.net.InetSocketAddress;

/**
 * @Author: Xuyk
 * @Description: 服务发现
 * @Date: 2020/12/29
 */
public interface ServiceDiscovery {

    /**
     * 返回指定服务的地址
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);

}
