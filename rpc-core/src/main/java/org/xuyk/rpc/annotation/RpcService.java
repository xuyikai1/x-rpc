package org.xuyk.rpc.annotation;

import java.lang.annotation.*;

/**
 * @Author: Xuyk
 * @Description: rpc服务 标记实现接口的类
 * @Date: 2020/12/30
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {
}
