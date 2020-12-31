package org.xuyk.rpc.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import org.xuyk.rpc.server.RpcServer;

import java.net.InetSocketAddress;

/**
 * @Author: Xuyk
 * @Description: 读取服务端地址 关联rpc.properties配置文件
 * @Date: 2020/12/30
 */
public class ServerNetUtils {

    /**
     * rpcServer配置文件 classpath下（/resources）
     */
    private static final String RPC_PROPERTIES = "classpath:rpc.properties";
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
     * 从配置文件rpc.properties中 获取获取服务端 host
     * @return host
     */
    public static String getServerHost(){
        if(!FileUtil.exist(RPC_PROPERTIES)){
            return DEFAULT_HOST;
        }
        String host = new Props(RPC_PROPERTIES).getStr(PROPERTIES_HOST_KEY);
        return StrUtil.isBlank(host) ? DEFAULT_HOST : host;
    }

    /**
     * 从配置文件rpc.properties中 获取服务端 port 默认9998
     * @return port
     */
    public static Integer getServerPort(){
        if(!FileUtil.exist(RPC_PROPERTIES)){
            return DEFAULT_PORT;
        }
        Integer port = new Props(RPC_PROPERTIES).getInt(PROPERTIES_PORT_KEY);
        return port == null ? DEFAULT_PORT : port;
    }

    /**
     * 从配置文件rpc.properties中 获取服务端地址
     * @return
     */
    public static InetSocketAddress getServerAddress(){
        if(!FileUtil.exist(RPC_PROPERTIES)){
            return new InetSocketAddress(DEFAULT_HOST,DEFAULT_PORT);
        }
        return new InetSocketAddress(getServerHost(),getServerPort());
    }

}
