package org.xuyk.rpc.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.xuyk.rpc.server.RpcServer;

import java.util.List;

/**
 * 	$RpcServerConfig 服务器端启动配置类
 * @author 17475
 *
 */
@Slf4j
@Getter
@Setter
public class RpcServerConfig {

	private String host;
	
	protected int port;
	
	private List<ProviderConfig> providerConfigs;
	
	private RpcServer rpcServer = null;
	
	public RpcServerConfig(List<ProviderConfig> providerConfigs) {
		this.providerConfigs = providerConfigs;
	}
	
	public void exporter() {
		if(rpcServer == null) {
			try {
				rpcServer = new RpcServer(host,port);
				rpcServer.startup();
			} catch (Exception e) {
				log.error("RpcServerConfig exporter exception: ", e);
			}
			
			for(ProviderConfig providerConfig: providerConfigs) {
				rpcServer.registerProcessor(providerConfig);
			}
		}
		// zk
		//	把所有服务提供的列表注册到zk上
		/** zk节点树
		 *	/dubbo (x-rpc)
		 *		/interface: com.bfxy.service.HelloService  服务端提供的服务
		 *			/providers 提供服务的服务端
		 *				/ip:port (192.168.11.101:8765)
		 *				/ip:port (192.168.11.102:8765)
		 *			/consumers 使用服务的客户端
		 * 				/ip:port
		 * 				/ip:port
		 */
	}

}
