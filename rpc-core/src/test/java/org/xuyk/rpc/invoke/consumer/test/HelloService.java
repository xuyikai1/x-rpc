package org.xuyk.rpc.invoke.consumer.test;

public interface HelloService {

    String hello(String name);

    String hello(User user);

}
