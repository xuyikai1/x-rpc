package org.xuyk.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author: Xuyk
 * @Description: rpc解码器 protostuff解码
 * @Date: 2020/12/18
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    public RpcDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //	如果请求数据包不足4个字节（包头长度） 可能程序未读取完毕则不做处理
        if(in.readableBytes() < 4) {
            return;
        }
        // 首先记录一下当前的位置
        in.markReaderIndex();
        // 当前请求数据包的大小读取出来
        int dataLength = in.readInt();
        // 当前可读字节长度小于数据包长度 说明数据没传完
        if(in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        // 真正读取需要长度的数据包内容
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        // 解码
        Object obj = ProtostuffSerializer.deserialize(data, clazz);
        // 填充到buffer中 传播给下游handler做实际的处理
        out.add(obj);
    }

}
