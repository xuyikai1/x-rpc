package org.xuyk.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Xuyk
 * @Description: 注册的服务信息
 * @Date: 2020/12/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceProperties {

    /**
     * 提供服务的服务名称
     */
    private String serviceName;

}
