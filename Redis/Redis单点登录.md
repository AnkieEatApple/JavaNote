## Redis单点登录
1. 使用的架构模式**为Redis+Cookie+Jackson+Filter**实现的单点登录
2. 操作流程
	1. 用户模块一期回顾
		* 用户登录、登出
		* 获取用户信息、是否登录
	2. 项目集成Redis客户端Jedis
		* 引入jedis pom
	3. Redis连接池构建及调试
		* JedisPoolConfig源码解析
		* JedisPool源码解析
		* JedisPool回收资源
		* 在Jedis的基础上封装RedisPool
	4. Jedis API封装及调试
		* 封装RedisPoolUtil，链接Redis，并set数据等
		* 集成测试验证
	5. Jackson封装JsonUtil及调试
		* Jackson封装JsonUtil及调试
		* 多泛型序列化和反序列化
	6. Jackson ObjectMapper源码解析
		* Inclusion.ALWAYS
		* Inclusion.NON_NULL
		* Inclusion.NON_DEFAULT
		* Inclusion.NON_EMPTY
		* SerislizationConfig.Feature.WRITE\_DATES\_AS\_TIMESTAMPSInclusion.NON_NULL
		* SerislizationConfig.Feature.WRITE\_DATES\_AS\_TIMESTAMPSInclusion.FAIL\_ON\_EMPTY_BEANS
		* DeserislizationConfig.Feature.WRITE\_DATES\_AS\_TIMESTAMPSInclusion.FAIL\_ON\_UNKNOWN_PROPERTIES
		* ObjectMapper DateFormat
	7. Cookie的封装及使用
		* 对Cookie的写(新增/更新)、读、删、domain
		* path、maxAge、httponly
	8. SessionExpireFilter重置session有效期
		* SessionExpireFilter编码及自测
	9. 用户session相关模块重构
		* 用户登录重构、登出重构、用户信息重构、用户是否登录重构
	10. Guava cache迁移Redis缓存
		* Guava cache用法
		* 集群后Guava Cache的不足
		* Guava cache迁移Redis缓存
	11. Multi-Process Debug自测验证，因为涉及到集群，多进程调试

	



