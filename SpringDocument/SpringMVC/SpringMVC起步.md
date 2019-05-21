## SpringMVC起步
#### 目录
1. MVC简介
2. SpringMVC基本概念
3. SpringMVC项目搭建
4. SpringMVC进行开发
	* 如何编写一个Controller
	* 如何实现数据绑定
	* 如何文件上传
5. 课程总结

### 一、简介
1. MVC是开发Web应用程序的通用架构方式
	* ![WeChat2436a38e164bd877ab8f704fff7f7307.png](https://i.loli.net/2019/05/16/5cdd152c6405557017.png)
	* Http请求到了前端控制器，前端控制器了解到了这个请求应当被谁请求，交给了具体的控制器
	* 具体的控制器根据业务逻辑生成了业务数据，并将业务数据返回给前端控制器
	* 前端控制器再将业务数据交给View，加工后再返回给前端控制器
2. 为什么叫做前端控制器呢？
	* 类似于医院就诊的分诊台，分诊台就称为前端调度器<br/>![WeChatb2402bc8123bb89f7e797709f81e4046.png](https://i.loli.net/2019/05/16/5cdd163916b1375588.png)
3. MVC本质
	* 核心思想就是业务数据抽取同业务数据呈现相分离
4. 什么是MVC？
	* Model-View-Controller
	* 是一种架构模式，程序分层，分工合作，即相互独立，又协同工作
5. 什么是View
	* 是视图层，为用户提供UI，重点关注数据的呈现
6. 什么是Model
	* 业务数据的信息表示，关注支撑业务的信息构成，通常是多个业务实体的组合
7. 什么是Controller
	* 调用业务逻辑产生合适的数据(Model)，传递数据给视图层用于呈现

### 二、SpringMVC的基本概念
#### 2.1 静态概念
1. **DispatcherServlet**
	* SpringMVC作为一种前端控制器(Front Controller)的实现模式，**DispatcherServlet**就是一种前端控制器
	* 浏览器的实现请求正式通过DispatcherServlet的分发
2. **Controller**
	* 会生成数据模型
3. **HandlerAdapter**
	* 是DispatcherServlet内部使用的一个类，是Controller的一个表现形式
4. **HandlerInterceptor**
	* 是一个interface，拦截器
5. **HandlerMapping**
	* 帮助DispatcherServlet选择正确的controller
6. **HandlerExecutionChain**
	* 构成一个执行链条<br/>`preHandle`->`Controller method`->`postHandle`->`afterCompletion`
7. **ModelAndView**
8. **ViewResolver**
	* 视图解析器
9. **View**

#### 2.2 动态概念
1. 上面的9个基本概念的结构<br/>![WeChat513500f5f8d8d41a78a17fd8e349466b.png](https://i.loli.net/2019/05/16/5cdd3af84eb5354506.png)
2. ![WeChatd0650d5bbff9a6ba8d0bcf88564fd2df.png](https://i.loli.net/2019/05/16/5cdd3b655e49230323.png)

### 三、配置Maven环境
#### 3.1 介绍
1. POM(Project Object Model)
	* 是一个配置文件pom.xml，再配置文件中会完善很多信息来完善功能
	* 包含很多信息，依赖关系，组织，表述信息等
2. Dependency Management，依赖管理
	1. 需要依赖，依赖项目A，项目B等
	2. 在`/WEB-INF/lib`中，就不需要考虑jar包的依赖，可以提供依赖管理
3. Coordinates，坐标
	1. maven是如何实现依赖管理的，是通过坐标，maven就是一个仓库，其他插件的都在这个maven中提供组件
	2. 一个maven组件需要有四个属性：groupId、artifactId、version、packaging，通过这四个坐标可以唯一标示一个包，最后的packaging可以缺省

#### 3.2 安装Maven
1. 安装并解压
2. 配置环境变量
3. 配置Maven配置文件，本地仓库路径，镜像

#### 3.3 Maven配置
1. 安装后进入到Maven/conf文件夹下找到setting.xml文件
2. 将这个setting.xml文件拷贝出来，然后修改。
3. maven的默认配置文件在{$user.home}/.m2/repository
4. 找到setting.xml文件的<mirror>标签，里面有maven库的地址，视频中的是UK Central，使用的是http://uk.maven.org/maven2
5. 这个文件可以在maven版本升级后，还是使用之前的setting配置

#### 3.4 mvn的命令创建项目
1. 需调查


### 四、SpringMVC进行开发
#### 4.1 配置文件
1. web.xml，需调查配置

	
#### 4.2 常用注解
1. @Controller
2. @RequestMapping
3. Binding，@ModelAttribute on Method
4. redirect/forward，重定向

#### 4.3 文件上传
1. 类似MMALL中的文件上传
2. 添加pom


#### 4.4 Json
1. 添加pom




