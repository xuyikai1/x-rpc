package org.xuyk.rpc.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.registry.zk.client.ZookeeperClient;
import org.xuyk.rpc.server.RpcServer;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * @Author: Xuyk
 * @Description: 资源配置文件工具类
 * @Date: 2020/12/30
 */
@Slf4j
public class ResourcesUtils {

    /**
     * 优先级1：rpcServer配置文件
     */
    private static final String LOCAL_RPC_PROPERTIES = System.getProperty("user.dir") + File.separator + "rpc.properties";
    /**
     * 优先级1：zookeeper配置文件
     */
    private static final String LOCAL_ZOOKEEPER_PROPERTIES = System.getProperty("user.dir") + File.separator + "zookeeper.properties";
    /**
     * 优先级2：rpcServer配置文件 classpath下（/resources）
     */
    private static final String CLASSPATH_RPC_PROPERTIES = "classpath:rpc.properties";
    /**
     * 优先级2：zookeeper配置文件 classpath下（/resources）
     */
    private static final String CLASSPATH_ZOOKEEPER_PROPERTIES = "classpath:zookeeper.properties";
    /**
     * rpcServer配置文件host
     */
    private static final String PROPERTIES_HOST_KEY = "server.host";
    /**
     * rpcServer配置文件端口号
     */
    private static final String PROPERTIES_PORT_KEY = "server.port";
    /**
     * 未配置host时 默认host
     */
    private static final String DEFAULT_HOST = NetUtil.getLocalhostStr();
    /**
     * 未配置port时 默认port
     */
    private static final Integer DEFAULT_PORT = RpcServer.DEFAULT_PORT;
    /**
     * 默认连接服务端zk地址
     */
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = ZookeeperClient.DEFAULT_ZOOKEEPER_ADDRESS;

    /**
     * 获取服务端地址 rpc.properties
     * @return ServerAddress
     */
    public static InetSocketAddress getServerAddress(){
        // 1.优先级1 项目部署路径下的配置文件
        if(FileUtil.exist(LOCAL_RPC_PROPERTIES)){
            String host = new Props(LOCAL_RPC_PROPERTIES).getStr(PROPERTIES_HOST_KEY);
            Integer port = new Props(LOCAL_RPC_PROPERTIES).getInt(PROPERTIES_PORT_KEY);
            if(StrUtil.isNotBlank(host) && port != null){
                log.info("read server address from deploy project path properties success, address: [{}:{}]", host, port);
                return new InetSocketAddress(host, port);
            }
        }
        // 2.优先级2 classpath下的配置文件
        if(FileUtil.exist(CLASSPATH_RPC_PROPERTIES)){
            String host = new Props(CLASSPATH_RPC_PROPERTIES).getStr(PROPERTIES_HOST_KEY);
            Integer port = new Props(CLASSPATH_RPC_PROPERTIES).getInt(PROPERTIES_PORT_KEY);
            log.info("【zzz address】: [{}:{}]", host, port);
            if(StrUtil.isNotBlank(host) && port != null){
                log.info("read server address from classpath properties success, address: [{}:{}]", host, port);
                return new InetSocketAddress(host, port);
            }
        }
        // 3.优先级3 默认配置
        log.info("read default server address, address: [{}:{}]", DEFAULT_HOST, DEFAULT_PORT);
        return new InetSocketAddress(DEFAULT_HOST,DEFAULT_PORT);
    }

    /**
     * 从配置文件中获取zookeeper地址 默认127.0.0.1:6379
     * @return ZookeeperAddress
     */
    public static String getZookeeperAddress(){
        // 1.优先级1 项目部署路径下的配置文件
        if(FileUtil.exist(LOCAL_ZOOKEEPER_PROPERTIES)){
            String address = new Props(LOCAL_ZOOKEEPER_PROPERTIES).getStr("rpc.zookeeper.address");
            if(StrUtil.isNotBlank(address)){
                return address;
            }
        }
        // 2.优先级2 classpath下的配置文件
        if(FileUtil.exist(CLASSPATH_ZOOKEEPER_PROPERTIES)){
            String address = new Props("zookeeper.properties").getStr("rpc.zookeeper.address");
            if(StrUtil.isNotBlank(address)){
                return address;
            }
        }
        // 3.优先级3 默认配置
        log.info("read default zookeeper address, address: [{}]", DEFAULT_ZOOKEEPER_ADDRESS);
        return DEFAULT_ZOOKEEPER_ADDRESS;
    }

}
