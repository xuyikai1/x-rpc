package org.xuyk.rpc.invoke.provider.test;


import org.xuyk.rpc.config.ProviderConfig;
import org.xuyk.rpc.config.RpcServerConfig;

import java.util.ArrayList;
import java.util.List;

public class ProviderStarter {

	public static void main(String[] args) {
		
		//	服务端启动
		new Thread(() -> {
			try {
				// 每一个具体的服务提供者的配置类
				ProviderConfig providerConfig = new ProviderConfig();
				providerConfig.setInterfaceClass("org.xuyk.rpc.invoke.consumer.test.HelloService");
				// 整合spring则可使用自定义注解的方式注入到容器中再关联
				HelloServiceImpl helloServiceImpl = HelloServiceImpl.class.newInstance();
				providerConfig.setRef(helloServiceImpl);

				//	把所有的ProviderConfig 添加到集合中
				List<ProviderConfig> providerConfigs = new ArrayList<>();
				providerConfigs.add(providerConfig);

				RpcServerConfig rpcServerConfig = new RpcServerConfig(providerConfigs);
				rpcServerConfig.setHost("127.0.0.1");
				rpcServerConfig.setPort(8765);
				rpcServerConfig.exporter();

			} catch(Exception e){
				e.printStackTrace();
			}
		}).start();
		
	}
}
