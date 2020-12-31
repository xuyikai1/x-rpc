package org.xuyk.rpc.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @Author: Xuyk
 * @Description: 自定义注解扫描器 用于添加自定义注解的扫描
 * @Date: 2020/12/30
 */
public class CustomAnnotationScanner extends ClassPathBeanDefinitionScanner {

    /**
     * 添加除了spring的@Compenant等注解外的自定义注解过滤器
     * @param registry
     * @param annotationType
     */
    public CustomAnnotationScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annotationType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annotationType));
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }

}
