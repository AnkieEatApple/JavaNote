## Redis基础

1. Redis简介
2. Redis常用数据类型
3. Redis开发语言客户端的介绍
4. 本项目使用的Redis版本
5. 安装Redis
6. Redis单实例的配置
7. Redis单实例服务端、客户端、启动及关闭
8. Redis单实例环境验证
9. Redis基础命令
10. Redis键命令
11. 五种数据结构，Redis命令快速上手

#### 一、Redis简介
1. Redis-REmote DIctionary Server的简写
2. Redis是一个使用ANSI C语言编写的开源数据库
3. 高性能的key-value数据库
4. 内存数据库，支持数据持久化
5. Redis官网: [https://redis.io/](https://redis.io/)，国内官网: [http://redis.cn/](http://redis.cn/)
6. 提供了Java、C/C++、C#、PHP、JS、Perl、Object-C、Python、Ruby、Erlang等客户端
7. 从2010年3月15日开始，Redis的开发工作由VMware主持，从2013年5月开始，Redis的开发由Pivotal赞助
8. Redis常用的数据类型
	* 数据类型(type): string、hash、list、set、sorted set
	* 编码方式(encoding): raw、int、ht、zipmap、linkedlist、ziplist、intset
	* 数据指针(ptr)
	* 虚拟内存(vm)
	* 其他信息...
9. 开发语言客户端的地址，官网: [https://redis.io/clients](https://redis.io/clients)，国内官网: [http://redis.cn/clients.html](http://redis.cn/clients.html)，其中笑脸的版本是使用比较多的稳定的版本。
10. 本项目使用的Redis版本是redis-2.8.0.tar.gz，下载地址为：[http://download.redis.io/releases/](http://download.redis.io/releases/)
11. 若为Windows，使用的版本是Redis-x64-2.8.2402，客户端不是官网维护的，是由Microsoft Open Tech group维护的，下载的地址为：[https://github.com/MicrosoftArchive/redis](https://github.com/MicrosoftArchive/redis)


#### 二、Redis安装
1. 下载tar包之后直接解压，然后进入文件夹，输入`make`命令进行编译，编译后输入`make test`进行测试，如果提示没有错误的话，表示编译成功。
2. 进入到src文件夹，执行`./redis-server`命令，会出现redis启动成功，还可以使用`./redis-server & `这样不会再命令行中阻塞占用
3. redis启动之后，会发现redis里面有程序正在运行的PID，可以通过`kill -9 PID`杀死进程
4. 还是在src文件夹中，执行`./redis-cli`命令，就会直接进入到client中，可以直接输入`set a b`之类的命令
5. 在windows中直接运行exe程序即可启动服务，同时在当前目录执行`redis-cli`即进入到了client目录

#### 三、Redis单实例配置及服务端启动
1. redis.conf配置文件
2. port端口
3. requirepass密码，打开这个注释`#`，在后面直接添加密码即可，然后客户端需要在链接服务端的时候，输入密码，输入不正确会导致没有访问权限。
4. masterauth主从同步中在slave配置master的密码

##### 3.1 单实例服务端启动
1. redis-server，最简单的执行，直接在src中执行就可以
2. redis-server ${redis.conf}，指定配置文件启动服务
3. redis-server --port ${port}，选择执行的端口来启动server

##### 3.2 单实例客户端启动
1. redis-cli
2. redis-cli -p ${port}，选择一个执行的port启动，默认的是6379
3. redis-cli -h ${ip}，后面的变量值一个ip
4. redis-cli -a ${password}，认证密码
5. redis-cli -p ${port} -h ${ip} -a ${password}，组合使用

##### 3.3 单实例服务端、客户端关闭
1. redis-cli shutdown
2. redis-cli -p ${port} shutdown
3. redis-cli -h ${ip} shutdown
4. redis-cli -p ${port} -h ${ip} shutdown

##### 3.4 单实例环境验证
1. 可以ping
2. 执行redis set命令相关，然后查看redis get命令获取到的值

#### 四、Redis命令
##### 4.1 基础命令
1. info
2. ping，在启动服务端和客户端后，在客户端指定ping命令之后，出现PONG表示链接成功。
3. quit
4. save，在键入一些数据之后，若不执行save命令，这些数据是不会持久化到磁盘上面的，需要执行save命令后才可以，服务端也会受到一些关于save命令信息。一般情况下客户端在退出之后，服务端会对客户端储存的数据直接进行持久化，属于人工触发的持久化命令
5. dbsize，查看当前的Keyspace的数量
6. select，可以通过`select 1`选择Keyspace为1的空间，set新的键值对然后查询可以得出在这个Keyspace中新添加的键值对，select用于选择新的Keyspace，可以选择的由0~15，在redis.conf中databases的关键字中定义的，当对应的Keyspace存储了keys之后，可以通过info命令查看到当前的Keyspace就会存在
7. flushdb，清除当前的Keyspace的keys
8. flushall，清除所有的Keyspace的keys

##### 4.2 键命令
1. set， 输入键值对，例如`set test test`
2. del，删除键值对，例如`del test`，返回1表明删除成功，返回2表明删除失败
3. exists，判断这个key是否存在，例如`exists a`，返回1表明存在，返回0表明不存在。
4. expire，对一个key设置过期时间，单位是秒，设置时间后可以使用`ttl key`来查看
5. ttl，设置一个key的过期时间，在单点登录的时候要用到，返回-1表示不会过期，返回-2表示这个key不存在
6. type，查看这个key的类型，例如`type b`，返回这个key的类型
7. randomkey，随机获取key值
8. rename，重新命名key的值，若这个key的值已经存在，会直接覆盖这个key的值，原来的key值将不存在。
9. renamenx，重命名原来的key值，`renamenx a b`，若b已经存在的话，会返回0，不存在的话返回1
10. setex，`setex c 100 c`，是按照秒，对这个100秒储存key
11. psetex，`psetex d 10000 d`，这个是毫秒，也就是10秒的储存的key
12. getrange，`getrange word 0 2`，表示这个是这个word的key下的字符串从头开始取到第2个字节，取三个字节。
13. getset，`getset a aa`，这个是先get，再set这个aa值，同时返回的值也是a这个值，也就是原来的这个key存储的值。
14. mset，批量set，`mset key1 value1 key2 value2 key3 value3 ...`
15. mget，批量get，`mget key1 key2 key3`
16. setnx，set前判断是否存在这个key，存在返回0，不存在set成功返回1
17. strlen，`strlen key`，查询当前的key对应的value的长度
18. msetnx，也是具有原子性的批量操作，会将不存在的key储存，将存在的key返回
19. incr，要求value是数值，然后对key进行操作，会使value + 1
20. incrby，自定义步长，`incrby key 100`，对key下的value进行增加100
21. append，在value后面链接字符串，这个不限制类型，`append key appendstr`


##### 4.3 Redis常见的五种数据结构
1. string：字符串
	1. 见上面的常用命令
2. list：链表
	1. lpush list 1 2 3 4 5 6 7 8 9 10，在list这个里面放了10个值
	2. type list，查看list的类型
	3. llen list，查看list的长度
	4. lrange list 0 2，查看list，0到2的值，会显示10，9，8
	5. lset list 0 100，将第0个位置修改为100
	6. lindex list 0，显示第0位置的value
	7. lpop list，移除list的第一个元素，也就是100
	8. rpop list，移除list的第一个元素，也就是是1
	9. 关于查看链表的元素，可以通过lrange list 0 100, 可以看100个元素。
3. set：无序集合
	1. sadd set a b c d，添加a b c d到set的集合中
	2. type set，查询set的类型
	3. scard set，查询set的内部元素的数量
	4. rename set set1，对set进行重命名为set1
	5. smembers set2，查看set2中的成员
	6. sdiff set1 set2，对比set1和set2中的成员不同，这个谁在前面显示谁的不同，set1在前就显示set1不同的部分，set2在前同理。
	7. sinter set1 set2，查看两者交集
	8. sunion set1 set2，查看两者并集
	9. srandmember set1 2，随机返回member的两个元素
	10. sismember set1 a，判断a是不是set1的成员元素，返回1表示是，0表示不是。
	11. srem set1 a b，移除set1中的a、b元素
	12. spop set2，随机移除这个set2中的元素，并返回这个元素

4. sorted set：有序集合
	1. zadd sortedset1 100 a 200 b 300 c，添加元素到有序集合中，会按照分数对元素进行排队
	2. type sortedset1，集合类型为有序集合
	3. rename sortedset1 sortedset，改名为sortedset
	4. zcard sortedset，查看集合中的元素个数
	5. zscore sortedset a，查看a元素在集合中对应的分数
	6. zscore sortedset 0 200，查看0到220的分数的元素的个数
	7. zrank sortedset a，查看集合中a的索引的值
	8. zincrby sortedset 1000 a，对a的分数值加1000，然后再查看索引值，就可以查看到索引值变换了，这个里面的元素是不能重复的，但是分数是可以重复的。
	9. zrange sortedset 0 100，查看0到100索引下的元素
	10. zrange sortedset 0 100 withscores，查看0到100索引下的元素和他们的分数。
5. hash：Hash表
	1. hset，set的处理的hash，`hset map name jim`，表示hset处理的是name和jim，这个时候存的是在map中的name和jim的键值对，而map是一个hash类型
	2. type map，可以查看map的类型是hash类型
	3. hexists map name，这里查看的是map中的name的对应的value是否存在，存在的话，返回1，不存在返回0
	4. hgetall map，获取所有的map下的所有的key值和map
	5. hkeys map，获取所有的map下的key
	6. hvals map，获取所有的map下的value
	7. hlen map，获取当前的key的数量
	8. hmget map name age，批量获取map下的value值
	9. hmset map newname newnamevalue newage newagevalue，批量储存
	10. hsetnx map name newjim，储存前判断name这个key是否存在。

#### 五、Redis基础备注
1. 这里主要是针对单例的模式下进行的这个教程，为下载、安装、启动，配置、基础命令的部分。



