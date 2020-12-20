package org.xuyk.rpc.client.callback;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/19
 */
public interface RpcCallback {

    /**
     * 异步调用响应成功
     * @param result
     */
    void success(Object result);
    /**
     * 异步调用响应失败
     * @param cause
     */
    void failure(Throwable cause);

}
