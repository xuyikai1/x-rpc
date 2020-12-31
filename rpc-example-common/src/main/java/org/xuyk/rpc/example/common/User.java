package org.xuyk.rpc.example.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: Xuyk
 * @Description: 会员实体类
 * @Date: 2020/12/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 7689012940049735217L;
    /**
     * 会员ID
     */
    private String userId;
    /**
     * 会员名称
     */
    private String userName;

}
