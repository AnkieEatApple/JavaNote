## NIO原理与实战.md
1. NIO网络编程模型
2. NIO网络编程详解
3. NIO网络编程实战
4. NIO网络编程缺陷
5. 知识基础及开发环境
	1. Java基础知识
	2. BIO网络编程基础知识
	3. 多线程基础知识
	4. mac操作系统
	5. JDK1.8版本，NIO出自于1.4版本，在1.8版本才稳定
	6. IDEA

#### 一、NIO网络编程模型
1. NIO简介
	* Non-Blocking I/O 或 New I/O
	* 是从特性出发，Non-Blocking表示非阻塞IO，New I/O表示年龄出发，是一款新的IO标准
	* 在JDK1.4版本出现
	* 主要是为了应付高并发网络服务
2. 编程模型
	* 模型：对共性的抽象
	* 编程：对编程共性的抽象
3. BIO网络模型
	* 属于阻塞IO
	* 正常的阻塞IO的连接的逻辑，服务端会建立一个等待客户端再次请求的线程，这样属于正常的网络传输逻辑，但是此时client端不再发送请求到服务端的这个线程，这个线程就会一直在这里等待客户端连接，造成资源浪费。<br/>![D6428F2E-6147-4F4E-A597-E7FA40A2520E.png](https://i.loli.net/2019/05/10/5cd50287ec0be.png)
	* 这种情况就是属于多个client端请求之后，但没有再次发送请求到server端，造成多个Thread处于等待中，造成资源浪费，服务端的性能遇到瓶颈，也可能导致服务端崩溃。<br/>![62B69E9A-254A-467E-8560-5D83498849E8.png](https://i.loli.net/2019/05/10/5cd50287d550e.png)
4. BIO模型缺点
	* 阻塞式IO，服务端会一直阻塞这个线程等待客户端再次发送消息
	* 弹性伸缩能力差，这里是一个线程对应的一个Client，这里的Thread和Client是1:1的对应关系，再好的服务端也接受不了上千万的client的接入。
	* 多线程耗资源，线程越多，服务器CPU的线程调度越频繁，分片到每个线程上的资源就越来越少
5. NIO网络模型--猜想
	* 非阻塞IO，读取一个文件的时候，要么直接返回错误，要么直接返回数据。调用read方法的时候，会直接返回一个结果，而非阻塞在那里不动。
	* 这里是一个client建立的一个socket连接，然后将这个socket放在set集合中，通过read读取这个set，有连接过来read到之后，再交给handler进行处理。<br/>![AF1E6065-0270-4B59-BFD7-306331A5E151.png](https://i.loli.net/2019/05/10/5cd505846a2c7.png)	

6. NIO网络模型
	* 服务端有一个selector来循环检测注册事件情况，一旦被监听到直接调用处理器，
	* 然后启动一个方法来链接事件处理器，而后注册链接可读时事件，同时相应客户端建立连接请求
	* 此时client再次发送请求的时候，在selector中启动连接读写处理器方法，然后处理客户端读写业务，同时注册链接可读事件，相应客户端请求<br/>![7809F357-FD4A-4AE6-86AC-536339BCB1E8.png](https://i.loli.net/2019/05/10/5cd51519a0dfc.png)

7. NIO网络模型改进
	* NIO根据BIO模型改进，属于**非阻塞式I/O模型**，服务器端提供一个单线程的Selector来监听所有连接，并负责关心连接锁关心的事件
	* **弹性伸缩能力加强**，服务端不再是多个线程处理，而是一个线程处理所有请求，服务端和客户端不再是1:1的关系，而是1:M的关系
	* **单线程节省资源**，避免线程频繁创建和销毁，避免线程上下文切换

#### 二、NIO网络编程详解
1. NIO核心为我们提供了三个核心实现类
	1. Channel：通道
	2. Buffer：缓冲区
	3. Selector：选择器 或 多路复用器
	
##### 2.1. Channel简介
1. **具有双向性**，是随机传输的通道，是JDK中对输入输出的另一种抽象，可以类比BIO中流的概念，与流不同的是，流是单向传输，有InputStream/OutputStream，而通道支持双向传输，一个Channel即可读又可写。
2. **非阻塞性**，传统流是阻塞模式，而Channel工作在非阻塞模式下，这个特点构成了NIO网络编程的基础。
3. **操作唯一性**， Channel操作的唯一使用的方式是使用buffer，通过buffer操作Channel实现数据块的读写。

##### 2.2 Channel实现(JDK中对Channel有四个实现类)
1. 文件类：FileChannel，是为文件进行读写的
2. UDP类：DatagramChannel，基于UDP的数据读写
3. TCP类：ServerSocketChannel/SocketChannel，基于TCP数据的读写
4. Socket的BIO连接回顾<br/>![E2034FAA-C400-454D-997C-DE2414286204.png](https://i.loli.net/2019/05/10/5cd51a2057aff.png)
5. 对Channel的使用，对服务端和客户端进行的通信<br/>![E4C66A3F-09AD-4A90-AB9F-AFA2AB00E160.png](https://i.loli.net/2019/05/10/5cd51c3b2842b.png)

##### 2.3 Buffer简介
1. Buffer是NIO中新加入的一个类，这个类提供唯一与Channel交互的方式
2. 作用：**读写Channel中的数据**，通过Buffer可以将从Channel重读取数据，或将数据写入到Channel中，仅此一招别无他法。
3. 本质：**一块内存区域**，Buffer本质上是一块可以写入数据和读取数据的内存，这块内存被NIO包装成了NIO的Buffer对象，并提供了一组方法来操作这块内存。
4. **Buffer属性**，一切对Buffer的操作本质上都是在折腾这四个属性，以ByteBuffer举例，该buffer类似于一个Byte数组。
	* **Capacity: 容量**，表明数组最大可以容纳多少字节，一旦写入的字节数超出最大字节数，就要将其清空后，才能继续向内写入数据
	* **Position: 位置**，当你写数据时，position表示当前的位置，初始的position的位置为0，当一个data写入到buffer后，position会向后移动到下一个可插入数据的buffer单元，position最大可为：capacity - 1，相当于这个数组的下标的最大值。当读取数据时，Buffer会从写模式切换到读模式。这个就是buffer读写转化的一个模式。此时position会被重置为0，当冲buffer处读取数据时，position会向后移动到下一个可读的位置。
	* **Limit: 上限**，在写模式下，表示Buffer的Limit最多可以往Buffer中写多少数据，此模式写 Limit==Capacity，当切换到读模式时，limit表示最多能从Buffer中读取多少数据，此时Limit会被设置成写模式写的position值。
	* **Mark: 标记**，Mark会存储一个position位置，之后可以调用buffer的reset方法，可以恢复到这个position位置，依然可以从这个位置处理数据 

##### 2.3 Buffer使用
1. Buffer的API，初始化长度为10的byte类型的buffer<br/>![920C9290-0275-4ECD-8065-1EFC6B73B3A1.png](https://i.loli.net/2019/05/10/5cd52170b1b60.png)
2. 向byteBuffer中写入三个字节<br/>![EFC648C4-7CE9-4182-ACDC-CF077A182703.png](https://i.loli.net/2019/05/10/5cd521bf3152d.png)
3. 将byteBuffer从写模式切换成读模式<br/>![0DEB2FF6-4669-41FF-946C-B5FC9CA1E8A4.png](https://i.loli.net/2019/05/10/5cd52213532cb.png)
4. 从byteBuffer中读取一个字节<br/>![FF4D152D-3522-4ACF-A181-72B8856CF128.png](https://i.loli.net/2019/05/10/5cd5225039575.png)
5. 调用mark方法记录下当前position的位置<br/>![4D78815D-9553-457F-AADD-81543D862CA1.png](https://i.loli.net/2019/05/10/5cd522bdb0b1a.png)
6. 先调用get方法调用下一个字节<br/>再调用reset方法将position重制到mark位置<br/>![7F2E731C-B76F-4F9E-8AB6-22774B34E4F1.png](https://i.loli.net/2019/05/10/5cd5233619f62.png)
7. 调用clear方法，将所有的属性重制<br/>![8723813E-74C8-420D-845B-7B4667C93D7A.png](https://i.loli.net/2019/05/10/5cd5238a9b94a.png)
8. NIO除了Boolean类型，对其他的所有的类型都有Buffer的类型实现，**但在网络编程中使用最多的还是ByteBuffer类**。

##### 2.4 Selector简介 
1. 作用：I/O就绪选择，选择器，或者叫多路复用器
2. **地位：NIO网络编程的基础** 
3. 总结：Selector是NIO中能够检测1到多个NIO通道，并能知晓通道是否为诸如读写做好准备的组件，通过它，一个线程就可以管理多个Channel，从而管理多个网络连接

##### 2.5 Selector使用
1. selector的使用<br/>![088E1789-7D2B-4C89-9628-E4B85CA60F61.png](https://i.loli.net/2019/05/10/5cd5253a10b22.png)

##### 2.6 SelectionrKey简介 
1. 在Selector中提到了在注册监听中，使用过一个SelectorKey的常量，它提供了一个对四个可监听事件的四个可监听常量值。
2. 四种就绪状态常量
	* 接受就绪
	* 连接就绪
	* 读就绪
	* 写就绪
3. **有价值的属性**，在调用selection时的SelectionKey的方法时，会返回一个SelectionKey的集合，可以根据这个SelectionKey的集合获取到当前的channel，获取selector对象，获取该channel就绪事件集合，和所关心事件集合。

##### 2.7 NIO编程实现步骤
1. 第一步：创建Selector
2. 第二步：创建ServerSocketChannel，并绑定监听端口
3. 第三步：**将Channel设置为非阻塞模式**
4. 第四步：将Channel注册到Selector上，监听连接事件
5. 第五步：循环调用Selector的select方法，检测就绪情况
6. 第六步：调用selectedKeys方法获取就绪channel集合
7. 第七步：判断就绪事件种类，调用业务处理方法
8. 第八步：根据业务需要决定是否再次注册监听事件，重复执行第三步操作

#### 三、NIO网络编程实战
1. 利用NIO编程知识点，实现多人聊天室


#### 四、NIO网络编程缺陷
1. 麻烦：**NIO类库和API繁杂**，需要熟练掌握Selector、SocketBuffer、ServerSocketChannel、SocketChannel、ByteBuffer等核心类，还需要很多知识做铺垫，比如BIO、线程等
2. 心累：**可靠性能力补齐，工作量和难度都非常大**，如果使用原生的I/O进行开发，不得不面临很大工作量和难度
	* 客户端的断链重连、网络闪断、半包读写、失败缓存、网络阻塞、异常码流处理等问题
	* 在可靠性能力补全等方面，投入的时间成本将是非常巨大的
	* 上面的多人聊天室的小程序，若仔细测试，bug还是很多
* 有坑：**Selector空轮询，导致CPU 100%**，
	* 主要是出现在类UNIX系统上，根据API的规定，如果调用Selector的select的方法时，如果没有准备就绪的channel，应该阻塞在select的调用上
	* 但linux上使用的是epoll I/O事件通知工具，操作系统使用这一高性能的技术与网络协议栈异步工作，从而导致就是没有准备就绪的channel，select方法也不会阻塞，最终造成CPU利用率100%的现象
	* 这个声称在JDK1.6的补丁里已经修复了，但实际证明在1.8版本仍然存在，只是概率低了而已，如果程序需要兼容不同的操作系统不同的运行环境，需要小心
	* 服务端中while(true)中的判断readyChannels是否为0，如果为0直接continue掉