package org.xuyk.rpc.client;

import org.xuyk.rpc.entity.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Xuyk
 * @Description: 待处理的请求队列
 * @Date: 2020/12/22
 */
public class RpcUnprocessedRequests {

    private static final Map<String, CompletableFuture<RpcResponse>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    /**
     * requestId对应的调用成功返回
     * @param rpcResponse
     */
    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (future != null) {
            // CompletableFuture的complete 任务执行完成后 设置返回的结果
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

}
