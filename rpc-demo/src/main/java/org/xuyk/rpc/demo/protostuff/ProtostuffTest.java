package org.xuyk.rpc.demo.protostuff;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Xuyk
 * @Description: Protostuff序列化/反序列化 测试类
 * @Date: 2020/12/18
 */
@Slf4j
public class ProtostuffTest {

    public static void main(String[] args) {
        // 前提：发送接收两端都是使用java语言 如果异构 则需要使用protobuff
        // 创建一个user对象
        User user = User.builder().id("1").age(20).name("张三").desc("programmer").build();
        // 创建一个Group对象
        Group group = Group.builder().id("1").name("分组1").user(user).build();
        // 使用ProtostuffUtils序列化
        byte[] data = ProtostuffUtils.serialize(group);
        log.info("序列化后 码流:{}", JSONUtil.toJsonStr(data));
        Group result = ProtostuffUtils.deserialize(data, Group.class);
        log.info("反序列化后:{}",JSONUtil.toJsonStr(result));
    }

}
