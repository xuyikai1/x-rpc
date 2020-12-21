package org.xuyk.rpc.invoke.consumer.test;


import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.client.RpcClient;
import org.xuyk.rpc.client.RpcFuture;
import org.xuyk.rpc.client.proxy.RpcAsyncProxy;

import java.util.concurrent.ExecutionException;

@Slf4j
public class ConsumerStarter {
	
	public static void sync() {
		//	rpcClient
		RpcClient rpcClient = new RpcClient();
		// 整合zk后 ip和服务列表应该从zk中拉取
		rpcClient.initClient("127.0.0.1:8765", 3000);
		HelloService helloService = rpcClient.invokeSync(HelloService.class);
		String result = helloService.hello("zhang3");
		log.info("result:{}",result);
		rpcClient.stop();
	}
	
	public static void async() throws InterruptedException, ExecutionException {
		RpcClient rpcClient = new RpcClient();
		rpcClient.initClient("127.0.0.1:8765", 3000);
		RpcAsyncProxy proxy = rpcClient.invokeAsync(HelloService.class);
		RpcFuture future = proxy.call("hello", "li4");
		RpcFuture future2 = proxy.call("hello", new User("001", "wang5"));

		Object result = future.get();
		Object result2 = future2.get();
		log.info("result:{}",result);
		log.info("result2:{}",result2);
		rpcClient.stop();
	}
	
	public static void main(String[] args) throws Exception {
		sync();
	}
}
