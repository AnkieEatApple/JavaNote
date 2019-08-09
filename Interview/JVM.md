## JVM相关知识点
##### 1. 目录
- [一、Compile Once，Run Anywhere如何实现](#Introduction)
- [二、JVM如何加载.class文件](#JVM)
- [三、什么是反射](#Reflect)
- [四、类从编译到执行的过程](#classloader)
- [五、类加载器的双亲委派机制](#Parental)
- [六、类的加载方式](#loading)


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
1. **隐式加载：new**
2. **显示加载：loadClass、forName等** 



