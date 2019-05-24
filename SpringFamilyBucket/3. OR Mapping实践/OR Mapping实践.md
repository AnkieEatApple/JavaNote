## 1. 
#### 一、认识Spring Data JPA
1. 对象与关系的范式不匹配<br/>![WeChat2538257638706ac7f94bbcf06392df4d.png](https://i.loli.net/2019/05/22/5ce4ad7681bf724466.png)
2. Hibernate，在这样的背景之下出现了这类的ORM
	* 一款开源的对象关系映射(Object/Relational Mapping)框架
	* 将开发者从95%的常见数据持久化工作中解放出来
	* 屏蔽了底层数据库的各种细节
3. Hibernate发展历程
	* 2001年，Gavin King发布第一个版本
	* 2003年，Hibernate开发团队加入JBoss
	* 2006年，Hibernate 3.2称为JPA实现
4. Java Persistence API  (Java 持久化 API)
	* JPA为对对象关系映射提供了一种基于POJO的持久化模型
		* 简化数据持久化代码的开发工作
		* 为Java社区屏蔽不同持久化API的差异
	* 2006年，JPA1.0作为JSR220的一部分正式发布
5. Spring Data
	* 在保留底层储存特性的同时，提供对一直的，基于Spring的编程模型
	* 主要模块
		* Spring Data Commons
		* Spring Data JDBC
		* **Spring Data JPA**
		* Spring Data Redis...

#### 二、定义JPA的实体对象
1. 常用JPA注解，基于Hibernate
	* 实体
		* @Entity、@MappedSuperclass
		* @Table(name)
	* 主键
		* @Id
			* @GeneratedValue(strategy, generator)
			* @SequenceGenerator(name, sqquenceName)  
	* 映射
		* @Column(name, nullable, length, insertable, updatable)
		* @JoinTable(name)、@JoinColumn(name)
	* 关系
		* @OneToOne、@OneToMany、@ManyToOne、@ManyToMany
		* @OrderBy

2. Project Lombok插件
	* Project Lombok能够自动嵌入IDE和构建工具，提升开发效率
	* 常用功能
		* @Getter/@Setter
		* @ToString
		* @NoArgsConstructor/@RequiredArgsConstructor/@AllArgsConstructor
		* @Data，混合注解
		* @Builder，为对象生成builder方法
		* @Slf4j/@CommonsLog/@Log4j2  


#### 三、线上咖啡馆实战项目-SpringBucks
1. 项目目标，通过一个完整的例子演示Spring全家桶各主要成员的用法 <br/>![WeChate5d65a4c608203f3310326033dca2b47.png](https://i.loli.net/2019/05/22/5ce4b32228fd977428.png)
2. 项目中的对象实体
	* 实体
		* 咖啡、订单、顾客、服务员、咖啡师
		* ![WeChat19d9c4f8ec0c96c4146a1c455355ae34.png](https://i.loli.net/2019/05/22/5ce4b3b10b7da50705.png)
		* 订单的相关流程<br/>![WeChatb8182d5794fbeac6f42b7d3ac1d99a69.png](https://i.loli.net/2019/05/22/5ce4b622ad35a41028.png)

3. 定义实体
	* **关于金额，不能使用浮点数进行金额的相关运算**，会产生错误，需要对数字转化为字符串然后进行运算，这里使用的是**joda-money**
	* 引用的依赖<br/>![WeChat72e966a5939f025cc18c5764f11f62aa.png](https://i.loli.net/2019/05/22/5ce4b71360e8364030.png)  
	* 实体的定义<br/>![WeChat233c4af6b888e7ec22608a234aec7f3e.png](https://i.loli.net/2019/05/22/5ce4b75d0658e99591.png )
	* 例子定义在Chapter03中的jpa-demo中	

4. 使用枚举来定义订单的状态，例子在jpa-complex-demo中

#### 四、使用SpringDataJPA操作数据库
1. Repository
	* @EnableJpaRepositories	
	* Repository\<T, ID>接口
		* CrudRepository\<T, ID>
		* PagingAndSortingRepository\<T, ID>
		* JpaRepository<T, ID>

2. 定义查询，根据方法名定义查询
	* find...By... / read...By... / query...By... / get...By...
	* count...By...
	* ...OrderBy...[Asc / Desc]
	* And / Or / IgnoreCase
	* Top / First / Distinct

3. 分页查询
	* PagingAndSortingRepository\<T, ID>
	* Pageable / Sort
	* Slice<T> / Page\<T> 

4. 保存实体，jpa-demo
5. 查询实体，jpa-complex-demo

#### 五、Repository是怎么从接口变成Bean的
1. Repository Bean是如何创建的
	* JpaRepositoriesRegistrar
		* 激活了@EnableJpaRepositories
		* 返回了JpaRepositoryConfigExtension
	* RepositoryBeanDefinitionRegistrarSupport.registerBeanDefinitions
		* 注册Repository Bean(类型是JpaRepositoryFactoryBean)
	* RepositoryconfigurationExtensionSupport.getRepositoryConfigurations
		* 取得Repository配置
	* JpaRepositoryFacroty.getTragetRepository
		* 创建了Repository     
	* 代码主要在spring-data-jpa-2.1.4.RELEASE.jar包中的repository中，例子为jpa-complex-jpa   

2. 接口中的方法是如何被解释的
	* RepositoryFactorySupport.getRepository添加了Advice
		* DefaultMethodInvokingMethodInterceptor
		* QueryExecutorMethodInterceptor
	* AbstractJpaQuery.excute执行具体的查询
	* 语法解析在Part中

#### 六、通过MyBatis操作数据库
1. 认识MyBatis
	* MyBatis [https://github.com/mybatis/mybatis-3](https://github.com/mybatis/mybatis-3)   
		* 一款优秀的持久层框架
		* 需要自己定义支持定制化SQL、储存过程和高级映射
	* Spring中使用MyBatis
		* MyBatis Spring Adapter [https://github.com/mybatis/spring](https://github.com/mybatis/spring)
		* MyBatis Spring-Boot-Starter [https://github.com/mybatis/spring-boot-starter](https://github.com/mybatis/spring-boot-starter)

2. 如何选择Hibernate这种JPA还是选择myBatis这种需要自己实现sql的
	* 看实现的sql的是否简单，如果sql操作简单，就可以上hibernate，如果操作sql复杂，需要定制，就直接上mybatis
	* 可以直接上mybatis，大厂的DBA可以对sql有更好的把控程度

3. 使用	 Spring-Boot-Starter后，需要的简单配置
	* `mybatis.mapper-locations = classpath*:mapper/**/*.xml`
	* mybatis.type-aliases-package = 类型别名的包名
	* mybatis.type-handlers-package = TypeHander扫描包名
	* mybatis.configuration.map-underscore-to-camel-case = true

4. Mapper的定义与扫描，例子在mybatis-demo中
	* @MapperScan配置扫描的位置
	* @Mapper 定义接口
	* 映射的定义--XML与注解
 	
#### 七、让MyBatis更好用的那些工具-**MyBatis Generator**
1. 认识MyBatis Generator [http://www.mybatis.org/generator/](http://www.mybatis.org/generator/)
	* MyBatis 代码生成器
	* 根据数据库生成相关的代码
		* POJO
		* Mapper接口
		* SQL Map XML

2. 运行MyBatis Generator
	* 命令行
		* java -jar mybatis-generator-core-x.x.x.jar -configfile generatorConfig.xml
	* Maven Plugin(mybatis-generator-maven-plugin)
		* mvn mybatis-generator:generator
		* ${basedir}/src/main/resources/generatorConfig.xml   
	* Eclipse Plugin
	* Java 程序、Ant Task使用的比较少

3. 配置MyBatis Generator
	* generatorConfiguration  
	* context
		* jdbcConnection
		* javaModelGenerator
		* sqlMapGenerator
		* javaClientGenerator(ANNOTATEDMAPPER / XMLMAOOER / MIXEDMAPPER)
		* table

4. 生成时可以使用的插件
	* 内置插件都在org.mybatis.generator.plugins包中
		* FluentBuilderMethodsPlugin
		* ToStringPlugin
		* SerializablePlugin
		* RowBoundsPlugin
		* ...

5. 使用生成的对象，例子：mybatis-generator-demo
	* 简单操作，直接使用生成的xxxMapper的方法
	* 复杂操作，使用生成的xxxExample对象

#### 八、让MyBatis更好用的那些工具-**MyBatis PageHelper**
1. 认识 MyBatis PageHelper，例子：mybatis-pagehelper-demo
	* MyBatis PageHelper[https://pagehelper.github.io](https://pagehelper.github.io)
	* 支持多种数据库
	* 支持多种分页方式
	* SpringBoot支持[https://github.com/pagehelper/pagehelper-spring-boot](https://github.com/pagehelper/pagehelper-spring-boot)
		* pagehelper-spring-boot-starter 