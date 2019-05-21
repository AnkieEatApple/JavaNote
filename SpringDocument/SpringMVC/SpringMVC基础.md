## SpringMVC数据绑定入门
1. 数据绑定
	* 简单绑定是江一个用户界面元素(控件)的属性绑定到一个类型(对象)实例上的某个属性的方法。
2. 数据绑定过程
	* IDE简介intellij idea
	* intellij idea创建Spring MVC项目
3. 基本类型、包装类型、数组
4. 简单对象、多层级对象、同属性对象
5. List、Set、Map
6. Json、xml
7. PropertyEditor、Formatter、Converter，目的是创建一个局部的类型转换器还是创建一个全局的类型转换器
8. RESTful的扩展
9. 课程总结
10. 参考视频: [https://www.imooc.com/video/10632](https://www.imooc.com/video/10632)

### 一、IDEA软件简介
1. eclipse里面的workspace相当于idea，project相当于module
2. 创建一个Maven的项目
	1. 勾选`Create from archetype`，在列表中找到提供的maven提供的webapp
	2. 添加GroupId，类似类似于包结构，例如`www.imooc.springmvc`，添加ArtifactId为springmvc
	3. 添加本地下载好的的maven版本，可以参考实例配置中的maven配置
	4. 