package org.xuyk.rpc.utils;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Xuyk
 * @Description: 线程池工厂池类
 * @Date: 2020/12/17
 */
@Slf4j
public class ThreadPoolExecutorUtils {

    /**
     * cpu核心数
     */
    private static final Integer CPU_COUNT = RuntimeUtil.getProcessorCount();
    /**
     * 线程池缓存
     */
    private static final Map<String, ThreadPoolTaskExecutor> THREAD_POOL_MAP = new ConcurrentHashMap<>();

    private ThreadPoolExecutorUtils() {
    }

    /**
     * 返回吞吐量较高的线程池
     * @param businessName
     * @return
     */
    public static ThreadPoolTaskExecutor getHighTpsThreadPoolExecutor(String businessName){
        if(THREAD_POOL_MAP.containsKey(businessName)){
            return THREAD_POOL_MAP.get(businessName);
        }
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CPU_COUNT * 2);
        executor.setMaxPoolSize(CPU_COUNT * 2);
        executor.setKeepAliveSeconds(5);
        // capacity > 0 -> LinkedBlockingQueue
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix(businessName);
        THREAD_POOL_MAP.put(businessName,executor);
        return executor;
    }
    /**
     * 返回处理较快的线程池
     * @param businessName
     * @return
     */
    public static ThreadPoolTaskExecutor getFastThreadPoolExecutor(String businessName){
        if(THREAD_POOL_MAP.containsKey(businessName)){
            return THREAD_POOL_MAP.get(businessName);
        }
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setKeepAliveSeconds(5);
        // capacity = 0 -> SynchronousQueue
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix(businessName);
        THREAD_POOL_MAP.put(businessName,executor);
        return executor;
    }

    /**
     *  优雅关闭所有线程池
     */
    public static void shutDownAllThreadPool() {
        // 使用并行流释放线程池资源
        THREAD_POOL_MAP.entrySet().parallelStream().forEach(entry -> {
            ThreadPoolTaskExecutor executor = entry.getValue();
            executor.shutdown();
            log.info("shut down thread pool [{}] [{}]", entry.getKey());
        });
    }

}
