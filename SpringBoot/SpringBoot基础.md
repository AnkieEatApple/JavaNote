## SpringBoot基础
1. 特点
	1. 化繁为简，简化配置
	2. 备受关注，是下一代框架
	3. 微服务的入门级微框架 
2. 目录 
	1. 自定义属性配置
	2. Controller
	3. Spring-Data-Jpa
	4. 事务管理
3. 环境
	1. Java1.8
	2. maven，可以修改为阿里云版本

#### 一、创建项目
1. 创建方式有两种，得是IDEA旗舰版才可以通过这个IDEA创建，否则可以通过网站创建
	1. 一种是在网页上创建项目添加项目的依赖
	2. 一种是在IDEA中创建项目，创建依赖，
		1. 选择Spring Initializr，选择default
		2. 添加包名、项目名、版本等信息
		3. 本例子选择Web系列下，Web选项，在右上角可以选择Spring Boot版本，也可以在上面选择依赖
		4. 选择指定的文件夹，创建项目即可
	3. 项目中的HELP.md、mvvw、mvvw.cmd暂时不需要可以删掉

2. 启动项目
	1. 可以通过IDE直接启动
	2. 可以在命令行中，进入制定目录，通过`mvn spring-boot:run`启动
	3. 对项目进行打包，`mvn clean package`，会在当前目录下的`target/`文件夹下产生一个`.jar`文件，然后调用`java -jar target/*.jar`命令，指定jar运行即可

3. 配置文件
	1. server.host=8081，添加端口
	2. server.servlet.context-path=/luckymoney，添加上下文路径，添加之后就称为访问http://localhost:8081/luckmoney/hello了

4. 配置文件还可以将文件改写为application.yml，然后里面的格式会变为树形的操作，推荐使用这种，类似于Json的格式
	* **注意**：这里的配置选项的值和前面的键是要有一个空格的，保证键的颜色是深蓝色的
	* 在外部引用配置文件中的文件时，需要使用`${键值名}`，同时引用@Value注解，在内部引用对应的键值名的时候，可以直接引用`${键值名}`，**外部引用例子注意引用变量的参数 **
	
	```
	@Value("${minMoney}")
   private BigDecimal minMoney;

    @Value("${description}")
    private String description;
	```
	
5. **对象配置**，对配置文件进行整体的进行注入，这时候需要对配置文件中定义一个开头，同样在项目中创建一个类，实现set和get方法，同时在pom文件中添加依赖
	
	```
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-configuration-processor</artifactId>
	    <optional>true</optional>
	</dependency>
	
	
	limit:
	  minMoney: 2
	  maxMoney: 1000
	  description: 最多只能是${limit.maxMoney}元
	```
	
	```
	@Component	// 一定要添加
	@ConfigurationProperties(prefix = "limit")
	public class LimitConfig {
	
	    BigDecimal minMoney;
	
	    BigDecimal maxMoney;
	
	    String description;
	
	    public BigDecimal getMinMoney() {
	        return minMoney;
	    }
	
	    public void setMinMoney(BigDecimal minMoney) {
	        this.minMoney = minMoney;
	    }
	
	    public BigDecimal getMaxMoney() {
	        return maxMoney;
	    }
	
	    public void setMaxMoney(BigDecimal maxMoney) {
	        this.maxMoney = maxMoney;
	    }
	
	    public String getDescription() {
	        return description;
	    }
	
	    public void setDescription(String description) {
	        this.description = description;
	    }
	}
	```
	
6. 这样就可以在controller里面调用了，调用方法为

	```
	@Autowired
	private LimitConfig limitConfig;
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String say() {
	    return "Hello World!"  + " : " + limitConfig.minMoney +  ", max: "+ limitConfig.maxMoney + "，说明：" + limitConfig.description;
	}
	```
	
7. 进行环境隔离，对不同的application.yml不同，可以再建application-dev.yml和application-prod.yml
	* 在application-dev.yml和application-prod.yml复制原来的application.yml文件的内容，然后清空application.yml，让这个application.yml文件作为控制环境的文件
	* 再application.yml文件中配置spring.profiles.active: dev，此时的环境就是dev的环境了，也就是使用的是application-dev.yml的环境，同理prod
	* 

8. 再命令中编译包文件之后，发布的时候可以通过选择指定的环境进行项目上线
	* 先打包：`mvn spring-boot:run`
	* 再选择环境运行：`java -jar -Dspring.profiles.active=prod target/luckymoney*.jar`  

#### 二、Controller的使用
1. **@Controller**， 处理http的请求
2. **@RestController**，Spring4之后新增加的注解，原来返回json需要@ResponseBody配合@Controller使用
3. **@RequestMapping**，配置url映射
	* 可以使用@GetMapping，@PostMapping等，
	* 如果需要访问二级映射，最好是在单个的controller的前面增加@RequestMapping在类的前面增加路径，在每个方法前面只有一层url访问
	* 如果方法前面增加@RequestMapping的话，只有url，没有自己的set/get方式等，最好选择好
4. **@PathValiable**，获取url中的数据，可以简单的实现RESTful模式
	
	```
	// http://localhost:8080/luckymoney/say/1000
	@GetMapping("/say/{id}")
	public String say(@PathVariable("id") Integer id) {
	    return "id : " + id;
	}
	```

5. **@RequestParam**，获取请求参数的值，常用的模式

	```
	// http://localhost:8080/luckymoney/say?id=10000
	@GetMapping("/say")
	public String say(@RequestParam("id") Integer id) {
	    return "id : " + id;
	}
	// 当参数没有添加的时候
	@GetMapping("/say")
    public String say(@RequestParam(value = "id", required = false, defaultValue = "0") Integer id) {
        return "id : " + id;
    }
	```
	
#### 三、数据库操作
1. Spring-Data-Jpa
	* JPA(Java Persistence API)定义了一系列对象持久化的标准，目前实现这一规范的产品有Hibernate、TopLink
2. RESTful API设计<br/>![WeChat313dafdbe9165c26ac5c77d8d298c41a.png](https://i.loli.net/2019/05/24/5ce755edb709226369.png)
3. 加入一些pom引用

	```
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-data-jpa</artifactId>
	</dependency>
	<dependency>
	    <groupId>mysql</groupId>
	    <artifactId>mysql-connector-java</artifactId>
	</dependency>
	
	```
4. 配置application.properties中的数据库的相关参数，这里需要注意的是，mysql的驱动要选择 com.mysql.cj.jdbc.Driver


#### 四、事务
1. 数据库事务，是指作为单个逻辑工作单元执行的一系列操作，要么完全地执行，要么完全地不执行。
2. 事务主要为数据库的事物，mysql的创建表的时候最好选择的是InnoDb这个引擎是支持数据库的事务的，也就是操作某几条sql语句的时候，可以进行回滚操作，有的语句没有执行成功过的时候，可以采取事务的操作处理。
	



	