package org.xuyk.rpc.demo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/16
 */
@Slf4j
public class Client {

    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 超出5000ms则连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new StringDecoder());
                        p.addLast(new ClientHandler());
                    }
                });

        String host = "127.0.0.1";
        int port = 8765;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host,port);
        ChannelFuture channelFuture;
        try {
            channelFuture = bootstrap.connect(inetSocketAddress).sync();

            //	添加监听 连接成功
            channelFuture.addListener((ChannelFutureListener) future -> {
                if(future.isSuccess()) {
                    log.info("successfully connect to remote server, remote address:{}",inetSocketAddress);
                }
            });

            //	添加监听 连接失败
            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) future
                    -> log.info("channelFuture.channel close complete, remote address:{}",inetSocketAddress));

            // 发送消息
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(Unpooled.wrappedBuffer("aaaaabbbbb".getBytes()));
            Thread.sleep(2000);

            channel.writeAndFlush(Unpooled.copiedBuffer("ccccccc".getBytes()));

            // 等待客户端端口关闭
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

}
