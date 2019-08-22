## JVM相关知识点
##### 1. 目录
- [一、Compile Once，Run Anywhere如何实现](#Introduction)
- [二、JVM如何加载.class文件](#JVM)
- [三、什么是反射](#Reflect)
- [四、类从编译到执行的过程](#classloader)
- [五、类加载器的双亲委派机制](#Parental)
- [六、类的加载方式](#loading)
- [七、了解Java的内存模型么？](#memory)
- [八、内存模型常考题解析](#question)


##### 2. 对Java的理解
1. 平台无关性：一次编译，到处运行
2. GC：垃圾回收机制
3. 语言特性：范型、反射、Lambda表达式
4. 面向对象：封装、继承、多态
5. 类库：集合、并发库、网络库、IO、NIO之类的
6. 异常处理

### <span id = "Introduction">一、Compile Once，Run Anywhere如何实现</span>
1. Java是如何实现平台无关性的
	* 编译时，javac编译，编译的是Java的源码，即将源码编译成class的字节码，存入到指定的文件中，如果使用javac需要先配置好JDK的环境变量
	* 运行时，直接使用java运行class字节码文件即可，`java javafile`，运行javafile.class即可

2. javap是Java自带的反编译器，即执行`javap -c javafile`，即可反编译刚才的class文件。
3. 编译器会默认生成一个无餐构造函数
4. 实现流程<br/>![WeChataefc9cc917954082fe50d23544e937de.png](https://i.loli.net/2019/08/05/xl9CSLYUAVjZaMH.png)

#### 1.1 为什么JVM不直接将源码解析成机器码去执行
1. 准备工作：每次执行都需要各种检查，能够保证在检查过后的代码生成的字节码反复执行可以不需要进行校验
2. 兼容性：可以将别的语言解析成字节码，例如：ruby，sclar

### <span id = "JVM">二、JVM如何加载.class文件</span>
1. Java虚拟机，是一种抽象化的计算机，通过在实际的计算机上仿真模拟各种计算机功能来实现的。
	* JVM有自己的硬件架构，如处理器、堆栈、寄存器等，还具有相应的指令系统。
	* JVM屏蔽了与具体操作系统系统平台的相关信息，使得Java程序只需生成在Java虚拟机上的目标代码及字节码，就可以在多种平台上不加修改的直接运行。
	* JVM最值得学习的两点就是：**JVM内存结构模型、GC**

#### 2.1 JVM
2. JVM是一个内存中的虚拟机，也就意味着JVM的存储就是内存，我们所写的常量、变量、方法都在内存中，这决定着我们程序运行的是否健壮是否高效。
3. JVM结构图<br/>![WeChat4e99674f25e5901587a6843b2da8cc24.png](https://i.loli.net/2019/08/05/O7uvL16UbBdNJxc.png)
	* Class Loader: 依据特定的格式，加载class文件到内存
	* Execution Engine: 对命令进行解析
	* Native Interface: 融合不同开发语言的原生库为Java所用，有些库因为执行效率等原因，不重复造轮子的原则，调用现有的接口。比如`class.forName()方法`，返回与给定字符串名称相关联的类或者接口的class对象。
	* Runtime Data Area: JVM内存空间结构模型设计(神作!)。专门为NativeMethodStack开辟了一块空间，用于登记navtive方法
4. JVM主要由 **Class Loader**、**Runtime Data Area**、**Execution Engine**、**Native Inteface**组成。
5. JVM流程，通过CLass Loader将符合条件的class文件加载到内存里，通过Execution Engine里的文件去解析字节码，然后交给操作系统去执行



### <span id="Reflect">三、什么是反射</span>
#### 3.1 谈谈反射
1. Java反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能都调用它的任意方法和属性；这种动态回去信息以及动态调用对象方法的功能成为Java语言的反射机制。
2. 写一个反射的例子，或者相关函数

```
// 新建立一个类，用于反射
package com.example.demo.reflect;
public class People {
    private String name;
    public void sayHello(String str) {
        System.out.println(str + ": " + name);
    }
    private String sayHi(String tag) {
        return "Hi " + tag;
    }
}

// 建立一个main函数
package com.example.demo.reflect;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public class Example {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        vClass clazz = Class.forName("com.example.demo.reflect.People");
        People people = (People) clazz.newInstance();
        System.out.println("Class name is " + clazz.getName());
        // getDeclaredMethod，可以类中所有的方法，但是不能获取类中继承的和一些接口的方法
        Method getHello = clazz.getDeclaredMethod("sayHello", String.class);
        people.sayHi("可以直接调用public方法");
        // 设置为true，可以调用private的方法
        getHello.setAccessible(true);   // 这里是设置能否访问私有变量和方法
        Object str = getHello.invoke(people, "Bob");
        System.out.println("getHello result is " + str);
        // 只能获取这个类上的public方法，但是还能获取继承公用类的方法。
        Method sayHi = clazz.getMethod("sayHi", String.class);
        sayHi.invoke(people, "Welcome");
        // 修改字段
        Field name = clazz.getDeclaredField("name");
        name.setAccessible(true);
        name.set(people, "Ankie");
        sayHi.invoke(people, "Welcome");   
    }
}
```



### <span id="classloader">四、类从编译到执行的过程</span>
1. 反射中之所以能获取到类的属性或者方法，或对其进行调用，必须获取其class对象，而要获取到该类的class对象，必须先要获取到该类的字节码文件对象。
2. 用上面的例子进行举例：
	* 编译器将People.java源文件编译成People.class字节码文件
	* ClassLoader将字节码转换为JVM中的Class<People>对象
	* JVM利用Class<People>对象实例化为People对象
#### 4.1 谈谈ClassLoader
1. ClassLoader在Java中有着非常重要的作用，它主要工作在class装载的加载阶段，其主要作用是从系统外部获得Class二进制数据流。
2. 它是Java核心组件，所有的Class都是由ClassLoader进行加载的，ClassLoader负责通过将Class文件里的二进制数据流装载进系统，然后交给Java虚拟机进行连接、初始化等操作。
3. ClassLoader是一个抽象类，提供一些接口供，用于提供加载
4. loadclass方法，就是用于加载制定用户名下的class文件

#### 4.2 ClassLoader的种类
1. BootStrapClassLoader: C++编写，加载核心库java.*，例如java.lang这个包，通常这些类的核心类的class的签名不能被替换掉，是由JVM内核实现的。
2. ExtClassLoader: Java编写，加载扩展库javax.*，是用户可见的classLoader，可以加载自定义的jar包。可以放在`jre/lib/ext扩展包下`，可以尝试用代码打印下`System.getProperty("java.ext.dirs")`。
3. AppClassLoader: Java编写，加载程序所在目录，可以尝试用代码打印下`System.getProperty("java.class.path")`。打印出来的javabasic路径下就是保存的这个程序build出来的class文件了
4. 自定义ClassLoader: Java编写，定制化加载

#### 4.3 自定义ClassLoader的实现
1. 关键函数：

```
protected Class<?> findClass(String name) throws ClassNotFoundException { 
	throw new ClassNotFoundException(name); 
}

protected final Class<?> defineClass(byte[] b, int off, int len) throws ClassFormatError {
	return defineClass(null, b, off, len, null);
}
```

2. 自定义ClassLoader



### <span id="Parental">五、类加载器的双亲委派机制</span>
1. 不同类的Loader加载类的路径和方式有所不同，为了分工各自负责各自的区块，使得逻辑更加的明确，才有这么多相互共存的ClassLoader，加载类的时候一定会按照自己管理的区域各司其职。
2. 会有这么一个机制，让它们之间相互协作，形成一个整体，这个机制就是双亲委派机制。
3. 双亲委派机制原理图<br/>![WeChat5b4fecffc06b5be7f9e0a38bf0eed5da.png](https://i.loli.net/2019/08/08/uFV4PWhdIqyoZiQ.png)
4. 加载方式：
	1. 自底而上检查类是否已经加载，检查是否已经加载过这个类，没有就向上查找直到最顶层，还是没有就去Xbootclasspath里面去寻找
	2. 自上而下尝试加载类，上面的没有找到，就继续去Djava.ext.dir里找，然后继续向下寻找。
5. loadClass避免不同线程加载同一个类，在里面放了一个同步锁，synchronized，代码里面就是上面的加载的方式向上寻找。没有的话，从上面到下面寻找。
6. 查看代码可以看到`AppClassLoader`和`ExtClassLoader`都是继承自`URLClassLoader`，但是实际上是`ExtClassLoader`是`AppClassLoader`的爸爸，<br/>可以通过`System.out.println(c.getClassLoader.getParent());`<br/>和`System.out.println(c.getClassLoader.getParent().getParent());`<br/>和`System.out.println(c.getClassLoader.getParent().getParent().getParent());`查看
	* 在查看`ExtClassLoader`的父亲的时候，会发现，打印的是null，因为`BootstrapClassLoader`是通过C++实现的，在loadclass类中，也就是挡c == null时，还需要在`BootstrapClassLoader`查找一下的原因。
	* `BootstrapClassLoader`最后会调用一个本地的native的方法，是用C/C++实现的，不相信可以在[ClassLoader.c](http://hg.openjdk.java.net/jdk8u/jdk8u/jdk/file/89c8bfe10659/src/share/native/java/lang/ClassLoader.c) 查看

#### 5.1 为什么要使用双亲委派机制去加载类
1. 避免多份同样字节码的加载，内存是宝贵的，A想调用比如System.out.println方法就是一个静态的方法，然后向上查找，最后在`BootstrapClassLoader`找到System类的相关实现。B一看装载过了，就可以直接调用了，这样内存中就只有一份这个字节码了。


### <span id="loading">六、类的加载方式</span>
1. **隐式加载：new**，隐式加载就不同newInstance方式加载，而new方式加载时可以通过构造函数传入参数
2. **显示加载：loadClass、forName等**，对于显示加载来讲，当我们获取到了class对象了之后，需要调用class对象的newInstance方法来生成对象的实例。

#### 6.1 loadClass和forName的区别
1. 都能在运行的时候知道一个类
2. 都能知道该类的属性和方法，也就是对于任意一个对象，都能调用它的任意方法和属性
3. 类的装载(加载)过程，这里装载表示class的生成过程，加载表示其中一步骤
	* **加载：**通过ClassLoader加载class文件字节码，生成Class对象。将class文件的字节码加载到内存中，并将这些静态数据转化为运行时的数据区中方法区的类型数据，在运行时，数据区堆中生成一个代表这个类的Java.lang.class对象，作为方法区内类数据的访问入口。
	* **连接：**
		* 校验：检查加载的class正确性和安全性
		* 准备：为变量分配储存空间并设置类的变量初始值
		* 解析：JVM将常量池内的符号引用转换为直接引用
	* **初始化：**执行类变量赋值和静态代码块
4. **ClassLoader.loadclass得到的class时还没有连接的**
	* 正常对一个类直接调用获取ClassLoader是没有初始化的，`ClassLodaer cl = Robot.class.getClassLoader();`，中的static代码块没有输出
	* 这种技术一般用在SpringIOC中，资源加载器获取要读入的资源的时候，读取一些Bean的配置文件的时候，如果是以Classpath的方式加载，就需要使用ClassLoader的方式来加载，这样主要是为了遵循Spring的LazyLoading的模式有关，为了加快初始化速度，大量的使用了LazyLoading的方式。ClassLoader不需要直接初始化，可以在有需要的时候初始化。
5. **Class.forName得到的class时已经初始化完成了的**
	* 直接执行这个class的时候可以初始化选定的类，可以试验一下就知道了，最常用的就是加载数据库的驱动，Class.forName("com.mysql.jdbc.Driver");



### <span id="memory">七、了解Java的内存模型么？</span>
>HotSpot: 代指虚拟机<br/>
>对于Java8， HotSpots取消了永久代，取代永久代的就是元空间

1. 内存简介
	* 计算机的所有内存都是运行在内存当中的
	* 图释<br/>![WeChatc8118e2805f9ce1f9b830cd71749c768.png](https://i.loli.net/2019/08/12/fbBFgoUQlitMxqJ.png)  
	* 在程序执行的过程中，需要不断的将内存的逻辑地址和物理地址进行映射，找到相关的指令去执行、
	* Java作为操作系统进程，也面临着和其他进程相同的限制，即受限制与操作系统架构提供的可寻址地址空间。操作系统的架构提供的可寻址地址空间由处理器的位数决定
	* 32位处理器：2^32 的可寻址范围 
	* 64位处理器：2^64 的可寻址范围
2. 地址空间的划分
	* **内核空间**，操作系统的程序和程序运行时的空间，包含连接计算机硬件，调度程序，以及联网和提供虚拟内存等服务的逻辑与C的进程。
	* **用户空间**，Java操作运行时的真正空间<br/>![WeChatd3e32e53877b8d3dbdb738042fcc7888.png](https://i.loli.net/2019/08/12/xFQEPcDtGJeIfgL.png)

3. **JVM内存模型**
	* **堆**：堆是所有线程共享的，主要用来存储对象。分为年轻代、老年代两块区域，通过NewRadio参数设定比例。对于年轻代，一个Eden和两个Suvivor区，使用参数SuvivorRadio来设定大小；
	* **Java虚拟机栈/本地方法栈**：线程私有的，主要存放局部变量表，操作数栈，动态连接和方法出口等；
	* **程序计数器**：同样是线程私有的，记录当前线程的行号指示器，为线程的切换提供保障；
	* **方法区**：线程共享的，主要储存类信息、常量池、静态变量、JIT编译后的代码等数据。方法区理论上来说是堆的逻辑组成部分；
	* **运行时常量池**：是方法区的一部分，用于存放编译期生成的各种字面量和符号引用。

#### 7.1 JVM内存模型-JDK8-(线程私有内存部分)
1. Java运行在虚拟机之上，运行时需要内存空间，虚拟机在运行程序的过程中，会将管理的内存空间划分为不同的内存区域方便管理。
2. 从线程的角度去看，哪些内存时私有的<br/>
	* 线程私有：程序计数器、虚拟机栈、本地方法栈
	* 线程共享：MetaSpace、Java堆
	* ![WeChat47a93c814fd6edcf752edc6bde221154.png](https://i.loli.net/2019/08/12/ZaKyPvI6n8wG39j.png)
3. 程序计数器(Program Counter Register)，私有的
	* 当前线程所执行的字节码行号指示器(逻辑)。**是逻辑计数器，非物理计数器**
	* 改变计数器的值来选去下一条需要执行的字节码指令，包括分支、循环、跳转、异常处理、线程恢复等基础功能
	* 和线程是一对一的关系，即"线程私有"，因为JVM的多线程是线程的轮流切换分配处理器执行时间的方式来实现的
	* 对Java方法计数，如果是Native方法则计数器值为Undefined
	* 不必安心内存泄漏
	* **为了线程切换后都有正确的运行位置，每个线程都有字节独立的计数器。**
4. Java虚拟机栈(Stack)，也是私有的
	* Java方法执行的内存模型
	* 包含多个栈针，栈针用于存储局部变量表、操作栈、动态连接、返回地址，每个虚拟机栈从入栈到出栈的过程
	* 一般是当方法调用结束时，栈针才会被销毁，虚拟机栈包含了单个线程每个方法执行的栈针，栈针则存取了局部变量表，操作 数栈、动态连接和方法出口等信息。
5. 本地方法栈，线程私有
	* 与虚拟机相似，主要用于标注了native的方法，例如之前的forName下的方法源码中的forName0的native方法。
6. 局部变量表和操作数栈，线程私有
	* 局部变量表：包含方法执行过程中的所有变量。
	* 操作数栈：入栈、出栈、复制、交换、产生消费变量。
	* 对于栈的解析可以查看下面的代码和执行逻辑，每个小块代表一个栈针<br/>![WeChatd4e84df64a2d9771352fcf1874c3ae3b.png](https://i.loli.net/2019/08/12/XBgEGwrUcnRYNZQ.png)

```
// 代码
public class ByteCodeSimple {
	public static int add(int a, int b) {
		int c = 0;
		c = a + b;
		return c;
	}
}
// 通过javac编译
javac ByteCodeSimple.java
// 通过口语的形式进行反编译，可以看到add的方法，里面有stack=2，也就时操作数栈
javap -verbose ByteCodeSimple.class
```
	
 

#### 7.2 递归为什么会引发java.lang.StackOverflowError异常
1. 实现一个最常见的斐波那契数列递归算法，计算普通的可以实现，但是当计算量过大的时候，`fibonacci(100000000);`的时候，就会出现**StackOverFlowError**
2. 当线程执行一个方法的时候，就会对应的创建一个对应的栈针，并将对应的建立 栈针压入虚拟机栈中，当方法执行完毕的时候，便会将栈针出栈。
3. 因此可知线程当前方法多对应的栈针必定位于Java栈的顶部，而我们的递归函数不断的调用自身
	1. 每新调用一个方法就会生成一个栈针
	2. 它会保存当前方法的栈针状态，将它放到虚拟机栈中
	3. 栈针上下文切换的时候会切换到最新的方法栈针当中
	4. 而每个虚拟机栈的深度是固定的，栈的帧数超出虚拟机栈的深度，就会爆出**StackOverFlowError**
	5. **解决办法**：限制递归的次数、尽量使用循环
4. 虚拟机栈过多会引发java.lang.OutOfMemoryError异常
	1. 当虚拟机栈可以动态扩展时，如果无法申请足够多的内存，就会出现这个错误
	2. 程序如下，但是windows尽量不要这么做，**容易死机**

```
public void stackLeakByThread() {
    while (true) {
        new Thread() {
            public void run() {
                while (true) {
                }
            }
        }.start();
    }
}
```


#### 7.3 **元空间(MetaSpace)与永久代(PermGen)的区别**-(线程共享内存部分)
1. 元空间和永久代都是储存class的相关信息，包括class对象的method等，实际上元空间和永久代均是方法区的实现，只是实现有所不同，方法区只是JVM的一种规范
2. 在Java7，原先位于方法区的字符串常量池，已经被移到Java堆中，并且在Java中使用了元空间替代了永久代。
3. **元空间与永久代的区别**
	* **元空间使用本地内存，而永久代使用的是JVM的内存**，最常见的错误(java.lang.OutOfMemoryError: PermGen space)

4. **MetaSpace相比PermGen的优势**
	* 字符串常量池存在永久代中，容易出现性能问题和内存溢出
	* 类和方法的信息大小难以确定，给永久代的大小指定带来困难
	* 永久代会为GC带来不必要的复杂度，并且回收效率偏低
	* 方便HotSpot与其他JVM如Jrockit的集成

#### 7.4 Java堆(Heap)
1. 介绍
	* 对象实力的分配区域
	* 所有线程共享的一块内存区域
	* 所有的对象实例的分配区域<br/>![WeChatea9c87d298faa426d4d9eeb879b3e1ac.png](https://i.loli.net/2019/08/16/uKXzxR1ZgCVLl8c.png)
	* GC管理的主要区域<br/>![WeChatdb50a9ebcb626ce2869c7b59ab40fb1a.png](https://i.loli.net/2019/08/16/jh1SubfzJTkBm6C.png)

#### 7.5 参考文献
1. [Java方法区、永久代、元空间、常量池详解](https://blog.csdn.net/u011635492/article/details/81046174)
2. [JAVA8内存模型](https://www.jianshu.com/p/258fd5b6734a)


### 八、<span id="question">内存模型常考题解析</span>

#### 8.1 JVM 三大性能调优参数-Xms -Xmx -Xss的含义？
1. 常用的指令如：`java -Xms128m -Xmx128m -Xss256k -jar xxxx.jar`
	* **-Xss：规定了每个线程虚拟机栈(堆栈)的大小**，会影响并发线程数的大小
	* **-Xms：堆的初始值**，刚创建出来的时候专属Java堆的大小，一旦对象容量超出Java堆的初始容量，堆会自动扩容，扩容到-Xmx的大小
	* **-Xmx：堆能达到的最大值**

2. **一般都将-Xms和-Xmx设置成一样的，因为扩容时会发生内存抖动，影响程序运行时的稳定性**

#### 8.2 Java内存模型中堆和栈的区别--内存分配策略
1. 内存分配策略
	1. **静态存储**：编译时确定每个数据目标在运行时的存储空间需求，在编译时就可以分配固定的内存空间
	2. **栈式存储**：数据区需求在编译时未知，运行时模块入口前确定
	3. **堆式存储**：编译时或运行时模块入口都无法确定，动态分配

2. Java内存模型中堆和栈的**联系**
	* 联系：引用对象、数组时，栈里定义变量保存堆中目标的首地址<br/>![WeChat9bae803d84350df143aaf6f1392b2d1d.png](https://i.loli.net/2019/08/16/Cyu7dA28BqQMXkG.png)

3. 堆和栈的区别
	* 管理方式：栈自动释放，堆需要GC
	* 空间大小：栈比堆小
	* 碎片相关：栈产生的碎片远小于堆
	* 分配方式：栈支持静态和动态分配，堆仅支持动态分配
	* 效率：栈的效率比堆高


#### 8.3 元空间、堆、线程独占部分之间的联系--内存角度
1. 程序示例<br/>![WeChat546411d33c4f0e8bea3624843f379647.png](https://i.loli.net/2019/08/16/x1TZmqcvdypKSFO.png)
2. 元空间：
	* Class: HelloWorld - Method: sayHello\setName\main - Field: name
	* Class: System类
3. Java堆：
	* Object：String("test")对象实例
	* Object：HelloWorld
4. 线程独占
	* Parameter reference: "test" to String object
	* Variable reference: "hw" to HelloWorld object
	* Local Variables: a with 1, lineNo(行号)

#### 8.4 不同JDK版本之间的intern()方法的区别--JDK6 vs JDK6+(7、8、9)
1. intern()方法的使用
```
String s = new String("a");
s.intern();
```
2. JDK6: 当调用intern方法时，如果字符串常量池先前已创建出该字符串对象，则返回池中的该字符串的引用。否则，将此字符串对象添加到字符串常量池中，并且返回该字符串的对象引用。(字符串常量池存在于永久代中)
3. JDK6+: 当调用intern方法时，如果字符串常量池先前将已创建出该字符串对象，则返回池中的该字符串的引用。否则，如果该字符串对象已经存在于Java堆中，则将堆中对此对象的引用添加到字符串常量池中，并且返回该引用；如果堆中不存在，则在池中创建该字符串并返回其引用。(这个常量池就不受永久代的约束了)

4. 例子解释 - JDK6 - false、false<br/>![WeChatad410395ed68edb471fc4e31d13d6244.png](https://i.loli.net/2019/08/16/fFcA9nNDU7YmrxW.png)

5. 例子解释 - JDK7 - false、true<br/>![WeChat2f00bc1099421bcae90e26e9e54e30b4.png](https://i.loli.net/2019/08/16/cio1IBjSD8OhQWx.png)








