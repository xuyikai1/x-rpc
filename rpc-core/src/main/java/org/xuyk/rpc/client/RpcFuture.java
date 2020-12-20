package org.xuyk.rpc.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.client.callback.RpcCallback;
import org.xuyk.rpc.entity.RpcRequest;
import org.xuyk.rpc.entity.RpcResponse;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/19
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
public class RpcFuture implements Future<Object> {

    private RpcRequest request;

    private RpcResponse response;

    private RpcCallback rpcCallback;

    private long startTime;
    /**
     * 整体请求时间阈值
     */
    private static final long TIME_THRESHOLD = 5000;
    /**
     * 基于AQS自定义的锁
     */
    private Sync sync;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(65536));

    public RpcFuture(RpcRequest request) {
        this.request = request;
        this.startTime = System.currentTimeMillis();
        this.sync = new Sync();
    }

    /**
     * 	$done 实际的回调处理
     * @param response
     */
    void done(RpcResponse response) {
        this.response = response;
        // 释放许可
        boolean success = sync.release(1);
        if(success) {
            invokeCallbacks();
        }
        // 整体rpc调用的耗时
        long costTime = System.currentTimeMillis() - startTime;
        if(TIME_THRESHOLD < costTime) {
            log.warn("the rpc response time is too slow, request id = {}, cost time = {} ",this.request.getRequestId(),costTime);
        }
    }

    /**
     * 	依次执行回调函数处理
     */
    private void invokeCallbacks() {
        final RpcResponse response = this.response;
        executor.submit(() -> {
//            rpcCallback =
            if(response.getThrowable() == null) {
                rpcCallback.success(response.getResult());
            } else {
                rpcCallback.failure(response.getThrowable());
            }
        });
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        // 尝试获取许可
        sync.acquire(-1);
        if(this.response != null) {
            return this.response.getResult();
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // 尝试获取许 最长等待timeout时长
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if(success) {
            if(this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("timeout excetion requestId: "
                    + this.request.getRequestId()
                    + ",className: " + this.request.getClassName()
                    + ",methodName: " + this.request.getMethodName());
        }
    }

    /**
     * AQS 锁
     */
    class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = -3989844522545731058L;
        /**
         * 成功响应的状态
         */
        private final int done = 1;
        /**
         * 等待的状态
         */
        private final int pending = 0;

        /**
         * 获取许可(类似上锁的操作)
         * @param acquires
         * @return
         */
        @Override
        protected boolean tryAcquire(int acquires) {
            // 状态为1为获取许可 否则未获取
            return getState() == done;
        }

        /**
         * 释放许可(类似释放锁)
         * @param releases
         * @return
         */
        @Override
        protected boolean tryRelease(int releases) {
            if(getState() == pending) {
                return compareAndSetState(pending, done);
            }
            return false;
        }

        boolean isDone() {
            return getState() == done;
        }
    }


}
