## Spring基础-IoC.md
1. 概述
2. Bean方式介绍及相关作用域
3. Bean的加载及继承
4. 注解的使用
5. 体验了一下xml的复杂，就会了解使用注解是一件多么爽快的事情
6. 参考视频: [https://www.imooc.com/video/19045](https://www.imooc.com/video/19045)
7. 码云的代码库：[https://gitee.com/LOVE0612/Imooc](https://gitee.com/LOVE0612/Imooc)

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
	6. **在xml中实例化之后，在后面添加scope标签的时候**，可以选择request、session、application三个作用域
		* request：每个request请求都会创建一个单独的实例。每个请求都不一样
		* session：每个session都会创建一个单独的实例。换个浏览器就会改变
		* application：每个servletContext都会创建一个单独的实例，更换浏览器不会改变，如果一个bean的作用域被表示为application，每一个servletContext都会为JavaBean进行一次实例化
		* websocket：每个websocket连接都会创建一个单独的实例。和request的作用域非常相像
4. 自定义作用域
	1. SimpleThreadScope作用域
		* 在每一个线程里面，spring会给我们一个新的实例，在同一个上下文的线程多次申请这个实例的时候，spring给我们的始终是一个实例。多线程的时候，每个线程spring会给不同的线程一个新的实例。
		* 这个和`java.lang.ThreadLocal`细节很像
	2. 想要自定义作用域的话，需要继承`org.springframework.beans.factory.config.Scope`
		* 定义了双例方法，也就是在一个类中添加两个map，同时对这两个map进行操作。

### 三、Bean的加载及继承
#### 3.1 Bean的懒加载
1. 什么是懒加载？
	* 一般情况下在加载spring.xml文件的时候，对在xml文件中实例化的bean会在加载spring.xml的时候直接实例化bean，bean都是默认的Sington。
	* Spring容器会在创建容器的时提前初始化Singleton作用域的bean。但是Bean，但是Bean被标注了Lazy-init="true"，则该Bean只有在被需要的时候才被初始化。
	* 只对singleton作用域的bean有效
2. 那么为什么其他作用域的bean不需要懒加载呢？
	* 应该是单例模式中的懒汉模式
3. 某个Bean设定懒加载
	* `<bean id="bean2 class="com...Bean" lazy-init="true"/>`
4. 为所有的Bean设定懒加载
	* 在spring.xml中的头配置标签<beans>中配置`default-lazy-init="true"`

#### 3.2 Bean的初始化和销毁
1. Bean初始化
	* 如果需要在Bean实例化之后执行一些逻辑，有两种方法：
		* 例子，JavaBean需要连接连接数据库之类的，传输数据，结束连接
	* 使用init-method，定义实例化之后执行的逻辑，就是定义一个方法，然后将这个方法名配置在<bean>标签内
	* 让Bean实现InitializingBean接口，然后在实现的方法中写逻辑就可以了
2. 如果需要在Bean销毁之前执行一些逻辑，有两种方法
	* 使用destory-method，和init-method类似
	* 让Bean实现DisposableBean接口，同上
3. 还可以在<beans>上面的头的标签里添加`default-init-method`，`default-destory-method`两个配置，类中没有这两个方法也没事，有的话，会默认在初始化和销毁的时候执行。

#### 3.3 属性继承
1. 继承是为了简化相同类中的属性和方法的代码，在xml注入的时候同样如此
	* ![964F0E63-1BC0-47AF-93B8-155E2AC1866C.png](https://i.loli.net/2019/05/15/5cdbf54777f0b79545.png)
	* 创建一个新的parentClass文件即可，在xml中实例化即可
	* ![67DA343A-0715-4984-BA6D-A7FAC5DBEB7D.png](https://i.loli.net/2019/05/15/5cdbf74ab114b89913.png)
	* 删除白线的parent即可
	* ![470CF5D9-4DFD-4980-9FF1-7A0B7E482DC1.png](https://i.loli.net/2019/05/15/5cdbf74ab35a921165.png)


### 四、注解的使用
#### 4.1 注解的基本使用
1. 介绍如何通过注解来取代繁杂的xml配置
2. xml VS annotation
	* 使用原来的xml格式<br/>![WeChatbe977d4bb09d6ed76a9f0b80145b3bf8.png](https://i.loli.net/2019/05/15/5cdc0ada8559469368.png)
	* 使用注解的方式，<br/>![WeChatbc2405b98dd280222ab757ffa99a37e8.png](https://i.loli.net/2019/05/15/5cdc0c1a7227e78649.png)

3. 使用注解的原因主要是为了减少我们xml格式下的大量的代码，在这种写法里面还会出现大量的注释代码<br/> ![WeChat8e71b24c82b74d49a46d1443e40aec6c.png](https://i.loli.net/2019/05/15/5cdc0cbcd512a81734.png)
	* 解决的办法是开启一个包扫描，<br/>![WeChat983c9f06baf8f5e93e42752e58239b3e.png](https://i.loli.net/2019/05/15/5cdc0d6daa83141669.png)
4. 注解的解释：	
	* ![WeChat219e7e752f0bf94050ac41217c7617a9.png](https://i.loli.net/2019/05/15/5cdc0dd6809d729518.png)
	* 为Bean取别名<br/>![WeChatfcbbcfaefcf29b41a7c2b814549ae53d.png](https://i.loli.net/2019/05/15/5cdc0e7e2c2e337205.png)
	* @Component方式只能指定一个id，不能同时为娶两个别名

	
#### 4.2 通过注解注入Bean
1. 通过方法注入Bean
	* 通过构造方法注入Bean
	* 通过set方法注入Bean
	* ![WeChat8023344c6d4315dddffda4ad1c9d79b4.png](https://i.loli.net/2019/05/15/5cdc12ed3fd6446007.png)
2. 通过属性注入Bean
	* 通过属性直接注入Bean<br/>![WeChat3bae581a403bceba718fec4889766279.png](https://i.loli.net/2019/05/15/5cdc13b427b0580988.png)
	* 实例化和注入时指定Bean的id<br/>![WeChat362df8532821c33b0bb2f78fc0158a6b.png](https://i.loli.net/2019/05/15/5cdc134e48d0542004.png)
3. 集合类Bean的型注入
	* 直接注入集合实例
		* 直接注入List实例<br/>![WeChate1f8a01b8cf1ffd5b22e6317d8030aa0.png](https://i.loli.net/2019/05/15/5cdc14266853a63111.png)
		* 直接注入Set实例<br/>![WeChat664a420fa6c088af5404989d89741295.png](https://i.loli.net/2019/05/15/5cdc14d10153081740.png)
	* 将多个反省的实例输入到集合
		* 将多个范型的实例注入到List<br/>![WeChat191fb59c874977ad27c5c7fe521bbe29.png](https://i.loli.net/2019/05/15/5cdc14689061126590.png)
		* 控制范型实例在List中的顺序，根据Order的参数控制顺序
		* 将多个范型的实例注入到Map<br/>![WeChat6866fbe4ad49a9d77be61b03455d351e.png](https://i.loli.net/2019/05/15/5cdc15505223a13203.png)
4. String、Integer等类型直接赋值
	* ![WeChat7a6fc680997277003fe5f68213f47513.png](https://i.loli.net/2019/05/15/5cdc1595868a439002.png)
5. StringIoC容器内置接口实例注入
	* ![WeChat8864f0ecbaae6ab776d7912cc316feba.png](https://i.loli.net/2019/05/15/5cdc15df0522b65918.png)

#### 4.3 通过注解设定Scope
1. Singleton作用域
	* ![WeChat42da6bd576d47f6f48d4048319c15d7e.png](https://i.loli.net/2019/05/16/5cdcca04cbb5d85648.png)
2. prototype作用域
3. Web环境作用域
	* request作用域
	* session作用域
	* application作用域
	* websocket作用域
4. 自定义作用域
	* 实现自定义作用域<br/>![WeChat3a8314bda6c622c4f8d9772f7db6fe74.png](https://i.loli.net/2019/05/16/5cdcca072c95b35417.png)
	* 注解形式<br/>![WeChat1341a335e59d9b9f49e4ddbc07c0cfd5.png](https://i.loli.net/2019/05/16/5cdcca064d68861561.png)
5. 方法注入
	* ![WeChatde9bdaaed32bb37cf3096ec0f70d6ebf.png](https://i.loli.net/2019/05/16/5cdcca071898f12470.png)

#### 4.4 注解懒加载
1. xml形式和annotation形式<br/>![WeChat6c05fd296bb17507c7b12bd3f68e11c9.png](https://i.loli.net/2019/05/16/5cdccf71b2ce812710.png)
	
#### 4.5 通过注解编写Bean初始化及销毁
1. 注解方式的初始化和销毁方法<br/>![WeChatb806c381b27b7516d833fc43e7cb89bc.png](https://i.loli.net/2019/05/16/5cdcd17c1729e10169.png)
2. 还有一种很独特的方式<br/>![WeChatadb2c54781c45e2ee33b2add56c4a78e.png](https://i.loli.net/2019/05/16/5cdcd1d0e844268918.png)


