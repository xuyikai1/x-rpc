package org.xuyk.rpc.client;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.entity.RpcRequest;
import org.xuyk.rpc.entity.RpcResponse;
import org.xuyk.rpc.factory.SingletonFactory;
import org.xuyk.rpc.registry.ServiceDiscovery;
import org.xuyk.rpc.registry.zk.ZkServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: Xuyk
 * @Description: JDK动态代理
 * @Date: 2020/12/20
 */
@Slf4j
@Getter
public class RpcClientProxy implements InvocationHandler{

    /**
     * 未被处理的请求 requestId
     */
    private final RpcUnprocessedRequests unprocessedRequests;
    private final RpcClient rpcClient;
    private final ServiceDiscovery serviceDiscovery;

    public RpcClientProxy() {
        this.rpcClient = SingletonFactory.getInstance(RpcClient.class);
        this.unprocessedRequests = SingletonFactory.getInstance(RpcUnprocessedRequests.class);
        this.serviceDiscovery = SingletonFactory.getInstance(ZkServiceDiscovery.class);
    }
    /**
     * jdk proxy
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[] {clazz},
                this);
    }
    /**
     * 	invoke代理接口调用方式
     * 	当客户端像调用本地方法一样调用代理类方法时，真正执行的内容
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1.设置请求对象
        RpcRequest request = new RpcRequest();
        String requestId = IdUtil.fastSimpleUUID();
        request.setRequestId(requestId);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        // 2.服务发现
        String serviceName = method.getDeclaringClass().getName();
        InetSocketAddress address = serviceDiscovery.lookupService(serviceName);

        // 2.发送真正的客户端请求 返回结果
        Channel channel = rpcClient.getChannel(address);
        if(channel == null || !channel.isActive()){
            throw new IllegalStateException();
        }
        CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
        unprocessedRequests.put(requestId, completableFuture);
        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("client send message success: {}", JSONUtil.toJsonStr(request));
            } else {
                future.channel().close();
                completableFuture.completeExceptionally(future.cause());
                log.error("Send failed:{}", future.cause());
            }
        });
        return completableFuture.get().getData();
    }

}
