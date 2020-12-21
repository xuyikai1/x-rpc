package org.xuyk.rpc.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Xuyk
 * @Description: 服务端调用配置（provider）
 * @Date: 2020/12/19
 */
@Getter
@Setter
public class ProviderConfig extends AbstractRpcConfig {

    /**
     * 接口对象具体的实现类实例
     */
    private Object ref;

}
