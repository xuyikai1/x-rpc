package org.xuyk.rpc.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Xuyk
 * @Description: Rpc配置类
 * @Date: 2020/12/19
 */
@Getter
@Setter
public abstract class AbstractRpcConfig {


    /**
     * 请求ID
     */
    private String requestId;
    /**
     * 服务接口
     * 例如：org.xuyk.rpc.api.HelloService
     */
    private String interfaceClass = null;

}
