## SpringCloudEureka.md
1. 服务注册发现对应的就是下面简单的微服务架构中的服务注册发现<br/>![WeChat783988aa099b79312bd9e25edc224c97.png](https://i.loli.net/2019/06/27/5d142f3eb810312078.png)
2. 基于Netflix Eureka做了二次封装
3. 两个组件组成：
	* Eureka Server 注册中心，这里是服务的注册中心
	* Eureka Client 服务注册，系统中的其他的微服务使用的是Eureka的客户端，连接到Eureka Server，并维持心跳连接，这样就能监控系统中各的微服务是否正常运行

### 一、Eureka Server(注册中心)
1. 注册中心就好比上学老师手中的名单，上面有班级里所有同学的名字，要点名的时候，就掏出那份名单
2. 到了微服务这里，就是记录所有微服务的信息和状态，应用的名字，在哪台服务器上，目前是不是正常工作
3. 在微服务架构里面，习惯将一个应用称之为一个服务
4. Eureka是找到了有了的意思
5. 使用idea ultimate版本

#### 1.1 新建
1. 在idea中选择Spring Discovery中的eureka的Server即可，然后生成项目
2. 生成项目之后，生成的pom文件，可以查看里面的parent是springboot，看一下版本，顺路看一下dependencyManagement，中的springcloud的版本
3. 依照视频中的说法，在[https://spring.io/projects/spring-cloud](https://spring.io/projects/spring-cloud)的网站下方，eureka的组件隶属于spring-cloud-netflix，里面有对应的建议版本选择，但是我没太看明白
4. 直接启动是访问不了8080端口的，没有东西的，需要在main函数入口的类上添加`@EnableEurekaServer`注解
5. 这个时候log中会报错，其中有一个连接不上client的错误，还会显示一个url是`serviceUrl='http://localhost:8761/eureka/`
6. 访问的这个eureka的主界面的是，localhost:8080，可以进入到SpringEureka的界面
7. **Instances currently registered with Eureka**，表示要注册到注册中心的一些应用
8. DS Replicas，表示server端有几个应用
9. 运行的时候的错误会一致报错误，因为进入到这个注解里面可以看到这个注解里面也有一个client，也就是这个server也需要注册到一个注册中心上

#### 1.2 配置application.yml
1. 该服务也是属于一个微服务，其中设置微服务的名称为：`spring.application.name: eureka`
2. 设置端口：`server.port: 8761`
3. 不让注册中心显示在application列表中，`eureka.client.register-with-eureka: false`
4. 在将上面的参数调整至true之后，设置了名称和port之后，运行项目会发现EUREKA的应用出现在了applications的列表中


### 二、Eureka Client(服务注册)
1. 本质上就是一个一个应用或者服务


#### 2.1 新建
1. 也是在SpringDiscovery中的Eureka Discovery，然后选择路径创建即可，实际上就是在pom上引入了eureka-client的依赖
2. **第一件事情同样是检查版本，确保server和client的版本统一**

#### 2.2 配置
1. 配置application.yml
2. `eureka.client.server-url.default-zone: http:localhost:8761/eureka`，也就是上面的server的地址
3. 在主类的main方法上面添加注解，`@EnableEurekaClient`的注解
4. 添加应用的名字，在yml添加，`spring.application.name: client`
5. 可以添加自定义的连接来替换ip地址，在yml中配置`eureka.instance.hostname: clientName`
6. 如果client的服务不停的重启的话，在eureka上面会出现警告，如果需要关闭的话，需要在server端，添加`eureka.server.enable-self-preservation: false`，这个只能在开发环境关闭，不能在生产环境关闭，这个是一个自我保护的机制


### 三、Eureka Server的高可用
1. 如果只有一个eureka的server话，那么会存在eureka若是挂掉，则后面的服务会无法使用的情况，所以要实现Eureka的高可用
2. 可以实现的方案是启动两个或多个eureka，然后两个eureka互相注册

#### 3.1 操作方法
1. 在IDEA的右上角的编译环境中打开编译环境，点击复制eureka，出现两个eureka server，然后分别取名，一个为EurekaApplication1，一个为EurekaApplication2
2. 设置两个eureka的端口，在environment上面添加**VM options**的参数为：`-Dserver.port=8761`，另一个端口号设置为8762
3. 在运行的时候选择不同的编译环境，application1的，设置server-url为http://localhost:8762/eureka/， application2的，设置server-url为http://localhost:8761/eureka/，分别启动，然后查看不同的环境下url下的application的列表。
4. 这样一个client服务直接注册在其中的一个eureka上面之后，就会在两个eureka上面都可以看到这个服务，实现eureka服务的高可用。<br/>![WeChat7612d0687f830c6fcb8502e2425bf80c.png](https://i.loli.net/2019/06/27/5d1477a9a229996184.png)
5. 这个时候client注册在Eureka1，并且的停掉了Eureka1之后，Eureka2的application的列表中依旧还是有client的信息，但是当client挂掉之后重启，因为client的yml中只是有Eureka1的的地址信息，这样就注册不了Eureka2了，也就完蛋了，**解决办法**，就是client同时注册两个Eureka Server，这样谁挂掉重启都没得问题

#### 3.2 多个Eureka高可用
1. 可以先让Eureka两两注册，让Client直接同时注册到上面的所有的Eureka上面。<br/>![WeChata4cf70891fc7508458719e194e246306.png](https://i.loli.net/2019/06/27/5d14795ee8f6755108.png)
2. 生产环境尽量这样部署，开发环境部署一个就好了


### 四、总结
1. @EnableEurekaServer、@EnableEurekaClient分别启动Server和Client
2. 提供心跳检测、健康检查、负载均衡等功能
3. Eureka的高可用，生产上建议至少两台以上
4. 分布式系统中，服务注册中心是最重要的基础部分

#### 4.1 为什么分布式系统中需要服务发现
1. 技术需要解决的背景：
	1. 若存在两个服务，分别有各自的地址，这样就可以直接在服务中增加对应的ip地址即可
	2. 但是分布式系统中，多个自制的处理元素不共享主内存，所以在分布式系统中，A、B都是多节点的，这样增加配置就是一件很麻烦的事情
	3. 同时在很多服务是根据流量的访问大小来调节A、B两个服务的集群的数量的，这样就会通过配置ip就会很难操作 

2. 解决办法
	1. 这个时候，就会必然出现一个服务中心的角色，所有的B服务ip都会上报到服务中心，然后A服务要是想调用B服务，可以直接去找服务中心即可
	2. 这个注册中心就类似于饭店的服务员，服务B就是炒菜的厨师，服务A就是顾客，想点菜都需要通过服务人员来点，若是饭店人突然多，就多找几个厨师，所以有问题直接就找服务人员就可以了
	3. 服务注册中心就是集群的解决方案，访问多个B的时候就是使用负载均衡的机制(ip、Hash、权重、轮训)等等，

3. 服务发现的两种方式，在客户端挑选叫客户端发现，在服务端叫服务端发现
	1. 客户端发现，简单直接不需要代理的介入，同时客户端是知道所有的服务端的可用的实际的地址的，缺点也很明显，A服务得自己去实现一套逻辑，把B挑出来，
	2. 服务端发现，通过代理的方式，代替A找B，这样B就是对A透明不可见的，A服务只需要找到代理就可以，比如nginx，Zookeeper、Kubernetes
	3. Eureka是客户端发现

4. 微服务的特点：**异构**
	* 不同语言
	* 不同类型的数据库

5. SpringCloud的服务调用方式
	* REST还是RPC
	* nodejs实现了eureka-js-client