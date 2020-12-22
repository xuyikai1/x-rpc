package org.xuyk.rpc.server;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.xuyk.rpc.factory.SingletonFactory;
import org.xuyk.rpc.entity.RpcRequest;
import org.xuyk.rpc.entity.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/19
 */
@Slf4j
public class RpcSeverHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private RpcServiceHolder serviceHolder;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65536));

    public RpcSeverHandler() {
        this.serviceHolder = SingletonFactory.getInstance(RpcServiceHolder.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        // 异步进行业务处理 不阻塞当前handler的worker线程
        executor.submit(() -> {
            log.info("rpc server receive client request:{}", JSONUtil.toJsonStr(rpcRequest));
            RpcResponse<Object> response = new RpcResponse<>();
            response.setRequestId(rpcRequest.getRequestId());
            try {
                response.setData(handleRequest(rpcRequest));
                log.info("server return response:{}",JSONUtil.toJsonStr(response));
            } catch (Throwable t) {
                response.setThrowable(t);
                log.error("rpc service handleRequest request Throwable:{},rpcRequest:{}",t,JSONUtil.toJsonStr(rpcRequest));
            }

            ctx.writeAndFlush(response).addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()) {
                    // afterRpcHook 后置处理器 可以实现一些自定义的内容（前置则可以在执行handle之前先操作）
                }
            });

        });
    }

    /**
     * 通过反射调用对应的服务
     * @param request
     * @return
     * @throws InvocationTargetException
     */
    private Object handleRequest(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        Object serviceRef = serviceHolder.getService(className);
        Class<?> serviceClass = serviceRef.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        // JDK reflect

        // Cglib reflect
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceRef, parameters);
    }

    /**
     * 	$exceptionCaught 异常处理关闭连接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server caught Throwable:{}" , cause);
        ctx.close();
    }

}
