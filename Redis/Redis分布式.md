## Redis分布式

* Redis分布式算法原理
* Redis分布式环境配置
* Redis分布式服务端及客户端启动
* 封装分布式Shard Redis API
* Redis分布式环境验证
* 集群和分布式概念讲解


### 一、Redis分布式算法原理
* 传统分布式算法
* Consistent hashing一致性算法原理	
* Hash倾斜性
* 虚拟节点
* Consistent hashing命中率


#### 1.1 传统分布式算法
1. 采用对要存的进行hash，然后根据Redis的数量进行取余的策略

#### 1.2 Consistent hashing一致性算法原理	
1. 环形hash空间，通常hash算法都是将value映射到32位的key值当中。
2. 将对象映射到hash空间，举例考虑4个对象object1～object4，通过hash函数计算出hash值的key，如下图
	
	![FE48E3FE-D79F-4E1F-AC78-255E09AB61EB.png](https://i.loli.net/2019/05/07/5cd0e6cc7413e.png)
	
3. 把cache映射到hash空间
	* 基本思想就是将对象和cache都映射到同一个hash数值空间中，并且使用相同的hash算法
	* hash(cache A) = key A;<br/>hash(cache C) = key C;
	* 一般cache选取的是ip地址和机器名
	![8C315B3F-7E2D-4856-8460-504560498E61.png](https://i.loli.net/2019/05/07/5cd0e88c3e494.png)
	
4. 在移除对应的cache的时候，不用像类似的传统的取余算法，担心找不到对应的cache
	![21090915-D3FC-47A5-913E-1EA45B41C8AB.png](https://i.loli.net/2019/05/07/5cd0e95588162.png)

5. 添加cache的时候，并不影响所有的节点，影响范围知识这个cache节点和上一个cache节点之间的key值存储的cache会发生变化。
	![CEDFAF43-9C4E-4B01-835B-D441310C232D.png](https://i.loli.net/2019/05/07/5cd0ea0f20888.png)


#### 1.3 Hash倾斜性
1. 理想当中的cache对数据的负载应该是均衡的，但是现实当中很有可能在环形分布cache位置不佳，导致单一的cache负载过大，其他的cache还过于清闲，这个就是因为Hash的倾斜性导致的
	![9D99F2A3-A1DC-4A16-ADE9-E5695116C3EA.png](https://i.loli.net/2019/05/07/5cd0eae7226a2.png)

#### 1.4 虚拟节点
1. 对hash的倾斜性，然后采取了一种虚拟节点的方式，这种方式将增加虚拟的cache节点，分布在环形中，几个虚拟节点再对应的真实的cache节点
	![6D7F88A7-954A-4996-B90E-EB25A5B0888A.png](https://i.loli.net/2019/05/07/5cd0ebc13116a.png)
2. 虚拟节点也会存在一定的hash倾斜性的问题，但是将虚拟节点和真实节点根据环境要求，存在一定的比例，增在很多虚拟节点，会将hash的倾斜性的问题降到最低。

#### 1.5 Consistent hashing命中率
1. 命中率计算公式为：`(1-n/(n+m)) * 100%`
	1. n：服务器台数
	2. m：新增的服务器台数
	3. 说明随着m的增加，变动会越来越小，随着分布式集群不断扩大的时候，算法的优点就会很自然的迸发出来
	4. 每个实际节点一般配置100～500个虚拟节点
2. 这个是一个经典的分布式算法，不仅仅可以应用在Redis的分布式上


### 二、Redis分布式环境配置
1. 项目中启动两个Redis的服务，一个是6379端口，一个是6380端口。

### 三、Redis分布式服务端及客户端启动
1. 修改两个redis的配置文件redis.conf
2. 修改端口一个为6379，一个为6380
3. 通过配置文件启动redis-server
4. redis-server ${redis[0-1]}的${redis.conf}，看启动哪个redis.conf

### 四、封装分布式Shard Redis API
1. ShardedJedis源码解析，就是分片的Jedis
2. 封装RedisShardedPool
3. 编写测试代码
4. 集成测试验证

### 五、Redis分布式环境验证
1. 查看两个Redis-server中的值

### 六、集群和分布式概念讲解
1. 集群
	* 表示的是一种物理形态，项目中的tomcat本质就是一个集群
	* 表示10个用户进入到10个服务器中，处理同一个问题，每台服务器负责处理一个用户
2. 分布式
	* 表示的是一种工作方式，处理任务的方式
	* 表示一个大任务，分成10个子任务，交由10个子服务计算
3. 这里一般对Tomcat的称之为集群，而Redis也可以叫做集群，但是工作方式不一样，所以称Redis的为分布式，从物理形态来看也是一个Cluster
4. [集群和分布式词汇举例解释](https://www.zhihu.com/question/20004877/answer/641895065)