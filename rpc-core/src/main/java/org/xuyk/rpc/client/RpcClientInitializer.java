package org.xuyk.rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @Author: Xuyk
 * @Description: 初始化channel pipeline
 * @Date: 2020/12/17
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();
        cp.addLast(new StringDecoder());
        //	编解码的handler
//        cp.addLast(new RpcEncoder(RpcRequest.class));
//        // 前4个字节是数据包头部，存储数据包长度,
//        // (lengthFieldOffset,lengthFieldLength) -> (0,4) 表示包头
//        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
//        cp.addLast(new RpcDecoder(RpcResponse.class));
//        //	实际的业务处理器RpcClientHandler
        cp.addLast(new RpcClientHandler());
    }

}
