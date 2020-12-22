package org.xuyk.rpc.factory;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.RuntimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Xuyk
 * @Description: 线程池工厂池类
 * @Date: 2020/12/17
 */
public class ThreadPoolExecutorFactory {

    private static final Integer CPU_COUNT = RuntimeUtil.getProcessorCount();

    /**
     * 吞吐量高的线程池缓存<businessName,ThreadPoolExecutor>
     */
    private static final Map<String, ThreadPoolExecutor> HIGH_TPS_THREAD_POOL = new HashMap<>();
    /**
     * 响应较快的线程池缓存<businessName,ThreadPoolExecutor>
     */
    private static final Map<String, ThreadPoolExecutor> FAST_RT_THREAD_POOL = new HashMap<>();

    private ThreadPoolExecutorFactory() {
    }

    /**
     * 返回吞吐量较高的线程池
     * @param businessName
     * @return
     */
    public static ThreadPoolExecutor getHighTpsThreadPoolExecutor(String businessName){
        if(HIGH_TPS_THREAD_POOL.containsKey(businessName)){
           return HIGH_TPS_THREAD_POOL.get(businessName);
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CPU_COUNT * 2,
                CPU_COUNT * 2,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new NamedThreadFactory(businessName, false));
        HIGH_TPS_THREAD_POOL.put(String.valueOf(businessName + "-"),threadPoolExecutor);
        return threadPoolExecutor;
    }
    /**
     * 返回处理较快的线程池
     * @param businessName
     * @return
     */
    public static ThreadPoolExecutor getFastThreadPoolExecutor(String businessName){
        if(FAST_RT_THREAD_POOL.containsKey(businessName)){
            return  FAST_RT_THREAD_POOL.get(businessName);
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,
                10,
                5,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory(businessName, false));
        FAST_RT_THREAD_POOL.put(businessName,threadPoolExecutor);
        return threadPoolExecutor;
    }

}
