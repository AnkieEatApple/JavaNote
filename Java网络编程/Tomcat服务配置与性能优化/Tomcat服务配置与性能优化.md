## Tomcat服务配置与性能优化
1. Tomcat概述以及运行原理
2. Tomcat环境搭建
3. Tomcat配置详情
	* 单点登录
	* 多域名访问
	* Tomcat性能调优
4. 参考视频: [https://www.imooc.com/learn/1114](https://www.imooc.com/learn/1114)

#### 一、Tomcat概述以及运行原理

1. Tomcat简介，Tomcat就是一个可以解析Http请求的并且将结果反馈给客户端的一个应用程序
	* Apache软件基金会的核心项目
	* 开源软件
	* 中小型应用服务器
	* 2018年占比58.2%，JBoss占比20.2%，Jetty占比10.67%
2. Tomcat的结构图
	* **Server**是指整个Tomcat服务器，其中包含多个组件，主要是负责启动管理各个Service，同时还监听8005端口发过来的shutdown命令，用于关闭整个服务器。
	* **Service**是Tomcat分装的主要对外提供完整的基于组件的Web服务，它其中包含Connector和Container两个核心组件，以及多个功能组件，各个Service之间是独立的，但是它们会共享一个虚拟机资源
	* **Connector**是Tomcat与外部事件的连接器，监听固定的端口接受外部请求，然后将请求传递给Container，将Container的处理结果返回给外部
	* **Container**是一个Servlet容器，内部的话由多层容器组成，主要是用于管理Servlet的生命周期，调用Servlet的相关方法取处理业务逻辑
	* **Jasper**是Tomcat的JSP解析引擎，主要是将JSP文件转换成Java文件，并且编译成class文件
	* **Naming**是一个命名服务，主要是将名称和对象联系起来，使我们可以用名称访问对象。
	* **Session**是负责管理和创建Session，以及Session的一个持久化，其实是可以自定义的，而且支持Session的一个集群，对于Tomcat而言，session是一个服务器开辟的内存空间，开发中经常使用session来存储一些临时的信息
	* **Loging**是记录相关的日志，包含访问错误信息，一些运行信息等
	* **JMX**是应用程序、设备、系统等植入管理功能的框架，可以通过JMX监控Tomcat的一个原型运行状态。

	![E42DC429-9009-4CCD-BB55-71474D77BAA5.png](https://i.loli.net/2019/05/13/5cd8e234f02a654567.png)
	
3. 核心组件主要为Connector组件和Container组件
4. Connecter组件
	* 接受客户端连接
	* 加工处理客户端请求
	* 每个Connecter都会对一个端口进行监听，然后分别去负责请求报文的解析和详细报文的组装，解析的过程就会生成request对象，而组装的过程就会生成response对象
	* Connector其实就是一个连接器，类似于城堡的城门，每个人想进入城堡就必须进入这个城门，为每个人进入城堡提供了通道，同时一个城堡也可能有两个或者多个城门，每个城门就代表了不同的通道
5. Container组件
	* 所有子容器的父接口
	* 典型的责任链设计模式，其中有四个子容器，Engine包括Host，Host包括Context，Context包括Wapper
	* 责任链模式就是使多个对象都有机会处理同一个请求，从而避免发送者和接受者之间的耦合关系，将这些对象连城一条链，并沿着这条链传递请求，直到有一个对象处理它为止
		* Engine引擎，是用来管理多个站点，一个Service最多只能有一个Engine
		* Host，是代表一个站点，也可以叫虚拟主机，通过配置host就可以添加站点，在Tomcat的文件夹下的webapps这个目录就相当于一个站点，ROOT目录的应用就是主应用
		* Context，是代表一个应用程序，相当于一个war包
		* Wapper，可以理解成一个封装了的Servlet
		
	![38535892-C253-4960-A7B3-0DDDE6866BDE.png](https://i.loli.net/2019/05/13/5cd8e8341a43e91176.png)

6. Server处理Http请求的一个过程
	1. 用户点击网页的内容，请求被发送到本机的8080，然后被监听端口Connector的8080获得
	2. 接下来Connector该请求交给它所在的Service所在的Engine，接着等待Engine的回应
	3. Engine获得的请求是localhost/test/index.jsp，然后去匹配所有的虚拟主机，也就是host，Engine去匹配名为localhost的host，也就是站点，然后名称为localhost的站点或得到请求text/index.jsp，然后再去匹配所有的context，Host匹配到路径为/test的Context(如果匹配不到就把该请求交给路径名为" "的Context去处理)
	4. 而host匹配的路径为/text/Context，也就是项目，localhost其实是一个站点，然后text是一个项目
	5. 匹配到/text/context，获取的请求是index.jsp，在mapping-table中寻找对应的servlet，Context匹配到URL PATTERN为*.jsp的Servlet,对应于JspServlet类
	6. 对应的servlet就会构造两个对象，一个是httpServletRequest和Response两个对象，最为参数去调用JspServlet的doGet()和doPost()的两个方法，去执行业务逻辑，比如说数据存储等程序。
	7. Context就会把执行完的HttpServletResponse对象返回给Host
	8. Host把HttpServletResponse对象返回给Engine。 
	9、Engine把HttpServletResponse对象返回Connector。 
	10、Connector把HttpServletResponse对象返回给客户Browser。
	![090C5286-CEDD-437F-B524-5788EFA46CE8.png](https://i.loli.net/2019/05/13/5cd8eab44821e69203.png)
	
#### 二、Tomcat环境搭建
1. 搭建运行基础环境(JDK1.8)
2. 安装Tomcat服务器
3. 首先下载jdk，下载地址为：
[www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html](www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
4. 安装之前判断是否已经安装了JDK，java -version
5. Tomcat的下载地址：[tomcat.apache.org.download-70.cgi](tomcat.apache.org.download-70.cgi)
6. Tomcat的目录结构
	* bin目录，主要存放tomcat运行的命令，<br/>一类是以`.sh`结尾的linux下的，一类是以`.bat`结尾的windows下的，主要的是start.sh和shutdown.sh<br/>catalina.sh主要是设置tomcat的内存和字符集
	* conf目录，主要都是一些配置文件，主要都是在tomcat日常中使用的配置文件
		* catalina.poicy主要是防止jsp代码和用户代码破坏tomcat容器。
		* catalina.properties主要是包含了不能被jsp或servlet修改的jar的文件列表
		* context.xml是有关context的一个配置文件，由于context位于顶层，所以是它是为所有的web应用使用的配置文件，它默认的web服务就是web.xml这个文件的文件位置。
		* logging.properties是tomcat日志使用的配置文件，定制了tomcat的日志格式等等的信息
		* server.xml是tomcat中最主要的文件，定义了tomcat的体系结构，主要是在tomcat启动的时候构建tomcat的容器，常常在这个文件中修改tomcat的端口号
		* tomcat-users.xml是tomcat的web页面一些人员和管理员的信息
		* web.xml这个文件被tomcat的所有应用程序所使用，主要是配置一些启动信息，启动页什么的
	* lib目录，主要包含了tomcat使用的所有的jar包，并且也包含了用户多个Web应用包含的程序共享的jar包
	* logs目录，主要存放tomcat在运行的过程中存放的日志信息，其中比较重要的就是在控制台输出的日志。在windows中输出的格式是`catalina.日期.log`，在linux中输出的是`catalina.out`
	* temp目录，主要是存放用户在tomcat运行过程中产生的一些临时文件，这些文件清空对tomcat也没有什么影响。
	* webapps目录，主要是存放应用程序，当tomcat启动时，会加载webapps下的应用程序，可以是以文件夹、war包、jar包的形式发布应用。也可以放在磁盘的任意位置，然后在配置文件中配置映射就好
		* ROOT目录，是tomcat的一个跟目录，可以直接通过url访问到这个目录下的文件，比如localhost:8080/tomcat.png
	* work目录，tomcat在运行时编译后的一个文件，比如说jsp编译后的一个文件，还有一些jar编译后的一些文件，清空这个目录，然后重启tomcat，可以达到清楚服务器缓存的这样的一个效果
	
#### 四、Tomcat常用配置修改
1. 配置端口号<br/>端口号并不是无限大，取值范围是在1～65535，而且1～1024会固定分配给一些服务，21会分配给ftp服务，25端口会分配给smftp服务也就是邮件服务，80端口分配给http服务
	* 打开conf/server.xml，
		* SHUTDOWN端口对应的是8005，监听这个端口，关闭tomcat的请求
		* HTTP访问端口8080，主要负责建立http请求
		* AJP协议访问端口8009，主要是和其他的http服务建立连接，把tomcat和其他的http集成，在集成的时候就用到这个连接器
		* 安装的时候默认启动这三个端口，当运行多个tomcat服务器的时候就需要修改这三个端口，并且这三个端口都不能重复

2. 修改内存，内存模型
	* 修改内存的主要目的是？<br/>当一个项目比较大的时候，他所依赖的jar包比较多，在应用服务器启动的时候，项目引用的类依次加载到内存当中，所以需要根据项目不同，修改内存参数。
	* java的逻辑内存大致分为：
		* 堆内存: 主要是存储类的实例、数组、引用数据类型(也就是用new生成的对象)
		* 栈内存: 去存储一些局部变量，比如方法参数
		* 静态内存区(持久区): 该区的内存就不会被GC回收，主要存储一些静态变量，常量，类的元数据，比如说方法、属性等等
3. 内存溢出
	* OutOfMemoryError: Java heap space异常
		* 这个异常表示堆内存满了，如果不是程序逻辑的bug，可能就是项目中引用的jar比较多，导致内存溢出，
		* JVM默认的堆内存最小的是使用我们物理内存的`1/64`，最大的是使用物理内存的`1/4`，最大不要超过物理内存的80%，就可以通过调整虚拟机的初始内存和最大内存这两个参数来加大内存的使用限制
	* OutOfMemoryError: PermGen space异常
		* 表示静态内存区满了，通常由于加载的类太多导致的，JDK8以下的版本需要修改PermSize和MaxPermSize这两个参数，限制静态区域最小最大的内存范围
		* 而JDK8改变了内存模型，将类定义放到了元数据空间，而元数据空间和堆内存共享一块内存区域，所以在JDK8之后就不会出现该异常了。
	* StackOverflowError异常
		* 表示栈内存溢出，通常是由死循环和无限递归导致的
4. 修改内存参数，修改catalina.sh参数，一般是配置到catalina的第二行就可以。`JAVA_OPTS="-server -Xms256m -Xmx512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"`
	* 在性能方面还需要考虑垃圾回收机制，虚拟机的堆大小决定了垃圾回收机制的时间和频率，如果堆的大小很大的话，那么垃圾回收的时间就会变的很慢，频率也会降低，调整堆大小的目的就是优化GC回收的时间和频率，以便最大化的处理用户的请求。
	* 其实在基本测试的时候，为了保证最好的性能，会把堆的大小适当，保证垃圾回收在基准测试的过程中出现，如果系统话费很多时间回收垃圾，就需要把堆这个参数减小一下，正常情况下一次垃圾回收应该不超过3～5秒，一般会使用物理内存的80%左右

5. 配置热部署
	* 什么是热部署?<br/>就是在不重新启动tomcat的前提下，将自己的项目部署到tomcat服务器中。这种方式非常方便，也称之为开发即用。
	* 如何配置热部署。
		1. 直接将war包拖放在webapps里面
		2. 打开`conf/server.xml`文件，添加配置项: `<Context debug="0" docBase="" path="/demo1" reloadable="true"/>`
			* debug关联的是log调试记录的详细信息，数值越大，就越详细，没有指定的话，就是为0
			* docBase为项目的一个绝对路径/相对路径(相对于webapps的)，需要指定到文件名
			* path就是url(类似localhost:8080)后面跟的一个字符串
			* reloadable如果是true的话，就会自动加载新增或改变的class文件。
		3. 在conf的Catalina/localhost文件夹下新建一个demo2.xml，将2中的信息拷贝到该文件中	`<Context debug="0" docBase="" reloadable="true"/>`

6. 配置连接池和数据源
	1. 什么是连接池？<br/>
	2. 如何配置数据源？<br/>
	3. 加载JDBC操作数据库的流程
		* 1）加载驱动，2）创建连接，连接数据库，3）执行SQL，4）释放连接
		* 在频繁的操作数据的时候，在124步会有重复操作，所有人只有在第三步不一样，这样就会造成一个性能的损耗，然后就有一个解决办法，准备一块空间，空间内专门保存着全部的数据库连接，以后用户在操作数据库的时候，就不用再去加载驱动之类的操作，直接从空间中取走连接即可，关闭的时候直接将连接放回到空间之中即可。这个空间就可以称之为连接池。
		* 数据库连接池就是在程序初始化的时候，集中创建多个数据库连接，并且把他们集中管理供程序去使用，不仅可以保证一个较快的读写速度，并且还更加的安全可靠，tomcat在7.0版本之前都是使用DBCP作为连接池实现的，DBCP饱受诟病，因为DBCP是一个单线程的，为了线程安全，会锁掉线程池，并且性能也不好，还比较复杂。在7.0之后就兼容了一个JDBC库
		* tomcat的JDBC库有两种配置，一种是异步的配置连接，还有一种就是独立的配置连接
	
#### 五、Tomcat实现web管理
1. 管理界面功能概述
	* 可以使用localhost:8080的那个web界面管理发布war包，可以在界面直接发布war包，但是当war包比较大的时候，需要在`webapps/manager/WEB_INF/web.xml`的50行左右，<max-file-size>的配置和<max-request-size>的配置中修改war包的大小

#### 六、Tomcat配置单点登录
1. 什么是单点登录？
	* 单点登录(Single Sign On)，简称SSO，是目前比较流行的企业业务整合的解决方案之一。
	* SSO的定义是在多个应用系统中，用户只需要登录一次，就可以访问所有相互信任的应用系统，类似于游乐场中的通票的理解，不用再分别在每个游乐设施再购买票
	* ![WeChatc6c04ac20723a46895e5ae54fc38fa58.png](https://i.loli.net/2019/05/13/5cd92ae5c624176801.png)

2. CAS
	* 开源的企业级单点登录的解决方案
	* CAS Server和CAS Client，CAS Server需要去独立部署，主要负责对用户的一个认证工作，CAS Client主要处理对客户端受保护资源的一个访问请求
	* ![WeChat4c9535847ba7cbb861acd660ccac26f8.png](https://i.loli.net/2019/05/13/5cd92bc61faeb87101.png)
	* CAS的官网地址下载：[www.apereo.org/projects/cas/download-cas](www.apereo.org/projects/cas/download-cas)
	* 实际项目中可以根据session和redis实现单点登录

3. 多域名访问
	* **提高硬件资源的利用率**，有很多网站通过配置虚拟主机的方式，实现服务器的共享，tomcat允许用户在同一台机器上配置多个web站点，在这种情况下，需要对每个web站点配置不同的主机名，也就是配置虚拟主机，就可以实现多域名访问
	* 配置多域名访问，分别为基于ip地址和基于端口的虚拟主机
		* 配置`conf/server.xml`，需要加一个Host标签，主要是添加了一个映射的访问的域名，通过这个域名来访问这个地址
		* 配置`conf/server.xml`，需要加一个Server标签，添加一个对应的Server，可以实现相同IP地址下，不同端口号访问另一个项目
		* 这种建议直接上docker，或者直接使用nginx的反向代理来访问

#### 七、Tomcat安全配置
1. 安装后初始化配置
	* 关闭服务器端口
		* 在远程连连接电脑后，执行对应的脚本，就可以远程关闭这个tomcat服务，这样很危险啊同志
		* 解决办法可以修改端口和对应的关闭字符串，例如将SHUTDOWN，改为QUIT之类的
	* 隐藏版本信息
		* 避免黑客对某些版本进行攻击，因此需要隐藏或伪装tomcat的版本信息
		* 在`lib/catalina.jar`的文件中，`org/apache/catalina/util/ServerInfo.properties`中的`server.info`参数改为NO-VERSION之类的什么都好
	* 禁用Tomcat管理界面
		* 防止黑客通过tomcat管理界面对tomcat进行攻击，因为上面有很多配置信息，可以通过拆解密码，控制管理界面用来攻击我们的服务器，或者是通过界面显示的一些配置信息进行攻击
		* 最好的办法就是将webapps中的ROOT文件重命名，然后创建一个空的ROOT文件，这样就访问不到那个经典的管理界面了
	* 自定义错误页面
		* 当我们访问一个不存在的页面的话，他会返回一个默认的404页面，这样的页面直接暴露给用户其实不是很友好，并且有时候对服务器还会产生一些安全隐患，因此一般会修改一下默认的错误页面
		* 将写好的error.html放在webapps/ROOT目录下，打开conf/web.xml，在最下面加入一些跳转路径<error-page>标签
		* ![08504067-3091-4665-9ADE-6B9921FD07BF.png](https://i.loli.net/2019/05/13/5cd94d9e5201725587.png)
	* AJP端口管理
		* AJP就是tomcat和http通信之间定制的一个协议，他能提供一个比较高的通信速度和效率，在tomcat的前端，如果用的是apache服务器，就会使用到AJP服务器，但是前端用的是Nginx的反向代理的话，就可以不使用这个连接器，因此就需要注释掉这个连接器
		* 打开conf/server.xml，找到AJP/1.3，8019这个端口，把这个注释掉就好了，如果是apache的话，就必须用这个
	* 如何使用HttpOnly提高cookie的安全性
		* 什么是Cookies？
			* 保存在客户端的纯文本文件，当通过浏览器访问服务端网页的时候，服务器就会生成一个证书返回给我们的浏览器，然后写入到我们的本地电脑，那么这个证书就是Cookie，一般来说Cookie都是服务端写入客户端的纯文本文件
		* Cookie实现离线购物车，在session出现之前，都是用cookie来保存请求的数据，用户向服务器发送的请求，服务器根据用户特定的请求向客户端进行展示，也就是没有Cookie就不能看到浏览器上购物车里的东西，类似于浏览器的收藏夹，保存了浏览器的状态，归根结底就是http的无连接性
		* XSS-跨站脚本攻击，是指黑客往Web页面，插入恶意的Html代码，当用户浏览该页面时，当用户浏览该页面时，嵌入的html的代码就会执行，从而达到恶意攻击用户的目的，如果在Cookie中设置了HttpOnly属性，通过程序就无法攻击或读取Cookie的信息，这样就能有效的防止XSS攻击。
			* 打开`conf/context.xml`，在大约19行的<Context>中添加`useHttpOnly=true`，即`<Context useHttpOnly=true>`这样我们就会对它的cookie进行一个加密
			* 像这种具有HttpOnly属性的cookie，一般称为HttpOnlyCookie，这种Cookie暴露给黑客和恶意网站的几率就会大大降低
2. 安全规范
	1. 关于账号管理、认证授权
		* 共享、无关账号，其实就是设置一些相应权限和铲除一些没有用的账号，这样防止无关账号来登录我们的服务器，对我们的服务器造成一些破坏，或者是信息的盗取
		* conf/tomcat-users.xml这里可以通过rolename配置
		* 口令密码：最好90天内一换，满足大小写数字字母等
		* 用户权限：这里最好配置一个最小的权限
	2. 日志配置的操作
		* conf/server.xml的配置的最后有关于日志的配置
		* classname：是catalina日志的一个包
		* Directory：是日志文件存放的一个目录，在tomcat下面有logs的这么一个文件夹，下面专门存放日志文件，当然也可以修改其他的路径
		* Prefix：是日志名称的一个前缀
		* Suffix：是日志文件的一个后缀
		* Pattern：如果是common的话，将记录访问源的ip，本地服务器的ip，记录日志服务器ip的访问方式，发送字节数，本地的接受端口等相关信息
		* resloveHosts：如果是true就会将服务器的ip地址通过DNS转化为主机名，如果是false就会写ip地址。
		* 主要就是为了记录tomcat的日志
	3. 设备其他操作 
		* 对于字符串交互，对于数据交互的界面，应该支持定时的账户一个登出，来保障一个数据的安全性，登出以后，用户需要再次才能登入系统
		* conf/server.xml：在8081端口，<Connector 标签下，有一个connectionTimeout的时间，这个就是自动登出的时间，默认的是20000毫秒
		
#### 八、Tomcat性能优化配置
1. 缓存优化(nginx、gzip)
	* 对于静态页面最好能将其缓存起来，这样就没必要每次从磁盘上去读，这里采用nginx作为缓存服务器，将图片、css、js文件都缓存，这样就有效的减少了tomcat的一个访问频次
	* 为了能加快网络的一个传输速度，可以开启gzip，但是考虑到tomcat已经处理很多很多工作了，可以将这个gzip交给前端的nginx来完成。
	* gzip可以压缩文件，提高传输速度，但是也会增加服务器的负载，图片也可以进行压缩
	* 总归来讲，就是高频次的一些东西，可以从服务器挪到缓存里面，这样的话，不仅访问速度快，而且用户体验也会更好
2. 运作模式
	* BIO：tomcat7及以下的运作模式，性能是非常低的，没有经过任何的优化处理，并且一个线程只能处理一个请求，还有一个就是在并发量比较多的时候，它的线程比较多，很浪费资源
	* NIO：基于缓存区、非阻塞的I/O，是1.4版本以后提供的一种I/O方式，是一种提供缓存区，并且提供一组非阻塞操作的JavaAPI，因此NIO也被看作是No-blocking的一个缩写，操作比传统I/O更好的一个并发性能
		* 有一个很大的特点就是，可以利用Java的一步I/O处理，通过少量的线程来处理大量的请求
	* APR：tomcat7及以上默认模式，从操作系统的层面解决异步I/O的一个问题，大幅度的提高了性能，windows是这种方式，linux如果安装了APR，tomcat启动的时候就会直接支持APR
	* 总的来说还是推荐使用NIO这种运动模式