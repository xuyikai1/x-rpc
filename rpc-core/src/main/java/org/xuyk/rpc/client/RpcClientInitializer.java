package org.xuyk.rpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.xuyk.rpc.codec.RpcDecoder;
import org.xuyk.rpc.codec.RpcEncoder;
import org.xuyk.rpc.entity.RpcRequest;
import org.xuyk.rpc.entity.RpcResponse;

/**
 * @Author: Xuyk
 * @Description: 客户端-初始化channel pipeline
 * @Date: 2020/12/17
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 客户端pipeline
     * 1.对RpcRequest进行编码
     * 2.对server响应的response进行解码
     * 3.解码完毕交给实际的业务处理器进行处理
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // pipeline加入编/解码器 和 实际的业务处理器
        ChannelPipeline cp = ch.pipeline();
        //	编解码的handler
        cp.addLast(new RpcEncoder(RpcRequest.class));
        // 前4个字节是数据包头部，存储数据包长度 LengthFieldBasedFrameDecoder起到辅助作用
        // (lengthFieldOffset,lengthFieldLength) -> (0,4) 表示包头
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new RpcDecoder(RpcResponse.class));
        cp.addLast(new RpcClientHandler());
    }

}
