package org.xuyk.rpc.client;

import cn.hutool.core.collection.CollectionUtil;
import org.xuyk.rpc.client.proxy.RpcAsyncProxy;
import org.xuyk.rpc.client.proxy.RpcProxyImpl;
import org.xuyk.rpc.common.SingletonFactory;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Xuyk
 * @Description: Rpc客户端
 * @Date: 2020/12/18
 */
public class RpcClient {

    private String serverAddress;

    private long timeout;

    private RpcConnectManager rpcConnectManager;

    private final Map<Class<?>, Object> syncProxyInstanceMap = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> asyncProxyInstanceMap = new ConcurrentHashMap<Class<?>, Object>();

    /**
     * 发起连接
     * @param serverAddress
     * @param timeout
     */
    public void initClient(String serverAddress, long timeout) {
        this.serverAddress = serverAddress;
        this.timeout = timeout;
        this.rpcConnectManager = SingletonFactory.getInstance(RpcConnectManager.class);
        // 发起连接 真正的连接为异步发起
        this.rpcConnectManager.connect(CollectionUtil.newArrayList(serverAddress));
    }

    public void stop() {
        rpcConnectManager.stop();
    }

    /**
     * 	$invokeSync 同步调用方法
     * 	通过类信息获取到代理类（缓存）
     * @param <T>
     * @param interfaceClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Class<T> interfaceClass) {
        if(syncProxyInstanceMap.containsKey(interfaceClass)) {
            return (T) syncProxyInstanceMap.get(interfaceClass);
        } else {
            Object proxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                    new Class<?>[] {interfaceClass},
                    new RpcProxyImpl<>(interfaceClass, timeout));
            syncProxyInstanceMap.put(interfaceClass, proxy);
            return (T)proxy;
        }
    }

    /**
     * 	$invokeAsync 异步调用方式的方法
     * @param <T>
     * @param interfaceClass
     * @return
     */
    public <T> RpcAsyncProxy invokeAsync(Class<T> interfaceClass) {
        if(asyncProxyInstanceMap.containsKey(interfaceClass)) {
            return (RpcAsyncProxy) asyncProxyInstanceMap.get(interfaceClass);
        } else {
            RpcProxyImpl<T> asyncProxyInstance = new RpcProxyImpl<>(interfaceClass, timeout);
            asyncProxyInstanceMap.put(interfaceClass, asyncProxyInstance);
            return asyncProxyInstance;
        }
    }

}
