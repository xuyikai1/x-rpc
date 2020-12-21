package org.xuyk.rpc.server;

import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.config.ProviderConfig;
import org.xuyk.rpc.exception.RpcException;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/19
 */
@Slf4j
@NoArgsConstructor
public class RpcServer {

    private String host;

    private Integer port;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * key 接口名称
     * value 接口实现类的对象实例
     * todo 通过自定义注解的方式注入
     */
    private volatile Map<String,Object> handlerMap = new HashMap<>();
    
    public RpcServer(String host,Integer port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 服务端启动
     * @throws InterruptedException
     */
    public void startup() throws InterruptedException {
        if(StrUtil.isBlank(host) || port == null){
            throw new RpcException("invalid address, please check host or port");
        }

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //	tpc = sync + accept  = backlog
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new RpcServerInitializer(handlerMap));

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

        // 同步阻塞等待
        /*try {
            channelFuture.await(5000, TimeUnit.MILLISECONDS);
            if(channelFuture.isSuccess()) {
                log.info("start x-rpc success! ");
            }
        } catch (InterruptedException e) {
            log.error("start x-rpc occur Interrupted, ex:{} ",e);
        }*/

    }

    /**
     * 程序注册器
     * @param providerConfig
     */
    public void registerProcessor(ProviderConfig providerConfig) {
        handlerMap.put(providerConfig.getInterfaceClass(), providerConfig.getRef());
    }

    /**
     * 优雅地关闭资源
     */
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
