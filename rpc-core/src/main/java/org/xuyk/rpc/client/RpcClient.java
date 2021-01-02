package org.xuyk.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.factory.SingletonFactory;
import org.xuyk.rpc.hook.ClientCustomShutdownHook;

import java.net.InetSocketAddress;
import java.util.Collection;
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
        // 添加JVM钩子 用于应用关闭时 自动释放资源
        ClientCustomShutdownHook.getCustomShutdownHook().releaseResources();
    }

    /**
     * 发起连接
     * @param inetSocketAddress
     * @return
     */
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

    /**
     * 获取对应连接的channel
     * @param inetSocketAddress
     * @return
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        // channel不存在则发起连接
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * Netty提供了一种主动关闭连接的方式
     * 发送一个Unpooled.EMPTY_BUFFER 这样我们的ChannelFutureListener的close事件就会监听到并关闭通道
     */
    public void close() {
        // 关闭通道连接
        Collection<Channel> channels = channelProvider.getAll();
        for (Channel channel : channels) {
            if(channel != null && channel.isActive()){
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
        // 关闭线程组
        eventLoopGroup.shutdownGracefully();
    }

}
