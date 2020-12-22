package org.xuyk.rpc.invoke.client;

public interface HelloService {

    String hello(String name);

    String hello(User user);

}
