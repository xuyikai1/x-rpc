package org.xuyk.rpc.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Xuyk
 * @Description: 客户端调用配置（consumer）
 * @Date: 2020/12/19
 */
@Getter
@Setter
public class ConsumerConfig extends AbstractRpcConfig {

    /**
     * 服务的调用方
     */
    private Class<?> proxyClass = null;

}
