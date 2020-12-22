package org.xuyk.rpc.server;

import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.entity.RpcServiceProperties;
import org.xuyk.rpc.enums.RpcErrorMessageEnum;
import org.xuyk.rpc.exception.RpcException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Xuyk
 * @Description: 服务holder
 * @Date: 2020/12/22
 */
@Slf4j
public class RpcServiceHolder {

    /**
     * 注册服务的缓存列表
     * key: 服务名称
     * value: 对应服务实例
     */
    private final Map<String, Object> serviceMap;
    /**
     * 已注册的服务名称列表
     */
    private final Set<String> registeredService;

    public RpcServiceHolder() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
    }

    /**
     * 注册服务
     * @param service
     * @param rpcServiceProperties
     */
    public void addService(Object service, RpcServiceProperties rpcServiceProperties) {
        String rpcServiceName = rpcServiceProperties.getServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, service);
        log.info("Add service: {} and interfaces:{}", rpcServiceName, service.getClass().getInterfaces());
    }

    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    public Object getService(String serviceName){
        Object service = serviceMap.get(serviceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    /**
     * 获取服务
     * @param properties
     * @return
     */
    public Object getService(RpcServiceProperties properties) {
        return getService(properties.getServiceName());
    }

}
