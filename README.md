# x-rpc

> 加载不全试试[一款自定义RPC框架 x-rpc \| 寒暄](http://xuyk.top/posts/x-rpc.html)

## 1.介绍

在微服务大行其道的2021，说到服务之间的通信，大家都会马上想到RPC（Remote Procedure Call Protocol）远程方法调用，也就是可以让我们调用远程第三方服务时能像调用本地方法一样简单快捷省事儿。其中被大家所熟知的就有SpringCloud中的Feign、Alibaba的Dubbo和谷歌的gRpc等。为了对这些框架有更深刻的了解，笔者自己尝试造了个轮子，也就是x-rpc。

x-rpc是一款基于 Netty+protostuff+Zookeeper 实现的 RPC 框架，框架大体结构参考的是dubbo：

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/dubbo-architure.jpg)

> 服务提供方（服务端）启动，向注册中心注册所要发布的服务列表；消费端（客户端）启动，向注册中心订阅/拉取所关心的服务，也就是进行服务发现；消费端发起请求，服务端响应。

简单说明一下x-rpc的设计思路：

1. **网络传输** ：netty作为**底层通信**框架，能够在简化网络编程的同时压榨cpu，充分提高应用的性能，是网络通信框架的不二之选。
2. **注册中心** ：天生就用来作为分布式系统下的注册中心、统一配置管理中心、命名服务中心等功能的zookeeper毋庸置疑承担了**服务发现**和**服务注册**的角色
3. **序列化** ：因为JDK自带的序列化效率很低并且有安全问题，所有这里采用谷歌的protostuff负责**数据包的序列化和反序列化**，当然kryo、Marshalling也是不错的选择
4. **动态代理** ： 使用动态代理可以屏蔽远程方法调用的细节比如网络传输。也就是说当你调用远程方法的时候，实际会通过代理对象来传输网络请求。
5. **负载均衡**：这里采用的是客户端负载均衡，通过轮询对服务的每个服务端节点进行依次请求，避免单机压力过大，达到负载均衡效果。

## 2.计划列表

**已优化列表**
- [x] 重用 Channel 避免重复连接服务端
- [x] 使用 zookeeper 进行服务发现与注册
- [x] 心跳链路检测
- [x] 使用 CompletableFuture 包装接收服务端返回结果
- [x] 客户端调用远程服务进行负载均衡，x-rpc默认使用**轮询**进行负载均衡
- [x] 集成spring

**代办列表**
- [ ] 数据传输时的数据包重新设计：加入魔数（识别之用）、采用序列化方式编号等
- [ ] 对数据包进行压缩，例如gzip压缩
- [ ] SPI 机制
...

## 3.图解说明

下面是整个x-rpc的代码架构，基于maven构建

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-projects-desc.png)

正常在使用过程中，遵循如下顺序：

1.服务端启动 -> 2.客户端启动 -> 3.客户端服务端整个请求流程（包括客户端发送请求 -> 服务端响应请求 -> 客户端处理响应数据）

我们就来一一剖析，进一步理解rpc底层大概都干了啥

### 3.1 服务端启动

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-serverStartup.jpg)

1. 【具体操作】：通过rpc自定义注解@RpcService（@RpcScan属性basePackages范围内）找到所要发布的服务列表，通过扫描注册的方式将服务实例注入到spring bean中
2. 【具体操作】：在服务实例初始化前，每个注册在spring的实例都会通过zookeeper客户端Curator创建持久节点注册服务，保存服务名和服务端地址，并对该节点进行监听，发生变化时
3. 【具体操作】：使用ConcurrentHashMap和Set缓存已注册的服务列表，用于后续客户端获取服务列表使用
4. 【具体操作】：创建启动Netty服务端，异步监控启动结果，添加shutdownHook用于应用关闭释放资源，服务端pipeline用于请求/响应信息的编解码、客户端请求处理等

### 3.2 客户端启动

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-clientStartup.jpg)

1. 【具体操作】：通过@RpcScan属性basePackages，把该范围内的标识spring注解的类注入至spring bean；在bean初始化后，为标识了@RpcReference的属性添加proxy代理实例，之后调用该属性方法时会调用代理类的invoke方法
2. pipeline用于请求/响应信息的编解码、服务端响应结果处理等

### 3.3 整个调用流程

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-wholeRequestProcess.jpg)

> 【server/client pipeline】：netty中的调用链，用到了设计模式中的责任链模式

## 4.运行项目

### 4.1 将项目 clone 至本地

```java
git clone https://github.com/xuyikai1/x-rpc.git
```

### 4.2 部署运行zookeeper 3.5.8，推荐使用docker

下载：

```java
docker pull zookeeper:3.5.8
```
运行：

```java
docker run -d --name zookeeper -p 2181:2181 
```

zookeeper:3.5.8

### 4.3 使用 maven 命令 install 整个x-rpc项目

这里使用IDEA进行操作：

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-mavenInstall.png)

### 4.4 启动服务端

运行 rpc-example-server 中的 RpcServerTest1的`main()`方法启动服务端

服务端启动成功如图：

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-serverStartupSuccess.png)

zookeeper节点情况：

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-prettyZoo.png)

> zookeeper client使用的是 [prettyZoo](https://github.com/vran-dev/PrettyZoo)

### 4.5 启动客户端/发送请求

运行 rpc-example-client 中的 RpcServerTest1的`main()`方法启动服务端

客户端启动/发送数据情况：

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-clientStartupSuccess.png)

![](https://xuyk-picture-bed.oss-cn-beijing.aliyuncs.com/x-rpc-serverReceiveRequestSuccess.png)

> 这里测试demo中客户端发送了两次请求

## 5.源码

[GitHub - xuyikai1/x-rpc](https://github.com/xuyikai1/x-rpc)

## 6.感谢

项目很多细节都是参考 javaGuide 的[guide-rpc-framework](https://github.com/Snailclimb/guide-rpc-framework)，在这里表示感谢
