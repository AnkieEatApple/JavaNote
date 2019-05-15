## 1
1. 概述
2. Bean方式介绍及相关作用域
3. Bean的加载及继承
4. 注解的使用
5. 体验了一下xml的复杂，就会了解使用注解是一件多么爽快的事情
6. 参考视频: [https://www.imooc.com/video/19045](https://www.imooc.com/video/19045)

### 一、概述
1. 2004年release出第一个版本
2. Java Web发展史
	1. JavaBean + Servlet + JSP逐步发展
	2. 面对EJB重量级框架带来的种种麻烦
	3. SpringMVC/Structs + Spring + Hibernate/MyBatis
	4. 享受SpringBoot “约定大于配置” 的种种乐趣
	5. 以Dubbo为代表的SOA微服务架构体系
	6. SpringCloud微服务架构技术生态圈
3. 内容
	1. 主要记录IoC并编写一个简单的IoC容器
	2. 主要记录如何通过xml的方式完成SpringIoC对Bean的管理
	3. 主要记录SpringIoC相关注解的使用
4. 目的
	1. 理解IoC的概念以及IoC存在的意义
	2. 学会使用SpringIoC完成对Bean生命周期的管理
	
#### 1.1 IoC概念介绍
1. IoC: Inversion of Control, 控制反转
2. 控制什么？
	* 控制的就是Java bean的创建及销毁(生命周期)
3. 反转什么？
	* 对象的控制权被反转了，将对象的控制权交给IoC容器
	* 当没有IoC容器的时候，被依赖的对象创建的时间是依赖它的对象，在使用它的时候，创建被依赖的它的对象，也就是被依赖的对象的控制权再依赖对象手里，有了IoC要进行反转，反转就是将这个控制权交给IoC
	* ![3413FC49-18B4-44E1-A4AA-0600000A0C8B.png](https://i.loli.net/2019/05/14/5cda8545abefc88303.png)
	* 高耦合性
		* 张三所有的行为都需要自己主动创建并销毁一辆车
		* 更换车辆的代价是巨大的，需要将代码中的所有Audi，替换成Buick
4. 思考问题
	1. 张三需要的是一辆奥迪？一辆别克？或者就是一辆车？张三只是需要一辆车
	2. 张三会制造车辆么？车辆不应该由张三来创建，代码改进为Audi、Buick继承了Car接口，实现了Car接口中的方法，car作为Zhangsan的内部属性由构造方法传入。
	3. 那么该由谁来创建这个辆车呢？IoC容器

#### 1.2 实现一个自己的IOC
1. 实现的类图<br/>![90D01821-184D-4055-8366-35B1C809CE10.png](https://i.loli.net/2019/05/14/5cda8815b5d5e52878.png)
2. 约定
	* 所有Bean的生命周期交由IoC容器进行管理
	* 所有被依赖的Bean通过构造方法执行注入，也可以set方法
	* 被依赖的Bean要优先创建，举例，zhang3依赖于一辆奥迪车辆，如果想创建zhang3这么一个Bean，就要求奥迪这辆车已经创建了，而且已经交由Ioc容器管理了
3. 代码回顾
	* 所有的依赖关系被集中统一的管理起来了，清晰明了
	* 每个类只需要关注于自己的业务逻辑
	* 修改依赖关系将是一件很容易的事情

#### 1.3 课程内容
1. 学习使用Spring管理我们的第一个Bean
	1. 创建一个spring.xml的配置文件
	2. 再xml文件中定义一个bean
	3. 通过ApplicationContext去读取xml配置文件，获取Spring上下文
	4. 获取Bean

### 二、Bean方式介绍及相关作用域
#### 2.1 实例话Bean方式介绍(详见视频)
1. 学习使用Spring实例话Bean
	* 通过构造方法实例话Bean<br/>`<bean id="bean" class...Bean/>`
	* 通过静态方法实例话Bean<br/>
	
	```
	<bean id="beanFromFactory" class...BeanFactory
				factory-method="createBean"/>
	```

	* 通过实例方法实例话Bean

	```
	<bean id="beanFactory" class="com...BeanFactory"/>
	<bean id="beanFromFactory" class="com...BeanFactory"
				factory-method="createBean"/>
	```
	
	* Bean的别名

	```
	<bean id="bean1" name="bean2, bean3" class="com...Bean"/>
	// 这个只能配置一个别名
	<alias name="bean1" alias="bean4"/>
	```
#### 2.2 注入Bean方式介绍(详见视频)
1. 通过构造方法注入Bean
2. 通过set方法注入Bean
3. 集合类Bean的型注入
	* List
	* Set
	* Map
	* Properties，这个数据结构类继承自HashTable，但是一个持久化的属性集合，键和对应值都是一个字符串，参考: [https://blog.csdn.net/qq_42552654/article/details/80819918](https://blog.csdn.net/qq_42552654/article/details/80819918)
4. null值注入
5. 注入时创建内部Bean

#### 2.3 Bean作用域(详见视频)
1. Singleton作用域
	* 一种类似于单例模式的作用域<br/>![F383C89A-84E7-4D0F-9968-989392DB3487.png](https://i.loli.net/2019/05/14/5cda9d22ab7b318251.png)
	* 这个是在当前的上下文环境下会保持单例模式，若新建一个上下文环境的话，这个单例的模式将会失效。
2. prototype作用域
	* 与之相反的类似于多例模式的作用域<br/>![D1E48296-2AC4-470A-86D7-8714E15A1AC3.png](https://i.loli.net/2019/05/14/5cda9d5a1a90994003.png)
	* 两种情况相互交叉的时候<br/>![BA2D5FBC-0576-4288-8D7C-0A38E0FDC28D.png](https://i.loli.net/2019/05/14/5cdaa00270ce339854.png)
	* Bean1是singleton，Bean2是prototype，Bean1依赖Bean2。我们希望每次调用Bean1的某个方法的时候，该方法拿到的Bean2都是一个新的实例，也就是 **方法注入**
3. Web环境作用域
	1. request作用域
	2. session作用域
	3. application作用域
	4. websocket作用域
	5. SpringWeb上下文环境<br/>![WeChat64d8724344c6f4e019252ced7021ce67.png](https://i.loli.net/2019/05/14/5cdab7fa0bc0967566.png)
4. 自定义作用域
	1. SimpleThreadScope作用域



### 三、Bean的加载及继承

### 四、注解的使用



