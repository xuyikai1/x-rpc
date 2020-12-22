package org.xuyk.rpc.invoke.server;


import org.xuyk.rpc.invoke.client.HelloService;
import org.xuyk.rpc.invoke.client.User;

public class HelloServiceImpl implements HelloService {

	@Override
	public String hello(String name) {
		return "hello! name：" + name;
	}

	@Override
	public String hello(User user) {
		return "hello user：" + user.getName();
	}

}
