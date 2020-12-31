package org.xuyk.rpc.spring;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.xuyk.rpc.annotation.RpcReference;
import org.xuyk.rpc.annotation.RpcService;
import org.xuyk.rpc.client.RpcClientProxy;
import org.xuyk.rpc.factory.SingletonFactory;
import org.xuyk.rpc.server.RpcServiceHolder;

import java.lang.reflect.Field;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/30
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final RpcServiceHolder rpcServiceHolder;

    public SpringBeanPostProcessor(){
        this.rpcServiceHolder = SingletonFactory.getInstance(RpcServiceHolder.class);
    }

    /**
     * bean初始化前 注册标注了@RpcService的服务
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            this.rpcServiceHolder.publishService(bean);
        }
        return bean;
    }

    /**
     * bean初始化后 为标注了@RpcReference的属性注入rpcClient代理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcClientProxy rpcClientProxy = new RpcClientProxy();
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

}
