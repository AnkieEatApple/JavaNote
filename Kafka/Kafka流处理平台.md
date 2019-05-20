## 1
1. Kafka概念解析
2. Kafka结构设计
3. Kafka场景与应用
4. Kafka高级特性

### 一、 Kafka概念解析
1. 起源于LinkedIn开源
	* 分布式数据同步系统Databus
	* 高性能计算引擎Cubert
	* Java异步处理框架ParSeq
	* Kafka流处理平台
2. Kafka历史
	* LinkIn开发
	* 2011年初开源，加入Apache基金会
	* 2012年从Apache Incubator毕业
	* Apache顶级开源项目
3. Kafka到底是什么
	* streaming platform has three key capablities:
		* Publish and subscribe to streams of records, similar to a message queue or enterprise messaging system，消息队列
		* Store streams of records in a fault-tolerant durable way，数据流存储的平台
		* Process streams of records as they occur，在数据产生的时候就对数据进行处理。
	* Kafka is generally used for two broad classed of addlications:
		* Buliding rea-time streaming data pipelines that reliably get data between systems or applications，构建知识数据流管道，当系统之间有比较强的数据依赖关系的时候，可以使用Kafka构建这样的一个数据管道，让数据可以流起来
		* Building read-time streaming applications that transfrom or react to the streams of data，构建一个实施的数据处理应用，它能够转换或相应这个数据流
* Kafka的实施场景
	* Building real-time streaming data pipelines that reliably get data between systems or applications，实时流的数据管道
	* Building read-time streaming applications that transfrom or recat to the strams of data，实时流的处理
5. 总结：
	* Kafka是一个面向于数据流的，生产、转换、储存、消费的整体的一个流处理平台。
	* Kafka是一个消息队列，但也不仅仅是一个消息队列。

### 二、 Kafka结构设计
#### 2.1 Kafka的基本概念
1. **物理概念**，有服务器和硬件载体的，可能在硬件方面有隔离性，这种就属于一个物理概念。
2. **逻辑概念**，仅存在逻辑上的一个概念，没有物理层面的隔离，可能只是一段代码，或者是一段策略逻辑，这种概念串联起了一个系统或者一个平台。
3. **Producer**：消息和数据的生产者，向Kafka的一个topic发布消息的进程/代码/服务
4. **Consumer**：消息的数据的消费者，订阅数据(Topic)并且处理其发布的消息的进程/代码/服务
5. **Consumer Group**：逻辑概念，对于同一个Topic，会广播给不同的group，一个group中，**只有一个Consumer可以消费该消息**。
6. **Broker**：物理概念，Kafka集群中的每个Kafka节点
7. **Topic**：逻辑概念，Kafka消息的类别，对数据进行区分、隔离
8. **Partition**：物理概念，Kafka下数据存储的基本单元。一个Topic数据，会被分散储存到多个Partiton，每一个Partition是**有序的**，**Partiton是Topic的一个细分**
9. **Replication**：同一个Partiton可能会有多个Replica(副本)，多个Replica之间数据是一样的
10. **Replication Leader**：一个Partiton的多个Replica上，需要一个Leader负责该Partiton上与Producer和Consumer交互，也就是说正房只有一个，其他的都是备胎，正房就是Replication Leader
11. **ReplicaManager**：负责管理当前的Broker所有分区和副本的信息、处理KafkaController发起的一些请求，副本状态的切换、添加/读取消息等，也就是Replication Leader挂掉了，如何重新选取新的Replication Leader

#### 2.2 Kafka的基本概念延伸
1. **Pratition**
	* 每一个Topic被切分为多个Partitions
	* 消费者的数目少于或等于Partition的数目，也就是每一个消费者会消费一个Partition，如果Partition的数目小于消费者的数目，就会出现多个消费者消费同一个partition的情况错误
	* Broker Group中的每一个Broker保存Topic的一个或多个Partitons，Broker是Kafka的一个存储节点，那就意味着多个Broker组成一个Broker Group，他们中的Broker会保存Topic的一个或者多个Partitions，也就是**同一个Partition不会被多个Broker同时保存**
	* Consumer Group中的仅有一个Consumer读取的Topic的一个或者多个Partitions，并且是唯一的Consumer
		* 一是为了容错，以Group的方式去消费这个Topic，在Group内部有一定的容错机制
		* 二是为了提高一定的性能
2. **Replication**
	* 当集群中有Broker挂掉的情况，系统可以主动地使Replicas提供服务。
	* 系统默认设置每一个Topic的replication系数为1，可以在创建Topic时单独设置。这个系数代表有多少个副本，多少个副本代表需要占用多少的资源，节约资源一般情况下为1

3. **Replication特点**
	* Replication的基本单位是Topic的Partition
	* 所有的读和写都是从Leader进，Followers只是做备份
	* Follower必须能够及时复制Leader的数据
	* 增加容错性与可扩展性
	
#### 2.3 Kafka的基本结构
1. 展示了Kafka整体的功能性的一个结构<br/>![WeChatfde19685783fd20d678b5b51a04dea69.png](https://i.loli.net/2019/05/17/5cde65411b25652594.png)
2. 展示可Kafka作为消息队列模式的一个运作<br/>![WeChat2f39467b40e461c1d3c9c37f59f99c00.png](https://i.loli.net/2019/05/17/5cde659b71ac825880.png)
3. Kafka是强依赖于ZooKeeper的，那有什么东西存储在了Zookeeper上了呢
	1. Broker的信息都存在了Zookeeper上
	2. Topic的分布都存在了Zookeeper上
4. Hadoop Cluster，面向Hadoop的大数据的处理
5. Real-time monitoring，实时监控
6. Data warehouse，数据仓库

#### 2.4 Kafka消息结构
1. Kafka的消息结构<br/>![WeChatc1094c2e1e1e05da356e9c8d15b042ba.png](https://i.loli.net/2019/05/17/5cde67cf801f243149.png)
2. Offset: 当前字段所处于的偏移是多少
3. Length: 整个消息有多长
4. CRC32: 是一个校验字段
5. Magic: 设置这个字段，是一个固定的数字，可以通过判断这个消息是不是需要的消息，若不一致，那就可以直接扔掉
6. attributes: 当前消息的属性
7. 后面的key和value没有长度限制

#### 2.5 Kafka特点
1. 分布式的特点
	* 多分区
	* 多副本
	* 多订阅者
	* 基于ZooKeeper的调度
2. 高性能
	* 高吞吐量，可以达到每秒几十万的吞吐量
	* 低延迟
	* 高并发
	* 时间复杂度为O(1)
3. 持久性与扩展性
	* 数据可持久化
	* 容错性
	* 支持在线水平扩展
	* 消息自动平衡
		* 避免消息在服务端过于集中于某几台机器，或者过于集中在某几台服务器上，从而导致数据频繁的访问这几台服务器，所产生热点问题。
		* 一方面将消息服务端的一个平衡，在消费者订阅的时候实现连接的一个平衡。

### 三、 Kafka场景与应用
1. 消息队列
	1. 稳定性、高吞吐性，同时它的消息可以被重复消费
	2. 因为Kafka还保持了一个数据持久型的保证，有更低的一个延迟
2. 行为跟踪
	1. 基于发布订阅模式的一个扩展应用，但我们需要跟踪用户浏览行为时候，可以将所有的浏览记录，以发布订阅的方式实时记录到topic里，这些被订阅者拿到后，就可以做进一步处理，做实时监控，以及离线场景所需要的数据积累
3. 元数据监控
4. 日志收集，一般来说都是将日志收集到一个单一的服务器上进行处理
5. 流处理
6. 事件源，是一种将状态转移，作为按事件排列的这样的一个记录序列，可以根据这个序列来回溯事件状态的一个变更过程
7. 持久性日志(commit log)，它将在节点处，在系统之外进行一个持久性日志的记录，这些日志可以在节点间备份数据，并且为故障节点间的数据恢复，提供一种同步的机制

#### 3.1 Kafka简单案例
1. 环境启动：
2. 简单的生产者与消费者
3. 下载与安装
	* Zookeeper下载：[http://zookeeper.apache.org/release.html](http://zookeeper.apache.org/release.html)
	* Kafka下载：[http://kafka.apache.org/downloads](http://kafka.apache.org/downloads)
	* 安装：解压，配置环境变量
	* Mac便捷安装: `brew install kafka`，直接就打包连zookeeper就都包含了
	* mac下brew安装zookeeper：[https://blog.csdn.net/qi49125/article/details/60779877](https://blog.csdn.net/qi49125/article/details/60779877)
	* mac下brew安装kafka(很不错): [https://www.jianshu.com/p/cddd25da8061](https://www.jianshu.com/p/cddd25da8061)
4. kafka安装之后的路径分别为	
	* /usr/local/Cellar/zookeeper
	* /usr/local/Cellar/kafka
5. 配置文件的位置
	* /usr/local/etc/kafka/server.properties
	* /usr/local/etc/kafka/zookeeper.properties
6. 启动命令及顺序，后面的&猜测和redis启动类似，避免阻塞在命令行
	* zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties &
	* kafka-server-start /usr/local/etc/kafka/server.properties &

7. 创建Topic
	* kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
	* --topic test: 表示这个topic的名称是test

8. 查看创建的topic
	* kafka-topics --list --zookeeper localhost:2181

9. 生产(发送)一些消息
	* kafka-console-producer --broker-list localhost:9092 --topic test 
	* 这样会启动简单的生产者

10. 消费消息
	* kafka-console-consumer --bootstrap-server localhost:9092 --topic test --from-beginning
	* --from-begining: 代表会从头开始消费，不添加不会从头开始消费

#### 3.2 Kafka代码案例
1. 参考课程: [https://www.imooc.com/video/17865](https://www.imooc.com/video/17865)
2. 代码地址为: [https://github.com/AnAngryMan/KafkaSimpleExample](https://github.com/AnAngryMan/KafkaSimpleExample)
3. 需要学历了解springboot的相关知识点




### 四、 Kafka高级特性
#### 4.1 消息事物
1. 为什么要支持事物？
	* 满足“读取-处理-写入”模式
	* 流处理需求的不断增强
	* 不准确的数据处理的容忍度

2. 数据传输的事务定义
	* 最多一次：消息不会被重复发送，最多被传输一次，但也有可能一次不传输
	* 最少一次：消息不会被漏发送，最少被传输一次，但也有可能被重复传输
	* 精确的一次(Exactly once): 不会漏传输也不会重复传输，每个消息都传输被一次且仅仅被传输一次，这是大家所期望的

3. 事物保证
	* 内部重试问题：Procedure幂等处理
	* 多分区原子写入
		1. 事物要保证kafka的Topic下每一个分区下的原子写入，那就意味着事物中的所有消息都讲被成功写入，或者被丢弃，比如在处理过程中出现了异常，那么事物终止了，消息就要被抛弃掉，事物中的消息不能被consumer读取到
		2. 如何实现呢？首先这里的原子是一个读取写入的一个过程，意味着某一个应用程序，比如Topic在Topic1，在topic1的偏移量x处读到一个消息，把这个消息称为A，然后对这个消息做了处理之后，将这个消息命名为B，并且写入到topic2，这样的整个的一个过程，只有当A和B被认为成功消费，并且被一起发布的时候，或者它们两个完全不发布的时候，这个读取写入操作是原子的
		3. 如何判定一个消息被成功消费了呢？A是从topic1的偏移量x这里读取的，当读取的时候这个偏移量会被标记成已消费，但这个偏移量在什么时候判定为已消费呢？就是Kafka会将这个偏移量写入一个叫做OffsetTopic的内部Kafka的topic来记录offset的commit，当这个偏移量提交给offset topic的时候，才证明是成功消费
		4. 由于消息仅在提供偏移量时被视为成功消费，所以跨多个主题和分区的原子写入(这里需要思考🤔)
	* 避免僵尸实例
		* 提供了避免僵尸实例的机制，来规避僵尸实例带来的事务行破坏。
		* 每个事物Producer分配一个transactional.id，在进程重新启动时能够识别相同的Producer实例，这个id就可以起到，可以区分不同的Producer实例，避免针对一个Producer分配不同的实例。
		* Kafka增加了一个与transaction.id相关的epoch，储存每个transaction.id内部元数据。这个epoch就是寿命或者是迭代，他会记录每次元数据的变更。
		* 一旦epoch被处罚，任何具有相同的transaction.id和更旧的epoccer的Producer被视为僵尸，Kafka会拒绝来自这些Producer的后续事物性写入

#### 4.2 零拷贝
1. 落落传输吃就行日志块，平时生产和消费的消息就是这些日志块，这些日志块平时是很消耗性能的
2. 使用的是Java Nio channel.transforTo()方法，就可以实现零拷贝数据传输的一个过程
3. 在底层其实是调用的Linux的sendfile的系统API，通过这样的一个系统调用，将系统的拷贝性能得到一个极大的提升

4. 文件传输到网络的公共数据路径，经历了四次拷贝
	* 操作系统数据将数据从磁盘读入到内核空间的页缓存<br/>页缓存是相当于磁盘上的第一层缓存，只有将数据存放到这层缓存上，系统才能快速的将这些数据拷贝到其他地方。
	* 应用程序将数据从内核空间读入到用户空间缓存中<br/>因为对于Linux中，内核空间是应用程序无法直接操作的。应用程序能操作用户所在的当前空间，应用程序需要将数据从内核空间读取到用户空间缓存里面
	* 应用程序将数据写回到内核空间到socket缓存中
	* 操作系统将数据从socket缓冲区复制到网卡缓冲区，以便将数据经网络发出。

5. 零拷贝过程，不是指所有的拷贝的次数为零，而是指内核空间和用户空间的交互拷贝次数为零
	* 操作系统将数据从磁盘读入到内核空间的页缓存
	* 将数据的位置和长度的信息的描述符增加至内核空间(socket缓冲区)
	* 操作系统将数据从内核拷贝到网卡缓冲区，以便将数据经网络发出

6. 文件传输到网络的公共数据路径演变<br/>![WeChat31dd6a1ac0e38f8decea7342382d7b2d.png](https://i.loli.net/2019/05/20/5ce23f6467f3743271.png)

### 五、总结
1. 总结图示<br/>![WeChatab4f75ba25ec3edb8c872545f9efa5c0.png](https://i.loli.net/2019/05/20/5ce240445f38928909.png)