package org.xuyk.rpc.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Xuyk
 * @Description: channel缓存类
 * @Date: 2020/12/22
 */
@Slf4j
public class RpcChannelHolder {

    /**
     * 连接通道缓存
     * key:inetSocketAddress.toString
     * value:channel
     */
    private final Map<String, Channel> channelMap;

    public RpcChannelHolder() {
        channelMap = new ConcurrentHashMap<>();
    }

    /**
     * 根据连接信息获取channel
     * @param inetSocketAddress
     * @return
     */
    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        // 获取对应地址的channel 可用则直接返回 不可用则删除
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        return null;
    }

    /**
     * 获取所有channel
     * @return
     */
    public Collection<Channel> getAll(){
        return channelMap.values();
    }

    /**
     * set <连接,channel>
     * @param inetSocketAddress
     * @param channel
     */
    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        channelMap.put(inetSocketAddress.toString(), channel);
    }

    /**
     * 根据连接删除channel
     * @param inetSocketAddress
     */
    public void remove(InetSocketAddress inetSocketAddress) {
        channelMap.remove(inetSocketAddress.toString());
        log.info("channel map size :{}", channelMap.size());
    }

}
