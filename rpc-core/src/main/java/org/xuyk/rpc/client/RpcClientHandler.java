package org.xuyk.rpc.client;

import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.entity.RpcResponse;
import org.xuyk.rpc.factory.SingletonFactory;

import java.net.SocketAddress;

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

    private RpcUnprocessedRequests unprocessedRequests = SingletonFactory.getInstance(RpcUnprocessedRequests.class);
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
        unprocessedRequests.complete(response);
    }

}
