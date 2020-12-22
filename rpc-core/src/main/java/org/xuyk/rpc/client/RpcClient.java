package org.xuyk.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.factory.SingletonFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: Xuyk
 * @Description: Rpc客户端
 * @Date: 2020/12/18
 */
@Slf4j
public class RpcClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private RpcChannelHolder channelProvider;

    public RpcClient(){
        // 初始化nettyClient
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 禁止数据包合并
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new RpcClientInitializer());
        // 获取channel缓存类
        this.channelProvider = SingletonFactory.getInstance(RpcChannelHolder.class);
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress){
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected {} successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
