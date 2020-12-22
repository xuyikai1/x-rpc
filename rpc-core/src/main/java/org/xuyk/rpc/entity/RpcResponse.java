package org.xuyk.rpc.entity;

import lombok.Getter;
import lombok.Setter;
import org.xuyk.rpc.enums.RpcResponseCodeEnum;

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

    /**
     * 成功
     * @param data
     * @param requestId
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    /**
     * 失败
     * @param rpcResponseCodeEnum
     * @param <T>
     * @return
     */
    public static <T> RpcResponse<T> fail(RpcResponseCodeEnum rpcResponseCodeEnum) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCodeEnum.getCode());
        response.setMessage(rpcResponseCodeEnum.getMessage());
        return response;
    }

}
