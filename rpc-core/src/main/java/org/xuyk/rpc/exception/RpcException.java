package org.xuyk.rpc.exception;

import lombok.Getter;

/**
 * @Author: Xuyk
 * @Description: rpc自定义异常类
 * @Date: 2020/12/19
 */
@Getter
public class RpcException extends RuntimeException  {

    private static final long serialVersionUID = -7650286793171720745L;

    private String message;

    public RpcException(String message) {
        super(message);
        this.message = message;
    }

}
