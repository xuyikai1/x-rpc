package org.xuyk.rpc.client.proxy;

import org.xuyk.rpc.client.RpcFuture;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/20
 */
public interface RpcAsyncProxy {

    RpcFuture call(String funcName, Object... args);

}
