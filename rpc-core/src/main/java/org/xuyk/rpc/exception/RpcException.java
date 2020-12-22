package org.xuyk.rpc.exception;

import lombok.Getter;
import org.xuyk.rpc.enums.RpcErrorMessageEnum;

/**
 * @Author: Xuyk
 * @Description: rpc自定义异常类
 * @Date: 2020/12/19
 */
@Getter
public class RpcException extends RuntimeException  {

    private static final long serialVersionUID = -7650286793171720745L;

    public RpcException(String message) {
        super(message);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }

}
