package org.xuyk.rpc.client.proxy;

import org.xuyk.rpc.client.RpcClientHandler;
import org.xuyk.rpc.client.RpcConnectManager;
import org.xuyk.rpc.client.RpcFuture;
import org.xuyk.rpc.common.SingletonFactory;
import org.xuyk.rpc.entity.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/20
 */
public class RpcProxyImpl<T> implements InvocationHandler, RpcAsyncProxy {

    private Class<T> clazz;

    private long timeout;

    private RpcConnectManager rpcConnectManager;

    private final AtomicInteger atomicInteger = new AtomicInteger();

    public RpcProxyImpl(Class<T> clazz, long timeout) {
        this.clazz = clazz;
        this.timeout = timeout;
        this.rpcConnectManager = SingletonFactory.getInstance(RpcConnectManager.class);
    }

    /**
     * 	invoke代理接口调用方式
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //1.设置请求对象
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        // 2.选择一个合适的Client任务处理器
        RpcClientHandler handler = rpcConnectManager.chooseHandler();

        if(handler == null){
            return null;
        }
        // 3. 发送真正的客户端请求 返回结果
        RpcFuture future = handler.sendRequest(request);
        return future.get(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 	$call 异步的代理接口实现, 真正的抱出去RpcFuture 给业务方做实际的回调等待处理
     */
    @Override
    public RpcFuture call(String funcName, Object... args) {

        //1.设置请求对象
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(this.clazz.getName());
        request.setMethodName(funcName);
        request.setParameters(args);
        //	TO DO: 对应的方法参数类型应该通过 类类型 + 方法名称 通过反射得到parameterTypes
        Class<?>[] parameterTypes = new Class[args.length];
        for(int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);

        //2.选择一个合适的Client任务处理器
//        RpcClientHandler handler = RpcConnectManager.getInstance().chooseHandler();
        RpcClientHandler handler = rpcConnectManager.chooseHandler();
        return handler.sendRequest(request);
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        if (typeName.equals("java.lang.Integer")) {
            return Integer.TYPE;
        } else if (typeName.equals("java.lang.Long")) {
            return Long.TYPE;
        } else if (typeName.equals("java.lang.Float")) {
            return Float.TYPE;
        } else if (typeName.equals("java.lang.Double")) {
            return Double.TYPE;
        } else if (typeName.equals("java.lang.Character")) {
            return Character.TYPE;
        } else if (typeName.equals("java.lang.Boolean")) {
            return Boolean.TYPE;
        } else if (typeName.equals("java.lang.Short")) {
            return Short.TYPE;
        } else if (typeName.equals("java.lang.Byte")) {
            return Byte.TYPE;
        }
        return classType;
    }

}
