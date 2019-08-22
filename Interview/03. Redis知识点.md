## Redis相关知识点
1. Memcache和Redis
2. 多路I/O复用
3. 常用类型
4. 海量数据中筛选某一固定前缀的key
5. 实现简单的分布式锁
6. 实现异步队列
7. 持久化：RDB、AOF、混合
8. Redis主从
9. Redis哨兵
10. Redis集群

### 一、简介
1. 主流应用架构<br/>![WeChat6d31574f4f3f917ccf19fcc0d43ede9a.png](https://i.loli.net/2019/07/06/5d20589b81a8188357.png)
2. 这样就是为了方便实现缓存读写技术方便
3. 缓存中间件主要有Memcache和Redis的区别
4. Memcache：代码层次类似Hash
	* 支持简单数据类型
	* 不支持数据持久化存储
	* 不支持主从
	* 不支持分片
5. Redis
	* 数据类型丰富
	* 支持数据磁盘持久化存储
	* 支持主从
	* 支持分片
6. 为什么Redis能这么快，官方提供能达到100000+QPS(QPS即query per second，每秒内查询次数)
	* 完全基于内存，绝大部份请求是存粹的内存操作，执行效率高。<br/>redis采取的是单进程单线程的数据库，由C语言编写，都是写在内存里面，不回受硬盘的IO限制
	* 数据结构简单，对数据操作也简单。<br/>redis不使用表，不回强制要求对存储的数据进行关联，因此会比关系型数据库高出一个量级，存储结构就是一个键值对，时间复杂度都是O(1)的
	* 采用单线程，单线程也能处理高并发请求，想多核也可启动多实例<br/>redis没有采取线程池来处理业务，redis的单线程结构指的是主线程是单线程的，这里处理IO请求和IO相关请求的处理，正因为是单线程的设计，对客户端的所有请求，都由一个主线程串行的处理，因此多个客户端同时对一个键进行写操作的时候，就不会有并发的问题  
	* 使用多路I/O复用模型，非阻塞IO<br/>若是阻塞IO，会存读取阻塞<br/> 
	* Redis采用的I/O多路复用函数：epoll(linux)/kqueue(macos)/evport/select
		* 因地制宜，因为可能要运行的操作系统不同 
		* 优先选择时间复杂度为O(1)的I/O多路复用函数作为底层实现
		* 都是以时间复杂度为O(n)的select作为保底
		* 基于react设计模式监听I/O事件，这里不是很理解

		
### 二、数据类型
1. 提供用户使用的数据类型
	* String: 最基本的数据类型，二进制安全，最大能存储512M，可以包含JPG图片和序列化的对象
	* Hash: String元素组成的字典，适合用于储存对象
	* List: 列表，按照String元素插入顺序排序
	* Set: String元素组成的无序集合，通过哈希表实现，不允许重复
	* Sorted Set: 通过分数来为集合中的成员进行从小到打的排序
	* 用于技术的HyperLogLog，用于支持存储地理位置信息的Geo

2. 底层数据类型基础
	1. 简单动态字符串
	2. 链表
	3. 字典
	4. 跳跃表
	5. 整数集合
	6. 压缩列表
	7. 对象


### 三、从海量Key里查询某一固定前缀的key
##### 1. 首先注意细节
1. 摸清数据规模，即问清楚边界，也就是数据量是多大

	
##### 2. 若是直接回答使用keys指令查询的话，会遭埋伏
1. KEYS pattern：查找多有符合给定模式的pattern的key，例如`keys k1*`
	* Keys指令一次性返回所有匹配的key
	* 键的数量过大会使服务卡顿

##### 3. 解决方法
1. `SCAN cursor [MATCH pattern] [COUNT count]`
	* 是一个基于游标的迭代器，需要给予上一次游标延续之前的迭代过程
	* 以0作为游标开始一次新的迭代，知道命令返回游标0完成一次遍历
	* 不保证每次执行都返回某个给定数量的元素，支持模糊查询
	* 一次返回的数量不可控，只能大概率的符合count参数
	* 命令使用，`scan 0 match k1* count 10`，这样就可以获取到返回的值，每次会返回一个新的游标，下次查询的时候可以将scan的游标添加为返回的游标
	* **注意**：这里可能会出现返回的key值里面出现重复的情况，需要在上层的业务代码中添加一下去重的代码。
	* 可以在业务代码中添加一个for循环，虽然这样每次循环的总体时间要比直接keys的时间要长，但是不回造成服务卡顿

2. Java中有Jedis包来提供开发


### 四、实现分布式锁
##### 1. 需要解决的问题
1. 互斥性，不能两个client同时获取到锁
2. 安全性，锁只能被持有该锁的客户端删除
3. 死锁，获取锁的客户端宕机，无法释放该锁，而其他client再也无法获取该锁
4. 容错，当一些redis节点宕机的时候，仍然能获取锁

##### 2. 实现方式
1. setnx key value：如果key不存在，则创建并赋值
	1. 时间复杂度：O(1)
	2. 返回值：设置成功，返回1，设置失败，返回0
2. 解决setnx长期有效的问题，expire key seconds 
	1. 设置key生存时间，当key过期时(生存时间为0)，会被自动删除
	2. 其中伪代码逻辑为，若setnx执行成功返回1的时候，设置过期时间，然后执行业务逻辑<br/>![WeChatfc74066eca45463253eca2399a8126b5.png](https://i.loli.net/2019/07/06/5d20828442b2774903.png)<br/>**但是这段程序会有风险，在刚刚setnx的时候就挂掉了，就会一致锁住**	
3. set和expire融合在一起，Set key Value [EX seconds] [PX milliseconds] [NX|XX]
	1. EX second: 设置键的过期时间为second秒
	2. PX millisecond：设置键的过期时间为millisecond毫秒
	3. NX：只在键不存在的时候，才对键进行设置操作
	4. XX：只在键已经存在时，才对键进行设置操作
	5. set操作成功完成时，返回OK，否则返回nil
	6. `set localtarget 12345 ex 10 nx`，想修改就需要在时间10s结束了的时候修改value
	7. 实现的伪代码逻辑<br/>![WeChat057c7df6ba61dd0c9bed6f1d2c2193c5.png](https://i.loli.net/2019/07/06/5d20857152dbe58516.png)


##### 3. 大量的key同时过期的注意事项
1. 集中过期，由于清除大量的key很耗时，会出现短暂的卡顿现象。
2. **解决方案：**在设置key的过期时间的时候，给每个key加上随机值

### 五、使用Redis做异步队列
#### 5.1 List生产消费模式
1. 使用List作为队列，RPUSH产生消息，LPOP消费消息
2. 例如: `rpush testlist aaa`等，取出的时候，直接`lpop testlist`即可
3. 当lpop没有数据的时候，表示当前的list中没有数据
	1. 缺点：没有等待队列里有值就直接消费
	2. 弥补：可以通过在应用层引入Sleep机制取调用LPOP重试
4. 如果不使用Sleep方式的话，还可以使用`BLPOP key [key...] timeout`: 阻塞直到队列有消息或者超时
5. 可以使用两个客户端同时模拟生产者和消费者，BLPOP会更精准的阻塞控制，`blpop testlist 30`，也就是等待30秒，等待生产者输入
	* 缺点：只能供一个消费者消费
	
#### 5.2 pub/sub 主题订阅者模式
1. 发送者(pub)发送消息，订阅者(sub)接收消息
2. 订阅者可以订阅任意数量的频道<br/>![WeChat0d33275d82b5981f6a7524a9fbc84934.png](https://i.loli.net/2019/07/07/5d21ff7d694c018910.png)
3. 首先在订阅频道，这里实验可以启动多个client，频道不被创建的时候就可以先订阅
	1. 订阅频道：`subscribe myTopic`
	2. 发布消息：publish myTopic "I love you"，这个时候订阅myTopic频道的就可以收到信息了。
4. pub/sub的缺点
	1. 消息的发布是无状态的，无法保证可达到，消息是即发即失的
	2. 如果需要更完善的功能，需要使用RabbitMQ或者Kafka

 
### 六、Redis持久化
1. [redis的持久化之RDB的配置和原理](https://www.cnblogs.com/xiaolovewei/p/9038220.html)
2. [深入Redis的RDB和AOF两种持久化方式以及AOF重写机制的分析](https://blog.csdn.net/Leon_cx/article/details/81545178)

#### 6.1 RDB(快照)持久化

##### 6.1.1 配置
1. 保存某个时间点的全量数据快照
2. 在redis的根目录下，查找redis.conf，一般情况下redis启动的时候，redis会加载redis.conf的信息。
3. 首先在redis.conf中找到save，有关于save的配置信息
	1. save 900 1，表示900秒内有1条写入指令，就产生一次快照
	2. save 300 10，表示300秒内有10条写入指令，就产生一次快照
	3. save 60 10000，表示60秒内有10000条写入指令，就产生一次快照
	4. 这三个都配置上了，表示三个阶段，分别是写入流量大、中、小的时候，对应的备份快照的进度。**主要是为了平衡性能和数据安全**

4. **stop-writes-on-bgsave-error**配置选项，是为了保护数据持久化一致性的问题
	1. 如果配置为yes，表示当备份进程出错的时候，主进程就停止进行写入操作了。这样做是为了保护数据持久化的一致性。
	2. 如果有完善的监控系统，可以将这个配置设置为no

5. **rdbcompression**压缩配置，将rdb文件是否压缩，一般设置为no
	1. 因为redis就是属于CPU密集型服务器，再开启压缩会带来更多的CPU的消耗，

6. 禁用RDB配置，直接在save的后面添加一个`save ""`即可



##### 6.1.2 持久化
1. 打开redis目录下的src文件夹，里面会有rdb文件，dump.rdb文件是一个二进制文件
2. RDB文件可以通过两个文件来生成
	1. save：阻塞redis的服务器进程，知道rdb文件被创建完毕，是在redis的主线程中保存快照的，但是redis就一个主线程，那么redis就阻塞着被
	2. **bgsave**：Fork出一个子进程来创建RDB文件，不阻塞服务器进程，父进程继续处理接收到的命令，子进程结束之后，会发送一个信号给父进程，父进程处理命令的同时，通过轮训来处理子进程的信号
		* 在调用bgsave命令的时候，会直接返回一个OK的返回码
		* lastsave，可以查看上一个save成功的时间戳，和删除rdb文件不删除没有关系，只要执行了save相关命令，就可以使用lastsave查找
3. 可以通过定时器之类的方法，定期的进行定时save操作，然后在通过脚本将dump.rdb文件转存为dump+时间戳.rdb的形式保存，这样就会看到不同时间点的快照了
4. **自动化触发RDB持久化**
	1. 根据redis.conf配置中的save m n定时出发，用的是bgsave
	2. 主从复制时，主节点自动触发，后面有讲
	3. 执行Debug Reload
	4. 执行Shutdown且没有开启AOF持久化
5. bgsave原理
	1. 原理图<br/>![WeChate97bf3f693ee2ec4cb62131f57533d78.png](https://i.loli.net/2019/07/07/5d220fbabb3bf66017.png)
	2. 检查子进程有没有RDB或者AOF相关方法
	3. 系统调用fork()，创建进程，linux下实现了**Copy-on-Write**，写实复制

5. Copy-on-Write
	* 传统方式下，fork()是直接讲父进程的东西全部复制给子进程，这样的效率低，复制的资源有的还可能用不上。
	* 这种方式是，创建子进程时，内核只为子进程创建虚拟空间，父子两进程使用的是相同的物理空间。只有父子进程发生更改时，才会为子进程分配独立的物理空间
	* 如果有多个调用者同时要求相同资源(如内存或磁盘上的数据存储)，它们会共同获取相同的指针窒息那个相同过的资源，知道某个调用者试图修改资源的内容时，系统才会真正复制一份专用的副本给该调用者，而其他的调用者见到的最初的资源仍保持不变。	
	
6. redis的rdb流程
	1. redis调用fork调用子进程
	2. 父进程继续调用client的请求，子进程负责讲内存内容写入到临时文件中
	3. 因为Copy-on-Write，父子进程共享相同的物理空间，当父进程接受写请求命令的时候，os会为父进程创建相应的副本，而不是写共享的页面
	4. 所以子进程的快照时fork命令运行的那一时刻的整个数据库的快照
	5. 当子进程的数据写入到临时文件之后，再用该临时文件替换掉快照文件，子进程退出，进而完成一次备份操作
	6. 如果redis服务再次启动，会直接加载rdb文件
	
##### 6.1.3 缺点
1. 内存数据的全量同步，数据量大会由于I/O而严重影响性能
2. 可能会因为redis挂掉而丢失当前至最近一次快照期间的数据
	
	
#### 6.2 AOF(Append-Only-File)持久化
1. 保存写状态，所有被写入AOF的指令，都是以redis的协议格式来保存的
2. 记录下除了查询以外的所有变更数据库状态的指令
3. 以append的形式追加保存到AOF文件中(增量)
4. 同样是修改redis.conf里面的配置文件
	1. 修改appendonly配置选项修改为yes
	2. 默认生成的文件是appendonly.aof
	3. 配置appendfsync，分别有always、everysec、no三种选项，分别代表变化就写、每秒、交由操作系统决定(但是操作系统一般都是缓存区满再存)
	4. 正常情况下，需要在客户端上设置`config set appendonly yes`，然后就可以看见在src文件中有刚才的默认文件名。同时在server接收的命令行中也有这个

5. 随着写文件的增加，AOF文件大小不断的增大，这就涉及到了重写机制
	1. 比如将一个value值循环增值100次，每次增加1，那么AOF文件会一直记录
		1. AOF文件的压缩主要是去除AOF文件中的无效命令
		2. 同一个key的多次写入只保留最后一个命令
		3. 已删除，已过期的key的命令不再保留
	2. redis在不中断服务的情况下，在后台重建AOF文件，同样调用了Copy-on-Write
		1. 首先先fork，创建出一个子进程
		2. 子进程将AOF写到一个临时文件里，不依赖原来的AOF文件
		3. 主进程持续降新的变动同时写到内存和原来的AOF里
		4. 主进程获取子进程重写AOF的完成信号，往新AOF同步增量变动
		5. 使用新的AOF文件替换掉旧的AOF文件
6. 手动触发，执行**bgrewriteaof**命令直接触发AOF重写
7. 自动触发，在redis.conf配置文件配置
	1. auto-aof-rewrite-min-size 64MB
	2. auto-aof-rewrite-min-percenrage 100


#### 6.3 数据的恢复
1. RDB和AOF文件共存的情况下，恢复的流程<br/>![](https://macdown-picture.oss-cn-beijing.aliyuncs.com/WeChat1561204f33df75d672744f0c8fb0d94c.png?Expires=1562561423&OSSAccessKeyId=TMP.hXfVqAhXaq2oBbhe3N975vSH63KLKhBgHUyJRwJYLyhxvHGngQDv1WWBjXRq1R6VYmqZMFw57sbNntD1cRWhGqExdeVdiL19VJHfY11jgEzzSiyKQcXNi1DtokBTsS.tmp&Signature=ukFExDLpW8BsMLsb4atTM49cahI%3D)


#### 6.4 RDB和AOF优缺点
1. RDB
	1. RDB优点：全量数据快照，文件小，恢复快
	2. RDB缺点：无法保存最近一次快照后的数据
2. AOF
	1. AOF优点：可读性高，适合保存增量数据，数据不易丢失
	2. AOF缺点：文件体积大，恢复时间长
3. RDB-AOF混合持久化方式<br/>![](https://macdown-picture.oss-cn-beijing.aliyuncs.com/RDB-AOF.png?Expires=1562569857&OSSAccessKeyId=TMP.hXfVqAhXaq2oBbhe3N975vSH63KLKhBgHUyJRwJYLyhxvHGngQDv1WWBjXRq1R6VYmqZMFw57sbNntD1cRWhGqExdeVdiL19VJHfY11jgEzzSiyKQcXNi1DtokBTsS.tmp&Signature=Ly5UCR65r5%2FkLm8VvAsW28uzzto%3D)
	* BGSAVE做镜像全量持久化，AOF做增量持久化


### 七、Pipeline及同步
#### 7.1 使用Pipeline
1. Pipeline和Linux的管道类似，就是在后面添加 `--pipe`参数
2. Redis基于请求/相应模型，单个请求处理需要一一应答
3. Pipeline批量执行命令，节省多次IO往返时间
4. **有顺序依赖的指令**建议分批发送

#### 7.2 Redis同步机制
1. 主从同步原理理解<br/>![](https://macdown-picture.oss-cn-beijing.aliyuncs.com/tongbu.png?Expires=1562570415&OSSAccessKeyId=TMP.hXfVqAhXaq2oBbhe3N975vSH63KLKhBgHUyJRwJYLyhxvHGngQDv1WWBjXRq1R6VYmqZMFw57sbNntD1cRWhGqExdeVdiL19VJHfY11jgEzzSiyKQcXNi1DtokBTsS.tmp&Signature=M%2Fcc41Ufa2v9tyQsDsoEuQ6TzJw%3D)
	* 一般都是一个master用于写的操作的，其他的若干个slave用来读操作的，每个master和slave都代表了一个redis的server实例
	* 定期的数据备份操作也是单独选择一个slave取完成的，这样可以最大程度的发挥出redis的性能，为了使让其支持数据的弱一致性，即最终一致性
	* 不需要时时保证master和slave中间的数据是实时同步的，但是过了一段时间后，它们的数据是趋于同步的，这就是所谓的最终一致性，redis可以使用主从同步，也可以使用从从同步
	* 第一次同步主节点做一次bgsave，并同时将后续的修改操作，记录到内存的buffer中，待完成后，将rdb全量同步到从节点中。从节点接受同步后，将rdb的镜像加载到内存中，再通知主节点，将其间修改的操作记录和增量数据同步到从节点，进行写入，这样就完成了整个同步的过程

2. 全同步过程(全量同步流程)
	* Slave发送sync命令到master
	* Master启动一个后台进程，将redis中的数据快照保存到文件中
	* Master将保存数据快照期间收到的写命令缓存起来
	* Master完成写文件操作后，将该文件发送给Slave
	* 使用新的AOF文件替换掉旧的AOF文件
	* Master将这期间的收集的增量写命令发送给Slave端

3. 增量同步过程
	* Master接收到用户的操作指令，判断是否需要传播到slave，一般读就不用了
	* 将操作记录追加到AOF文件
	* 将操作传播到其他的Slave：1. 对齐主从库；2. 往相应缓存写入指令
	* 将缓存中的数据发送给Slave

4. 主从模式的弊端就是不具备高可用性，当Master挂掉之后，redis将不能对外提供写入操作

#### 7.3 Redis Sentinel(Redis 哨兵)
1. 解决主从同步Master宕机后主从切换的问题
	* 监控：检查主从服务器是否运行正常
	* 提醒：通过API向管理员或者其他应用程序发送故障通知
	* 自动故障迁移：主从切换
2. Redis Sentinel是一个分布式的系统，一个架构中可以运行多个sentinel进程，采用流言协议，接收主服务器是否下线的信息，并使用投票协议，来决定是否执行自动故障迁移，以及决定那个从服务器作为新的主服务器。和Zookeeper比较类似

3. **流言协议Gossip**，在杂乱无章中寻求一致
	* 在有界网络中，每个节点都随机的与其他节点通信，经过一番杂乱无章的通信之后，最终所有的节点的状态都会达成一致。
	* 种子节点定期随机向其他节点发送节点列表以及需要传播的消息，任何新加入的节点就很快的被其他节点所知道
	* 不保证信息一定会传递给所有的节点，但是最终会趋于一致


### 八、Redis集群
#### 8.1 如何从海量数据中快速找到所需
1. 数据分片：按照某种规则去划分数据，分散储存在多个节点上
	* 通过数据分片，减轻单个节点服务器的压力
	* RedisCluster采用无中心结构，每个节点保存数据和整个集群的状态，每个节点都和其他节点连接，节点直接采用gossip协议传播信息和发现新的节点
	* 主要目的是将不同的key分散到不同的节点，通常做法就是获取key的哈希值，然后根据节点数求模
2. 常规的按照哈希划分无法实现节点的动态增减

#### 8.2 一致性哈希算法
1. 对2^32取模，将哈希值空间组织成虚拟的圆环
2. 将数据key使用相同的函数Hash计算出哈希值<br/>![](https://macdown-picture.oss-cn-beijing.aliyuncs.com/WeChatb9b7a250a1ff96480246a9461b4f58f3.png?Expires=1562577818&OSSAccessKeyId=TMP.hXfVqAhXaq2oBbhe3N975vSH63KLKhBgHUyJRwJYLyhxvHGngQDv1WWBjXRq1R6VYmqZMFw57sbNntD1cRWhGqExdeVdiL19VJHfY11jgEzzSiyKQcXNi1DtokBTsS.tmp&Signature=Ky8Mucwoebm%2F9Fft7GJCZhbBJLM%3D)
3. 将各个服务器通过哈希进行一个哈希的变换，具体可以选择服务器的ip或者主机名作为关键字进行哈希，这样每台服务器就能确定在哈希环上的位置。
4. 对数据的key使用和刚刚和服务器ip一个相同的函数，去计算出哈希值，并确定此数据在环上的位置
5. 沿着环顺时针行走，第一台遇到的就是数据目标存储的服务器
6. 这样遇到其中的服务器宕机的情况，则受影响的数据仅仅是服务器到其环型空间前一台服务器之间的数据，也就是逆时针行走遇到的第一代服务器之间的数据，其他的数据是不回受到影响的
7. **新增服务器NodeX**，影响的是新增服务器逆时针到上一台服务器之间的数据
8. **Hash环的数据倾斜问题**，在节点很少的时候，容易因为节点分布不均匀，导致数据倾斜，指的是被缓存的对象大部分缓存在同一台服务器上<br/>![](https://macdown-picture.oss-cn-beijing.aliyuncs.com/%E6%95%B0%E6%8D%AE%E5%80%BE%E6%96%9C.png?Expires=1562578635&OSSAccessKeyId=TMP.hXfVqAhXaq2oBbhe3N975vSH63KLKhBgHUyJRwJYLyhxvHGngQDv1WWBjXRq1R6VYmqZMFw57sbNntD1cRWhGqExdeVdiL19VJHfY11jgEzzSiyKQcXNi1DtokBTsS.tmp&Signature=UmonKd44swpXrpHs9jQ8%2FiuP4is%3D)

9. 针对数据倾斜引入了虚拟节点解决数据倾斜的问题。<br/>![](https://macdown-picture.oss-cn-beijing.aliyuncs.com/%E8%99%9A%E6%8B%9F%E8%8A%82%E7%82%B9.png?Expires=1562578743&OSSAccessKeyId=TMP.hXfVqAhXaq2oBbhe3N975vSH63KLKhBgHUyJRwJYLyhxvHGngQDv1WWBjXRq1R6VYmqZMFw57sbNntD1cRWhGqExdeVdiL19VJHfY11jgEzzSiyKQcXNi1DtokBTsS.tmp&Signature=FNX63%2BFB47U11Icusv7f6w4aAdw%3D)
	
10. 多个虚拟节点，即对每个服务器节点计算多个hash，计算结果位置都放置一个子服务器节点，可以在服务器ip或主机名后面增加编号来识别，通常将虚拟节点设置为32或者更大，就可以避免数据倾斜的问题。
11. 使用主从同步和哨兵模式将redis设置为高可用性。
	