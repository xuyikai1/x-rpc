package org.xuyk.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/18
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

    private Class<?> clazz;

    public RpcEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * 	编码器要做的事情
     * 	1. 把对应的java对象进行编码
     * 	2. 之后把内容填充到buffer中去
     * 	3. 写出到Server端
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 判断msg和genericClass是否为同一类型
        if(clazz.isInstance(msg)) {
            // protostuff序列化
            byte[] data = ProtostuffSerializer.serialize(msg);
            // 消息分为：1.包头 (数据包长度)  2.包体(数据包内容)
            // 先传入数据包长度 再传入数据包
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }

}
