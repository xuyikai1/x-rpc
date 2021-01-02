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
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = -6556176442540372848L;
    /**
     * 响应code
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 请求ID 完整调用链的唯一标识
     */
    private String requestId;
    /**
     * 返回结果
     */
    private T data;
    /**
     * 返回异常
     */
    private Throwable throwable;

}
