package org.xuyk.rpc.annotation;

import java.lang.annotation.*;

/**
 * @Author: Xuyk
 * @Description: 为被该注解标识的属性装配代理
 * @Date: 2020/12/30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {
}
