## 1. 
#### 一、简介
1. 历史背景
	* 诞生于2002年，成型于2003年，作者为Rod Johnson
		* 《Expert One-on-One J2EE Design and Development》
		* 《Expert One-on-One J@EE Development without EJB》
	* 目前已经发展到了Spring5.X版本，支持JDK8-11及Java EE 8 
2. spring的框架
	* Spring Framework
	* Spring 相关项目
	* 整个Spring家族
3. 官网: [https://spring.io/projects](https://spring.io/projects)
4. Spring Framework
	* 用于构建企业级应用的轻量级一站式解决方案
	* 设计理念
		* 力争让选择无处不在
		* 体现海纳百川的精神
		* 保持向后的兼容性
		* 专注API设计
		* 追求严苛的代码质量

5. Spring Boot
	* 快速构建基于Spring的应用程序
		* 快、很快、非常快
		* 进可开箱即用，退可按需改动
		* 提供各种非功能特性
		* 不用生成代码，没有xml配置
	* 还有SpringData、SpringMVC、Spring WebFlux...

6. Spring Cloud
	* 简化分布式系统的开发
	* 服务注册与发现
	* 熔断
	* 服务追踪等

#### 二、拓展
1. Spring5.x改变暗示了什么？<br/>![WeChatb09733d00f6e222f0cec6dacaffa9d5b.png](https://i.loli.net/2019/05/21/5ce36f874c33f13225.png)
2. Spring Boot和SpringCloud的出现
	* 开箱即用
	* 与生态圈的深度整合
	* 注重运维
	* CloudNative的大方向
	* 最佳实践不嫌多，固化到系统实现中才是王道等

	

#### 三、生成Spring项目
1. 进入到[https://start.spring.io/](https://start.spring.io/)界面，根据界面提示，可以定义group和artifact的id，以及依赖的包，这里添加了web、actuator包，定义之后会点击生成会自动生成一个包，使用idea打开即可
2. curl http://localhost:8080/hello，判断返回的是否是hello spring
3. curl http://localhost:8080/actuator/health，检查当前应用的状态是OK的还是不OK的
4. 再pom文件中，spring-boot-maven-plugin主要功能是为了我们再执行打包的过程当中，替我们生成一个可执行的jar包，命令为`mvn clean package -Dmaven.test.skip`
5. 打包后再target文件夹内，生成的jar包会有两个，一个是origin的jar包，一个是带有依赖的jar包，使用的时候使用那个带有依赖的jar包，可以直接通过命令`java -jar hello-spring-0.0.1-SNAPSHOT.jar`运行程序
6. 如果需要定义字节实现的parent，这里就需要建立一个新的<dependencyManagement>标签，将org.springframework.boot存放到<dependencies>标签下，也可以实现已知的目标<br/>![WeChata28d79c154370d3cfdd3031a73eb28e1.png](https://i.loli.net/2019/05/21/5ce383694aa8b93423.png)
