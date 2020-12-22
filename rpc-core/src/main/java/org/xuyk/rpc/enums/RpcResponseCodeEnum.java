package org.xuyk.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/22
 */
@Getter
@AllArgsConstructor
public enum RpcResponseCodeEnum {

    /**
     * 远程调用返回成功
     */
    SUCCESS(200, "远程调用返回成功"),
    /**
     * 远程调用返回失败
     */
    FAIL(500, "远程调用返回失败");
    private final int code;

    private final String message;

}
