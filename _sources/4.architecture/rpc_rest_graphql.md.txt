# RPC, REST, 还是 GraphQL


API 就是应用程序接口，它是提供给服务使用者的。如果是进程内的调用，通过函数调用就好了，如果是跨线程的函数调用，通过进程间通信 IPC 方法，例如管道(pipe)，信号(signal or semaphore)，消息队列(message queue), 共享内存(shared memory), UDS(Unix Domain Socket) 等方法。通常，我比较喜欢 UDS, 分布式系统中更多的是跨主机的通信，这时所提供的 API 的风格一般有三种

1. RPC
2. REST
3. GraphQL

最常用的传输层协议是 TCP, 应用层多基于 HTTP 或者是在 TCP 上封装的应用协议

## RPC

我用到的最早的远程过程调用方法是 Java 的 RMI(Remote Method Invocation), 现在使用的比较多的是 gRPC.
RPC 最初的理念是将远程调用模拟为本地调用。远程服务就是提供一个方法，只要调用方提供了方法名和所需的参数，那么就可远程调用这个方法了，但是远程方法调用与本地方法调用毕竟不同。

关于网络通信有 8 个著名的谎言

1. The network is reliable 网络是可靠的
2. Latency is zero 延迟几乎为零
3. Bandwidth is infinite 带宽是无限的
4. The network is secure 网络总是安全的
5. Topology doesn't change 网络拓扑结构是不变的
6. There is one administrator 总会有一个管理员
7. Transport cost is zero 传输成本几乎为零
8. The network is homogeneous 网络都是同质化的

相比本地调用，远程调用失败的可能性大大提高, 即使基于可靠性传输协议 TCP, 依然可能会有网络错误或超时

## REST

REST 是表述性状态传输 (Representational State Transfer) 的缩写。 REST 是 Roy Fielding 在 2000 年获得博士学位时设计的一种架构风格。 论文。 基本前提是开发人员使用标准的 HTTP 方法 GET、POST、PUT 和 DELETE 来查询和更改 Internet 上由 URI 表示的资源。 

### REST 的不足

* 面向资源的方式不适合复杂的业务逻辑处理

REST 实现 CRUD 很容易，而实现复杂的业务逻辑就力有未逮了

* REST 对于批量处理不友好

* REST 对于定制化的需求不方便

<hr/>
本作品采用[知识共享署名-非商业性使用-禁止演绎 4.0 国际许可协议](http://creativecommons.org/licenses/by-nc-nd/4.0/)进行许可。