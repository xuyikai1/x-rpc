package org.xuyk.rpc.invoke.provider.test;


import org.xuyk.rpc.invoke.consumer.test.HelloService;
import org.xuyk.rpc.invoke.consumer.test.User;

public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		return "hello! " + name;
	}

	@Override
	public String hello(User user) {
		return "hello! " + user.getName();
	}

}
