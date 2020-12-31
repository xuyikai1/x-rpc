package org.xuyk.rpc.server;

import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xuyk.rpc.exception.RpcException;
import org.xuyk.rpc.factory.SingletonFactory;
import org.xuyk.rpc.utils.ServerNetUtils;


/**
 * @Author: Xuyk
 * @Description: Rpc服务端
 * @Date: 2020/12/19
 */
@Slf4j
@Getter
@Component
public class RpcServer {

    /**
     * 服务端默认绑定端口
     */
    public static final Integer DEFAULT_PORT = 9998;

    private String host;

    private Integer port;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final RpcServiceHolder rpcServiceHolder;

    public RpcServer(){
        this.host = ServerNetUtils.getServerHost();
        this.port = ServerNetUtils.getServerPort();
        this.rpcServiceHolder = SingletonFactory.getInstance(RpcServiceHolder.class);
    }

    /**
     * 服务端启动
     */
    @SneakyThrows
    public void startup(){
        if(StrUtil.isBlank(host) || port == null){
            throw new RpcException("invalid address, please check host or port");
        }

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //	tpc = sync + accept  = backlog
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new RpcServerInitializer());

        // 异步加监听
        ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
        channelFuture.addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()) {
                log.info("server startup success, address: {}:{}",host,port);
            } else {
                log.error("server startup fail, address: {}:{}",host,port);
                throw new RpcException("server start fail, cause: " + future.cause());
            }
        });

    }

    /**
     * 发布服务
     * @param service
     */
    public void publishService(Object service){
        this.rpcServiceHolder.publishService(service);
    }

    /**
     * 优雅地关闭资源
     */
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();

    }

}
