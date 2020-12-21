package org.xuyk.rpc.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: Xuyk
 * @Description: Rpc响应类
 * @Date: 2020/12/17
 */
@Getter
@Setter
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -6556176442540372848L;

    /**
     * 请求ID
     * 异步调用时 服务器回调响应时 用于找到实际处理器的标识
     */
    private String requestId;
    /**
     * 返回结果
     */
    private Object result;
    /**
     * 返回异常
     */
    private Throwable throwable;

}
