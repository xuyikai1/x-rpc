package org.xuyk.rpc.example.api;

import org.xuyk.rpc.example.common.User;

/**
 * @Author: Xuyk
 * @Description: user接口
 * @Date: 2020/12/22
 */
public interface UserService {

    /**
     * 根据userId获取username
     * @param userId
     * @return
     */
    String getUsername(String userId);

    /**
     * 根据userId获取user
     * @param userId
     * @return
     */
    User getUser(String userId);

}
