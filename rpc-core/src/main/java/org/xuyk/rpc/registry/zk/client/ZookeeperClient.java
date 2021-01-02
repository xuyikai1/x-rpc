package org.xuyk.rpc.registry.zk.client;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.xuyk.rpc.utils.ResourcesUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Xuyk
 * @Description: zookeeper客户端 curator
 * @Date: 2020/12/23
 */
@Slf4j
public class ZookeeperClient {

    /**
     * 客户端连接 尝试基本睡眠时间 单位毫秒
     */
    private static final int BASE_SLEEP_TIME = 1000;
    /**
     * 客户端连接 最大尝试次数
     */
    private static final int MAX_RETRIES = 3;
    /**
     * zookeeper注册根节点
     */
    private static final String ZK_REGISTER_ROOT_PATH = "/x-rpc/";
    /**
     * 注册服务地址缓存
     * key：注册服务地址节点 value：发布该服务的服务器地址列表
     */
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    /**
     * 已注册服务列表
     */
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    /**
     * 已监听服务节点列表
     */
    private static final Set<String> WATCHER_NODE_SET = ConcurrentHashMap.newKeySet();
    /**
     * zookeeper客户端 curator
     */
    private static CuratorFramework zkClient;
    /**
     * 默认连接服务端zk地址
     */
    public static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    /**
     * 静态代码块 类初始化时发起连接
     */
    static {
        String address = ResourcesUtils.getZookeeperAddress();
        // 重试策略 重试三次 分别等待1000ms/2000ms/4000ms后重试
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        // curator客户端
        zkClient = CuratorFrameworkFactory.builder()
                // 连接服务端地址 格式：ip1:port1,ip2:port2...
                .connectString(address)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
    }

    /**
     * 创建持久节点
     * 客户端连接断开节点不会像临时节点一样被删除
     * @param serviceName serviceName
     * @param inetSocketAddress server address
     */
    public static void createPersistentNode(String serviceName,InetSocketAddress inetSocketAddress) {
        // 例 /x-rpc/org.xuyk.rpc.HelloService/127.0.0.1:9999
        String nodePath = ZK_REGISTER_ROOT_PATH + serviceName + inetSocketAddress.toString();
        try {
            if (REGISTERED_PATH_SET.contains(nodePath) || zkClient.checkExists().forPath(nodePath) != null) {
                log.info("The node already exists. The node is:[{}]", nodePath);
                return;
            }
            // 创建持久节点
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(nodePath);
            log.info("The node was created successfully. The node is:[{}]", nodePath);
            REGISTERED_PATH_SET.add(nodePath);

            // 添加服务节点的监听
            if(!WATCHER_NODE_SET.contains(serviceName)){
                registerWatcher(serviceName);
                WATCHER_NODE_SET.add(serviceName);
            }
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail:{}", nodePath, e);
        }
    }

    /**
     * 获取指定节点下的所有子节点信息
     * @param serviceName rpc serviceName
     * @return nodesNameList
     */
    public static List<String> getChildrenNodes(String serviceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(serviceName)) {
            return SERVICE_ADDRESS_MAP.get(serviceName);
        }
        List<String> result = null;
        // 例如 /x-rpc/org.xuyk.rpc.HelloService
        String servicePath = ZK_REGISTER_ROOT_PATH + serviceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName, result);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    /**
     * 给特定节点注册监听器 节点发生变化触发对应操作
     * @param serviceName 注册服务名
     */
    private static void registerWatcher(String serviceName) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + serviceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName, serviceAddresses);
            log.info("node's children change: {}", JSONUtil.toJsonStr(serviceAddresses));
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
        log.info("service [{}] register Watcher success", serviceName);
    }

    /**
     * 当服务器关闭时 删除服务器在zk注册的信息
     */
    public static void clearRegistry() {
        // 使用并行流依次删除已注册的服务信息
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                // 当前服务端地址
                InetSocketAddress address = ResourcesUtils.getServerAddress();
                if (p.endsWith(address.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }

    /**
     * 关闭zk客户端
     */
    public static void close(){
        zkClient.close();
    }

}
