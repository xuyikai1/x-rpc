package org.xuyk.rpc.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.common.ThreadPoolExecutorHolder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Xuyk
 * @Description: 连接管理器
 * @Date: 2020/12/17
 */
@Slf4j
public class RpcConnectManager {

    /**
     * 可用的<连接,连接对应处理器>
     * 一个连接对应一个实际业务处理器
     */
    private Map<InetSocketAddress, RpcClientHandler> availableHandlerMap = new ConcurrentHashMap<>();

    /**
     * 所有可用连接对应的 任务执行器列表（线程安全）
     */
    private CopyOnWriteArrayList<RpcClientHandler> availableHandlerList = new CopyOnWriteArrayList<>();

    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutorHolder.getHighTpsThreadPoolExecutor("connectManagerThreadPool");

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    /**
     * 可重入锁
     */
    private ReentrantLock connectedLock = new ReentrantLock();
    /**
     * 锁竞争的资源
     */
    private Condition connectedCondition = connectedLock.newCondition();
    /**
     * 多线程操作isRunning来判断是否正在运行 使用volatile修饰
     */
    private volatile boolean isRunning = true;

    private volatile AtomicInteger handlerIdx = new AtomicInteger();

    private static volatile RpcConnectManager RPC_CONNECT_MANAGER = new RpcConnectManager();
    /**
     * 统一使用单例工厂生产ConnectManager单例
     */
    private RpcConnectManager(){}

    public static RpcConnectManager getInstance() {
        return RPC_CONNECT_MANAGER;
    }

    /**
     * 客户端发起连接（调用连接管理器的连接方法）
     * 更新可用的服务器连接
     */
    public void connect(List<String> availableAddresses){
        if(CollectionUtil.isEmpty(availableAddresses)){
            // 添加告警
            log.error(" no available server address! ");
            // 清除所有的缓存信息
            clearAllConnectedResources();
        }

        // 1.从注册中心获取到可用的服务端地址列表
        Set<InetSocketAddress> addressSet = new HashSet<>();
        for (String availableAddress : availableAddresses) {
            String[] array = availableAddress.split(":");
            String host = array[0];
            int port = Convert.toInt(array[1]);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host,port);
            addressSet.add(inetSocketAddress);
        }

        // 2.对照并更新缓存的地址列表
        // 2.1 对未缓存的地址使用线程池异步发起连接
        for (InetSocketAddress inetSocketAddress : addressSet) {
            if(!availableHandlerMap.containsKey(inetSocketAddress)){
                connectAsync(inetSocketAddress);
            }
        }

        // 2.2 对缓存中过期的地址需要进行移除
        for (RpcClientHandler rpcClientHandler : availableHandlerList) {
            InetSocketAddress socketAddress = (InetSocketAddress) rpcClientHandler.getSocketAddress();
            if(!addressSet.contains(socketAddress)) {
                log.info(" remove invalid server node :{}",socketAddress);
                RpcClientHandler handler = availableHandlerMap.get(socketAddress);
                clearHandlerResource(handler,socketAddress);
            }
        }
    }

    /**
     * 异步发起连接
     * @param socketAddress
     */
    private void connectAsync(SocketAddress socketAddress){
        threadPoolExecutor.execute(() ->{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    // 禁止数据包合并
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new RpcClientInitializer());
            // 发起连接
            connect(bootstrap,socketAddress);
        });
    }

    /**
     * 真正地发起连接
     * @param bootstrap
     * @param socketAddress
     */
    private void connect(Bootstrap bootstrap,SocketAddress socketAddress){
        final ChannelFuture channelFuture = bootstrap.connect(socketAddress);

        //	添加监听 连接成功的时候 将新连接放入缓存中
        channelFuture.addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()) {
                log.info("successfully initClient to remote server, remote address:{}",socketAddress);
                // 从pipeline中找到业务处理的RpcClientHandler
                RpcClientHandler handler = future.channel().pipeline().get(RpcClientHandler.class);
                // 添加至缓存中
                addHandler(handler);
            }
        });

        //	添加监听 连接失败的时候 清除资源后进行发起重连操作
        channelFuture.channel().closeFuture().addListener((ChannelFutureListener) future -> {
            log.info("channelFuture.channel close complete, remote address:{}",socketAddress);
            future.channel().eventLoop().schedule(() -> {
                log.warn("initClient fail, ready to reconnect.. ");
                // 清空对应资源再重新发起连接
                clearAllConnectedResources();
                connect(bootstrap, socketAddress);
            }, 3, TimeUnit.SECONDS);
        });
    }

    /**
     * 添加RpcClientHandler到指定的缓存中
     * connectedHandlerMap & connectedHandlerList
     * @param handler
     */
    private void addHandler(RpcClientHandler handler) {
        // 连接成功添加到缓存中
        InetSocketAddress socketAddress = (InetSocketAddress) handler.getChannel().remoteAddress();
        availableHandlerMap.put(socketAddress, handler);
        availableHandlerList.add(handler);

        // 唤醒可用的业务执行器
        signalAvailableHandler();
    }

    /**
     * 	唤醒另外一端的线程(阻塞的状态中) 告知有新连接接入
     * 	唤醒的是等待新连接接入的线程 waitingForAvailableHandler
     */
    private void signalAvailableHandler() {
        connectedLock.lock();
        try {
            connectedCondition.signalAll();
        } finally {
            connectedLock.unlock();
        }
    }

    /**
     * 连接失败及时释放资源
     * 1.连接资源释放和通道关闭 2.缓存移除
     */
    private void clearAllConnectedResources() {
        for(final RpcClientHandler rpcClientHandler : availableHandlerList) {
            // 通过RpcClientHandler 找到具体的remotePeer, 从connectedHandlerMap进行移除指定的 RpcClientHandler
            InetSocketAddress socketAddress = (InetSocketAddress) rpcClientHandler.getSocketAddress();
            RpcClientHandler handler = availableHandlerMap.get(socketAddress);
            clearHandlerResource(handler,socketAddress);
        }
    }

    /**
     * 释放资源
     * @param handler
     */
    private void clearHandlerResource(RpcClientHandler handler,InetSocketAddress socketAddress){
        if(handler != null) {
            // 真正的连接资源 释放
            handler.close();
            availableHandlerMap.remove(socketAddress);
            availableHandlerList.remove(handler);
        }
    }

    @SuppressWarnings("unchecked")
    public RpcClientHandler chooseHandler() {
        // 复制一份已连接的缓存列表
        CopyOnWriteArrayList<RpcClientHandler> handlers = (CopyOnWriteArrayList<RpcClientHandler>)this.availableHandlerList.clone();

        int size = handlers.size();

        while(isRunning && size <= 0) {
            try {
                // 等待有新连接接入时 说明可用连接缓存数量已经大于0 重新获取缓存列表
                if(waitingForAvailableHandler()) {
                    handlers = (CopyOnWriteArrayList<RpcClientHandler>)this.availableHandlerList.clone();
                    size = handlers.size();
                }
            } catch (InterruptedException e) {
                log.error(" waiting for available node is interrupted !");
                throw new RuntimeException("no initClient any servers!", e);
            }
        }
        if(!isRunning) {
            return null;
        }
        // 最终使用取模方式取得其中一个业务处理器进行实际的业务处理
        return handlers.get(((handlerIdx.getAndAdd(1) + size) % size));
    }

    /**
     * 等待新连接接入通知方法
     * @return
     * @throws InterruptedException
     */
    private boolean waitingForAvailableHandler() throws InterruptedException {
        connectedLock.lock();
        try {
            return connectedCondition.await(6000, TimeUnit.MICROSECONDS);
        } finally {
            connectedLock.unlock();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        // 设置状态为停止
        isRunning = false;
        // 资源释放
        for (RpcClientHandler handler : availableHandlerList) {
            handler.close();
        }
        // 突然stop，唤醒可能在阻塞等待新连接接入的线程跳出while循环
        signalAvailableHandler();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

    /**
     * 重新连接
     * @param handler
     * @param socketAddress
     */
    public void reconnect(final RpcClientHandler handler, final SocketAddress socketAddress) {
        // 释放资源
        if(handler != null) {
            handler.close();
            availableHandlerList.remove(handler);
            if(socketAddress instanceof InetSocketAddress){
                availableHandlerMap.remove(socketAddress);
            }
        }
        // 重新异步发起连接
        connectAsync(socketAddress);
    }

}
