package org.xuyk.rpc.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Xuyk
 * @Description: 响应类
 * @Date: 2020/12/17
 */
@Data
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -6556176442540372848L;

    private String requestId;

    private Object result;

    private Throwable throwable;

}
