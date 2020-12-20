package org.xuyk.rpc.client;

import cn.hutool.json.JSONUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.entity.RpcRequest;
import org.xuyk.rpc.entity.RpcResponse;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Xuyk
 * @Description: 实际业务处理器
 * @Date: 2020/12/17
 */
@Slf4j
@Getter
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Channel channel;
    /**
     * 客户端所要连接的远程地址SocketAddress
     */
    private SocketAddress socketAddress;
    /**
     * key：requestId value：RpcFuture
     */
    private Map<String, RpcFuture> pendingRpcResponseTable = new ConcurrentHashMap<>();
    /**
     * 通道注册的时候触发此方法
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    /**
     * 通道激活的时候触发此方法（客户端真正发起连接时）
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("RpcClientHandler channel active...");
        super.channelActive(ctx);
        this.socketAddress = this.channel.remoteAddress();
    }


    /**
     * 对服务端的响应内容做处理
     * @param ctx
     * @param response
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        log.info("Client receive Server msg:{}", JSONUtil.toJsonStr(response));
        String requestId = response.getRequestId();
        RpcFuture rpcFuture = pendingRpcResponseTable.get(requestId);
        if(rpcFuture != null) {
            pendingRpcResponseTable.remove(requestId);
            rpcFuture.done(response);
        }
    }

    /**
     * Netty提供了一种主动关闭连接的方式
     * 发送一个Unpooled.EMPTY_BUFFER 这样我们的ChannelFutureListener的close事件就会监听到并关闭通道
     */
    void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 	异步发送请求方法
     * @param request
     * @return
     */
    public RpcFuture sendRequest(RpcRequest request) {
        RpcFuture rpcFuture = new RpcFuture(request);
        pendingRpcResponseTable.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }

}
