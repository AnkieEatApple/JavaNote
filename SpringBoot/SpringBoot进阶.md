## 1
1. web进阶
	1. 使用@Valid表单验证
	2. 使用AOP处理请求
	3. 统一异常处理
	4. 单元测试
	
#### 一、表单验证
1. 在路由请求的方法参数，由之前的`@RequestParam("cupSize") String cupSize, @RequestParam("age") Integer age`形式改变为，`Girl girl`的对象的形式，这样也可以直接接受，前提是Girl已经存在
2. 可以在Girl的模型中对某一个字段进行拦截，在某一个字段上添加
	
	```
	@Min(value = 18, message = "未成年少女禁止入内")
	private Integer age;
	
	// 在这里添加上BindingResult参数
	@PostMapping(value = "/girls")
	public Girl girlAdd(@Valid Girl girl, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        System.out.println(bindingResult.getFieldError().getDefaultMessage());
	        return null;
	    }
	    return girlRepository.save(girl);
	}
	```
	
#### 二、AOP统一处理请求日志
1. AOP
	* AOP是一种编程范式
	* 与语言无关，是一种程序设计思想
	* 面向切面(AOP), Aspect Oriented Programming
	* 面向对象(OOP), Object Oriented Programming
	* 面向过程(POP), Procedure Oriented Programming
2. 面向过程->面向对象，换个角度看世界，换个姿势处理问题
3. 面向对象关注的是将需求功能垂直划分为，不同的并且相对独立的，会封装成良好的类，并将它们有自己的行为
4. AOP技术恰恰相反，利用一种横切的技术，将面向对象构建的庞大的类的体系，进行水平的切割，并且会将那些影响到了多个类的公共行为封装成了一个可重用的模块，这个模块称为切面，所以AOP成为面向切面编程
	* **将通用的逻辑从业务逻辑中分离出来**

5. 首先添加依赖,<br/>`<groupId>org.springframework.boot</groupId>`<br/>`<artifactId>spring-boot-starter-aop</artifactId>`
6. 创建一个aspect的包，在这个包里创建一个HttpAspect的类，在这个类中，可以统一定义访问指定的url对应的方法之前和之后中切片层面插入对应的log的作用，还可以获取到访问信息等
	
	```
	@Aspect
	@Component
	@Slf4j
	public class HttpAspect {
	    @Pointcut("execution(public * com.imooc.controller.GirlController.*(..))")
	    public void log() {}
	
	    @Before("log()")
	    public void doBefore(JoinPoint joinPoint) {
	
	        ServletRequestAttributes attributes  = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	        HttpServletRequest request = attributes.getRequest();
	        // url
	        log.info("url = {}", request.getRequestURL());
	        // method
	        log.info("method = {}", request.getMethod());
	        // ip
	        log.info("ip = {}", request.getRemoteAddr());
	        // 类方法
	        log.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
	        // 方法参数
	        log.info("method_args={}", joinPoint.getArgs());
	    }
	
	    @After("log()")
	    public void doAfter() {
	        log.info("2222222222222");
	    }
	
	    @AfterReturning(returning = "object", pointcut = "log()")
	    public void doAfterReturning(Object object) {
	        log.info("response={}", object);
	    }
	}
	```
	
#### 三、统一的异常处理
1. 代码中的异常
2. 为什么需要统一异常处理，类似于慕课商城中的ServerResponse的类的构成，这个事实对返回类型的包装
3. 对于Service层中的有异常的时候，返回不同的返回值时，在Controller层还要判断一下，这样就造成了需要定义新的flag值，并且在Controller中重复判断了，就会很麻烦
4. 可以在Service层抛出新的Exception异常，然后通过捕获异常来返回包装好的ServiceResponse类
5. spring框架只对RuntimeException进行回滚
6. 可以自己创建一个GirlException类，继承自RuntimeException，然后对这个类进行自定义，用来处理不同的Exception异常，返回不同的校验码
7. 可以在位置错误上面添加一个log.error，定义一个异常的处理，这样可以方便在日志中查找
8. 可以在枚举中将code、msg来定义错误码和错误，这样就会方便的统一管理。


#### 四、单元测试
1. 测试对应的服务的接口，进入到测试的目录，在目录中对好是对应的一系列接口新创建一个新的类，
	* 这个类需要添加`@RunWith(SpringRunner.class)`注解，表示在测试环境下运行，底层是JUnit测试工具
	* 还需要添加`@SpringBootTest`注解，标示需要添加整个springboot的工程
	* 在对应的测试方法上添加`@Test`注解，可以注解运行这个
2. 也可以直接对要测试的方法，右键俄案后直接添加测试代码。就会创建测试类和测试代码	
3. 测试带有url的测试，可以通过类似postman之类的测试，也可以用IDEA中的test测试
	* 测试类需要多添加的注解是`@AutoConfigureMockMvc`，在对应的test方法中添加测试方法

	```
	@RunWith(SpringRunner.class)
	@SpringBootTest
	@AutoConfigureMockMvc
	public class GirlControllerTest {

		@Autowired
		private MockMvc mvc;

		@Test
		public void girlList() throws Exception {
			mvc.perform(MockMvcRequestBuilders.get("/girls")).andExpect(MockMvcResultMatchers.status().isOk());
		}
	}
	```
4. 打包的时候跳过测试 `mvn clean package -Dmaven.test.skip=true`