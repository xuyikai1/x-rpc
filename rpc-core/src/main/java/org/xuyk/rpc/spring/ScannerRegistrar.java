package org.xuyk.rpc.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;
import org.xuyk.rpc.annotation.RpcScan;
import org.xuyk.rpc.annotation.RpcService;

/**
 * @Author: Xuyk
 * @Description: 扫描注册 自定义注解
 * @Date: 2020/12/30
 */
@Slf4j
public class ScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    /**
     * 扫描spring注解路径
     */
    private static final String SPRING_BEAN_BASE_PACKAGE = "org.xuyk.rpc.spring";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取RpcScan注解的属性列表 basePackage..
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        if (rpcScanAnnotationAttributes != null) {
            // 获取basePackage的属性值
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }
        // 扫描RpcService注解
        CustomAnnotationScanner rpcServiceScanner = new CustomAnnotationScanner(beanDefinitionRegistry, RpcService.class);
        // 扫描Component注解
        CustomAnnotationScanner springBeanScanner = new CustomAnnotationScanner(beanDefinitionRegistry, Component.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }
        int springBeanCount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("扫描spring注解数量：[{}]，扫描路径:{}", springBeanCount, SPRING_BEAN_BASE_PACKAGE);
        int rpcServiceCount = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("扫描x-rpc自定义注解数量 [{}]，扫描路径:{}", rpcServiceCount, rpcScanBasePackages);

    }

}
