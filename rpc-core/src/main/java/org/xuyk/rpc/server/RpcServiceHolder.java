package org.xuyk.rpc.server;

import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.enums.RpcErrorMessageEnum;
import org.xuyk.rpc.exception.RpcException;
import org.xuyk.rpc.factory.SingletonFactory;
import org.xuyk.rpc.registry.ServiceRegistry;
import org.xuyk.rpc.registry.zk.ZkServiceRegistry;
import org.xuyk.rpc.utils.ResourcesUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Xuyk
 * @Description: 服务holder 缓存管理已注册服务
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

    private final ServiceRegistry serviceRegistry;

    public RpcServiceHolder() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        this.serviceRegistry = SingletonFactory.getInstance(ZkServiceRegistry.class);
    }

    /**
     * 注册服务
     * @param serviceName
     * @param service
     */
    public void addService(String serviceName, Object service) {
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        log.info("Add service: {} and interfaces:{}", serviceName, service.getClass().getInterfaces());
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
     * 发布指定服务
     * @param service
     */
    public void publishService(Object service){
        Class<?> clazz = service.getClass().getInterfaces()[0];
        String serviceName = clazz.getCanonicalName();

        // 注册服务
        serviceRegistry.registerService(serviceName, ResourcesUtils.getServerAddress());
        // 加入缓存
        addService(serviceName, service);
    }

}
