package org.xuyk.rpc.annotation;

import org.springframework.context.annotation.Import;
import org.xuyk.rpc.spring.ScannerRegistrar;

import java.lang.annotation.*;

/**
 * @Author: Xuyk
 * @Description: 扫描rpc自定义注解与spring注解
 * @Date: 2020/12/30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ScannerRegistrar.class)
@Documented
public @interface RpcScan {

    /**
     * 扫描路径
     * @return
     */
    String[] basePackage();

}
