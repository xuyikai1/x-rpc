package org.xuyk.rpc.demo.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/16
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("server channel active...");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String request = (String) msg;
        String response =  "Server :" + request;
        log.info("Server receive Client msg:{}",request);
        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
    }

}
