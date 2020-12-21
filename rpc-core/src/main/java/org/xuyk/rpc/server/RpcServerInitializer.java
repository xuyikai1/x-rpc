package org.xuyk.rpc.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.xuyk.rpc.codec.RpcDecoder;
import org.xuyk.rpc.codec.RpcEncoder;
import org.xuyk.rpc.entity.RpcRequest;
import org.xuyk.rpc.entity.RpcResponse;

import java.util.Map;

/**
 * @Author: Xuyk
 * @Description: 服务端-初始化channel pipeline
 * @Date: 2020/12/19
 */
@NoArgsConstructor
@AllArgsConstructor
public class RpcServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * key：调用接口名称
     * value：接口实例
     */
    private Map<String, Object> handlerMap;

    /**
     * 服务端端pipeline
     * 1.对客户端发送的RpcRequest进行解码
     * 2.解码完毕交给实际的业务处理器进行处理
     * 3.对准备响应的response进行编码
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();
        // 前4个字节是数据包头部，存储数据包长度 LengthFieldBasedFrameDecoder起到辅助作用
        // (lengthFieldOffset,lengthFieldLength) -> (0,4) 表示包头
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new RpcDecoder(RpcRequest.class));
        cp.addLast(new RpcEncoder(RpcResponse.class));
        cp.addLast(new RpcSeverHandler(handlerMap));
    }

}
