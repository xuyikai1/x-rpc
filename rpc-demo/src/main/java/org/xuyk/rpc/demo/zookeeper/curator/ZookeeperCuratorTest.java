package org.xuyk.rpc.demo.zookeeper.curator;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/23
 */
@Slf4j
public class ZookeeperCuratorTest {

    public static void main(String[] args) {
        String serviceName = "UserService";
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",2181 );
        InetSocketAddress inetSocketAddress2 = new InetSocketAddress("127.0.0.1",2182 );

        // 1.在服务节点下 创建服务端地址节点
        ZookeeperClient.createPersistentNode(serviceName,inetSocketAddress);

        // 2.获取服务的子节点
        List<String> childrenNodes = ZookeeperClient.getChildrenNodes(serviceName);
        log.info("childrenNodes:{}", JSONUtil.toJsonStr(childrenNodes));

        // 3.再新增一个结点 查看是否监听器会有作用
        ZookeeperClient.createPersistentNode(serviceName,inetSocketAddress2);

        // 4.服务器关闭时 删除当前服务器地址的注册信息
        ZookeeperClient.clearRegistry(inetSocketAddress);
        ZookeeperClient.clearRegistry(inetSocketAddress2);

        // 关闭客户端
        ZookeeperClient.close();
    }

}
