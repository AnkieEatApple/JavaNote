## Redisson框架及项目集成.md
1. Redisson介绍
2. Redisson官方网站
3. Redisson框架集成

#### 一、Redisson介绍
1. Redisson是架设在Redis基础上一个Java驻内存数据网格(In-Memory Data Grid)
2. Redisson在基于NIO的Netty框架上，充分利用了Redis键值数据库提供的一系列优势
3. 在Java实用工具包常用接口的基础上，为使用者提供了一系列具有分布式特性的常用工具类
4. 使得原本作为协调单机多线程并发程序的工具包获得了协调分布式多机多线程并发系统的能力，大大降低了设计和研发大规模分布式系统的难度
5. 同时结合各富特色的分布式服务，更进一步简化了分布式环境中程序相互之间的协作
6. Redisson是一个框架，集成了很多例如redis分布式锁等

#### 二、Redisson官方网站
1. Redisson 官网 [https://redisson.org/](https://redisson.org/)
2. Redisson Github [https://github.com/redisson/](https://github.com/redisson/)
3. Redisson Wiki 中文 [https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95](https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95)

#### 三、Redisson框架集成
1. 添加依赖

```
<dependency>
  <groupId>org.redisson</groupId>
  <artifactId>redisson</artifactId>
  <version>2.9.0</version>
  <type>pom</type>
</dependency>

// 这个是另一个依赖
<dependency>
  <groupId>com.fasterxml.jackson.dataformat</groupId>
  <artifactId>jackson-dataformat-avro</artifactId>
  <version>2.9.0</version>
  <type>bundle</type>
</dependency>
```


