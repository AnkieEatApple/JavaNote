## 1. 
#### 一、如何配置数据源
1. 还是打开[https://start.spring.io](https://start.spring.io)网站，再dependencis中有一个see all的选项，俩面有很多关于SQL的选项，可以选择对应的组件进行添加
2. Spring Boot配置演示的配置
	* 引入对应的数据库驱动--H2
	* 引入JDBC依赖--spring-boot-starter-jdbc
	* 获取DataSource Bean，打印信息
	* 也可通过/acturator/beans查看Bean
	* 引用依赖主要有H2、JDBC、Lombok、Web、Actuator
3. 这里并没有配置H2的数据库，springboot默认配置了一个测试名和一个测试密码
4. 运行之后可以通过[http://localhost:8080/actuator/beans](http://localhost:8080/actuator/beans)这个地址，访问beans上下文，里面有spring的数据源
5. 直接配置所需的Bean
	* 数据源相关
		* DataSource(根据选择的连接池实现决定)
	* 事物相关(可选)
		* PlatformTarnsactionManager(DataSourceTransactionManager) 
		* TransactionTemplate
	* 操作相关(可选)
		* JdbcTemplate

6. Spring Boot 做了哪些配置
	* DataSourceAutoConfiguration
		* 配置DataSource
	* DataSourceTransactionManagerAutoConfiguration
		* 配置DataSourceTransactionManager
	* JdbcTemplateAutoConfiguration
		* 配置JdbcTemplate
	* 符合条件时，才进行配置
7. 数据源相关配置属性
	* 通用
		* spring.datasource.url=jdbc:mysql://localhost/test
		* spring.datasource.username=dbuser
		* spring.datasource.password=dbpass
		* spring.datasource.dirver-class-name=mysql.jdbc.Dirver(可选)，springboot会根据url选择一个合适的驱动
	* 初始化内嵌数据库
		* spring.datasource.initalization-mode=embedded|always|never
		* spring.datasource.schema与spring.datasource.data确定初始化SQL文件
		* spring.datasource.platform=hsqldb|h2|oracle|mysql|postgresql(与前者对应)

#### 二、配置多数据源的注意事项
1. 不同数据源的配置要分开
2. 关注每次使用的数据源
	* 有多个DataSource时系统如何判断
	* 对应的设施(事务、ORM等)如何选择DataSource
3. Spring Boot中多数据源配置
	* 手工配置两组DataSource及相关内容
	* 与Spring Boot协同工作(二选一)
		* 配置@Primary类型的Bean
		* 排除Spring Boot的自动配置
			* DataSourceAutoConfiguration
			* DataSourceTransactionManagerAutoConfiguration
			* JdbcTemplateAutoConfiguration

#### 三、那些好用的连接池-HikariCP
1. HikariCP，日语是光的意思，首页为: [http://brettwooldridge.github.io/HikariCP/](http://brettwooldridge.github.io/HikariCP/)
2. HikariCP为什么快
	1. 字节码级别优化(很多方法通过JavaAssist生成)
	2. 大量小改进
		* 用FastStatementList代替ArrayList
		* 无锁集合ConcurrentBag
		* 代理类的优化(比如，用invokestatic代理了invokevirtual)

3. 再Spring Boot中的配置
	* Spring Boot 2.x
		* 默认使用HikariCP
		* 配置spring.datasource.hikari.*配置
	* Spring Boot 1.x
		* 默认使用Tomcat连接池，需要移除tomcat-jdbc依赖
		* spring.datasource.type=com.zaxxer.hikari.HikariDataSource
4. 常用HikariCP配置参数
	* 常用配置
		* spring.datasource.hikari.maximunPoolSize=10，数据库最大的连接池有多少
		* spring.datasource.hikari.minimumIdle=10，最小连接个数
		* spring.datasource.hikari.idleTimeout=600000，idel的超时
		* spring.datasource.hikari.connectionTimeout=30000，连接的超时
		* spring.datasource.hikari.maxLifetime=1800000，每个连接最大可以存活多久
	* 其他配置相见HikariCP官网   
 
#### 四、那些好用的连接池-Alibaba Druid
1. Druid连接池是阿里巴巴开源的数据库连接池项目。Druid连接池尾监控而生，内置强大的监控功能，监控特性不影响性能。功能强大，能防SQL注入，内置logging能诊断Hack应用行为。
2. Druid特点
	* 经过阿里巴巴各大系统的考验，值得信赖
	* 实用功能
		* 详细的监控(真的是全面)
		* ExceptionSorter，针对主流数据库的返回码都有支持
		* SQL防注入
		* 内置加密配置
		* 众多扩展点，方便进行定制
3. 官网github: [https://github.com/alibaba/druid](https://github.com/alibaba/druid)	
4. 怎么实用呢？数据源配置
	* 直接配置DruidDataSource
	* 通过druid-spring-boot-start
		* spring.datasource.druid.*
		* ![WeChat32d81fe8783ad6c151670c2960f23040.png](https://i.loli.net/2019/05/21/5ce3a8dc0105238841.png)
	* Filter配置
	  * spring.datasource.durid.filters=stat,config,wall,log4j(全部使用默认值)
	* 密码加密
	 	* spring.datasource.password=<>
	 	* spring.datasource.durid.filter.config.enable=true
	 	* spring.datasource.durid.connection-properties=config.decrypt=true;config.decrtpt.key=<public-key> 
	* SQL防注入
		* spring.datasource.durid.filter.wall.enable=true
		* spring.datasource.durid.filter.wall.db-type=h2
		* spring.datasource.durid.filter.wall.config.delete-allow=false
		* spring.datasource.durid.filter.wall.config.drop-table-allow=flase

5. Druid Filter
	* 用于定制连接池操作的各种环节
	* 可以继承FilterEventAdapter以方便地实现Filter
	* 修改META-INF/durid-filter.properties增加Filter配置

6. 连接池选择时的考量点
	1. **可靠性**：一定希望数据库连接池在数据库发生问题或网络发生抖动的时候，能够自动的发现这些问题，并快速的恢复
	2. **性能**：数据库的性能开销尽可能小
	3. **功能**：除了连接池本身的功能之外，还可以提供防注入啊，监控之类的，对性能可稳定性没有太大的影响
	4. **可运维性**：比方说我们的数据库的一个密码对任何系统来说都是非常敏感的，如果需要把明文配置在我的配置文件里面或者写死在我的代码里面，当然是不可接受的，Druid可以内嵌密码加密的功能，HikariCP也可以使用其他的类库实现，如果数据库密码使用明文，一定要三思
	5. **可扩展性**：整个业务操作有很多的SQL语句，想知道每条SQL语句的执行效果，就可以在数据库连接之前加入一条注释值了的，可以对sql做拦截或预处理之类的
	6. **其他**：注意连接池的论坛是否还活跃

#### 五、Spring中的JDBC操作
1. **spring-jdbc**
	* core，JdbcTemplate等相关核心接口和类
	* database，数据源相关辅助类
	* object，将基本的JDBC操作封装成对象
	* support，错误码等其他辅助工具
2. 常用的Bean注解，通过注解定义Bean
	* @Component，就是一个通用的注解，来定义一个通用的bean
	* @Repository，做DAO相当于一个数据操作的一个仓库，后面关于数据库的操作建议都是用@Repository定义的Bean中
	* @Service，业务的服务
	* @Controller，SpringMVC
		* @RestController，为了方便大家开发RESTful Service

3. 简单的JDBC操作，JdbcTemplate
	* query
	* queryForObject
	* queryForList
	* update，可以实现插入、修改、删除
	* execute
4. SQL批处理，例子是simple-jdbc-demo
	* JdbcTemplate
		* batchUpdate
			* BatchPreparedStatementSetter 
	* NamedPArameterJdbcTemplate
		* batchUpdate
			* SqlParameterSourceUtils.createBatch  

#### 六、Spring的事务抽象
1. 一致的事务模型
	* JDBC/Hibernate/myBatis
	* DataSource/JTA
			
2. 事务抽象的核心接口
	* PlatformTransactionManager
		* DataSourceTransactionmanager
		* hibernateTransactionManager
		* JtaTransactionManager 
	* TransactionDefinition
		* Propagation
		* Isolation
		* Timeout
		* Read-only status

3. 事务的传播特性
	* ![WeChat2d7e9180aee2df62d95151425827ef32.png](https://i.loli.net/2019/05/21/5ce3b785514ba96086.png)

4. 事务的隔离特性
	*  ![WeChatfdef0acad919466deba2d6b84c5ed380.png](https://i.loli.net/2019/05/21/5ce3b80e0c18356278.png)

5. 编程式事务，例子为programmatic-transaction-demo
	* Transaction	Template
		* TransactionCallback，又返回值
		* TarnsactionCallbackWithoutResult，没返回值  
	* PlatformTransactionManager
		* 可以传入TransactionDefinition进行定义 

6. 声明式事务
	* ![WeChatc038fec7f2cb9bd26570d9317c0200dd.png](https://i.loli.net/2019/05/21/5ce3b97d2cd7f15178.png)

7. 基于注解的配置方式
	* 开启事务的注解的方式
		* @EnableTransactionManagement
		* <tx:annotation-driven/>
	* 一些配置
		* proxyTargetClass，当前做的AOP是基于接口的还是基于类的，一般实现都是定义接口，然后定义实现类，也可以直接对类进行增强
		* mode 
		* order，使用的AOP的一个拦截顺序
	* **@Transactional**
		* transactionManager
		* propagation
		* isolation
		* timeout
		* readOnly
		* 怎么判断回滚

#### 七、Spring的JDBC异常抽象    	
1. Spring会将数据操作的异常转换为DataAccessException，无论使用何种数据方式，都能使用一样的异常
	* ![WeChat75c6d691194d2eaf2936a2328f43b233.png](https://i.loli.net/2019/05/21/5ce3bc64929bb50486.png)

2. Spring是怎么认识那些错误码的
	* 通过SQLErrorCodeSQLExceptionTranslator解析错误码
	* Error定义
		* org/springframework/jdbc/support/sql-error-codes.xml
		* Classpath下的sql-error-codes.xml

3. 定制错误码解析逻辑，例子为errorcode-demo
	* 针对不同的业务有一些别的异常希望捕获到，或者加了一个数据层的代理，代理可能抛出一些错误码，比如在mysql前面加了一层proxy，这些错误码的相应和其他的数据库不一样的话，就需要定制
	* ![WeChat97bf925e2274946d11ab07e458cd1c36.png](https://i.loli.net/2019/05/21/5ce3bdae828c942746.png) 

#### 八、课程答疑
1. 开发环境相关的说明
	* Java8/Java11，因为会用到Lambda
	* IDEA 社区版，安装Lombok插件、MavenHelper<br/>![WeChat39c30fc58d05984da80bb1ad75d4d5ca.png](https://i.loli.net/2019/05/21/5ce3c1f233c7033281.png) 
	* Apache Maven
	* MacOS
	* Docker
2. 一些Spring常用注解简介
	* Java Config相关注解
		* @Configuration，表明当前的这个类是一个配置类
		* @ImportResource，配置注入一些配置以外的，xml配置文件信息注入进来
		* @ComponentScan，告诉我整个spring容器，里面去扫描哪一些package下的Bean
		* @Bean，被标注后，他的返回就可以作为springbean的配置，存在于整个spring context当中
		* @ConfigurationProperties，把注解绑定过来，方便使用配置
	* 定义相关注解
		* @Component/@Repository/@Service，所有的JavaBean都可以用@Component定义，@Repository专门用来标记这个组件是一个数据库访问层的，@Service就是服务层的一个Bean
		* @Controller/@RestController，Web层的一个Bean，早先的版本上只有Controller，@RestController是Controller+ResponseBody
		* @RequestMapping，帮助定义方法，或类下的方法都是在那些url下面的，做一个映射
	* 注入相关注解
		* @Autowired/@Qualifier/@Resource，@Autowired会把上下文当中的按照类型，来做一个查找，然后注入进来，@Qualifier指定Bean的名字，@Resource根据名字进行注入
		* @Value，在Bean中注入一些常量  
3. 关于Actuator Endpoints访问不到的说明
	* Actuator 提供的一些好用的Endpoint，对系统在生产时做的一些监控<br/>![WeChata444bb87ae37e39166c01bb6c476205e.png](https://i.loli.net/2019/05/21/5ce3c5b45c1bd97501.png)
	* 如何解禁Endpoint
		* 默认
			* /actuator/health 和 /actuator/info可Web访问
		* 解禁所有的Endpoint
			* application.properties/application.yml
			* management.endpoints.web.exposure.include=*
		* **生产环境需要谨慎** 
4. 对数据源、分库分表、读写分离的关系
	* 几种常见的情况
		* 系统需要访问几个完全不同的数据库
		* 系统需要访问同一个库的主库与备库
		* 系统需要访问一组做了分库分表的数据库 
		* ![WeChat97efbd0968b36d40fda03037710d0d3e.png](https://i.loli.net/2019/05/21/5ce3c869efca667977.png)
	* 使用数据库中间件的情况
		* 使用带了事务的读写使用主库，只读的事务使用从库
		* 同时分库分表的主备库，做了00～99的一个路由，也做了00主库备库的一个路由去分离了读写的一些请求
		* ![WeChatb2b4037597575c123cd446e070266ee9.png](https://i.loli.net/2019/05/21/5ce3c9714731997647.png) 
5. 与内部方法调用与事务的课后问题
	* 事务的本质，例子declarative-transaction-demo
		* Spring的声明式事务本质上是通过AOP来增强了类的功能
		* Spring的AOP本质上就是为类做了一个代理
			* 看似在调用自己写的类，实际用的是增强后的代理类
		* 问题的解法
			* 访问增强后的代理类的方法，而非直接访问自身的方法   
6. REQUIRES_NEW与NESTED事务传播特性的说明，例子：transaction-propagation-demo
	* REQUIRES_NEW，始终启动一个新事务
		* 两个事务没有关联
	* NESTED，在原事务内启动一个内嵌事务
		* 两个事务有关联
		* 外部事物回滚，内嵌事务也会回滚  
7. Alibaba Druid的一些展开说明 	
	* **慢SQL日志**，例子：druid-demo
		* 系统属性配置
			* druid.stat.logSlowSql=true
			* druid.stat.slowSqlMillis=3000
		* Spring Boot
			* spring.datasource.druid.filter.stat.enabled=true 
			* spring.datasource.druid.filter.stat.log-slow-sql=true
			* spring.datasource.druid.filter.stat.slow-sql-millis=3000 ，是监控事件超过3秒的语句
	* 一些注意事项
		* 没有特殊情况，不要在生产环境打开监控的Servlet，线上还是以日志输出为准
		* 没有连接泄露可能的情况，不要开启removeAbandoned，显示中大多数使用的是ORM的框架，造成连接泄漏的可能不是特别大，这个开启后会对性能有很大的影响
		* testXxx的使用需要注意，会对开销使用很大
		* 务必配置合理的超时时间，建立连接的地方会有一个超时的控制
	* druid的很多拓展都是通过责任链的模式实现的。 