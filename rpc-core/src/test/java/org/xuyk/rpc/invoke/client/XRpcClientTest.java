package org.xuyk.rpc.invoke.client;


import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.client.RpcClientProxy;

@Slf4j
public class XRpcClientTest {
	
	public static void main(String[] args) {
		RpcClientProxy clientProxy = new RpcClientProxy();
		HelloService helloService = clientProxy.getProxy(HelloService.class);
		String result1 = helloService.hello("张三");
		log.info("result1:{}",result1);

		User user = new User();
		user.setName("李四");
		String result2 = helloService.hello(user);
		log.info("result2:{}",result2);
	}

}
