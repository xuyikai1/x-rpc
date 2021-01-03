package org.xuyk.rpc.server;

import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.xuyk.rpc.entity.RpcRequest;
import org.xuyk.rpc.entity.RpcResponse;
import org.xuyk.rpc.exception.RpcException;
import org.xuyk.rpc.factory.SingletonFactory;
import org.xuyk.rpc.utils.ThreadPoolExecutorUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: Xuyk
 * @Description: 服务端处理客户端发送的请求
 * @Date: 2020/12/19
 */
@Slf4j
public class RpcSeverHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private RpcServiceHolder serviceHolder;

    private static final String EXECUTOR_POOL_NAME = "rpcSeverHandler";

    private final ThreadPoolTaskExecutor executor;

    public RpcSeverHandler() {
        this.serviceHolder = SingletonFactory.getInstance(RpcServiceHolder.class);
        this.executor = ThreadPoolExecutorUtils.getHighTpsThreadPoolExecutor(EXECUTOR_POOL_NAME);
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
            // 返回响应结果
            ctx.writeAndFlush(response);
        });
    }

    /**
     * 通过反射调用对应的服务 返回结果
     * @param request
     * @return
     */
    private Object handleRequest(RpcRequest request){
        String className = request.getClassName();
        Object serviceRef = serviceHolder.getService(className);
        Class<?> serviceClass = serviceRef.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        try{
            // JDK reflect
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            return method.invoke(serviceRef, parameters);
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        /*// Cglib reflect
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceRef, parameters);*/
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 心跳包检测
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the channel");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
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
