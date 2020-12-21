package org.xuyk.rpc.demo.protostuff;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: Xuyk
 * @Description:
 * @Date: 2020/12/18
 */
@Data
@Builder
public class Group {

    private String id;

    private String name;

    private User user;

}
