package org.xuyk.rpc.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: Xuyk
 * @Description: Rpc请求类
 * @Date: 2020/12/18
 */
@Getter
@Setter
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1376282793094544652L;

    /**
     * 请求ID
     * 异步调用时 服务器回调响应时 用于找到实际处理器的标识
     */
    private String requestId;
    /**
     * 类名
     */
    private String className;
    /**
     * 调用方法
     */
    private String methodName;
    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;
    /**
     * 参数值列表
     */
    private Object[] parameters;

}
