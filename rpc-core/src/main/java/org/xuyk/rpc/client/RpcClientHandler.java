package org.xuyk.rpc.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @Author: Xuyk
 * @Description: 实际业务处理器
 * @Date: 2020/12/17
 */
@Slf4j
@Getter
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    /**
     * 客户端所要连接的远程地址SocketAddress
     */
    private SocketAddress socketAddress;

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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String request = (String) msg;
        String response =  "Client :" + request;
        log.info("Client receive Server msg:{}",request);
        // TODO 释放资源
    }

    /**
     * Netty提供了一种主动关闭连接的方式
     * 发送一个Unpooled.EMPTY_BUFFER 这样我们的ChannelFutureListener的close事件就会监听到并关闭通道
     */
    void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

}
