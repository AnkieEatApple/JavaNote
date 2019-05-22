## 1

### 一、Mybatis Generator
#### 1.1 简介
1. 官网，[http://www.mybatis.org/generator/](http://www.mybatis.org/generator/)
2. 解释
	1. 会自动生成pojo、dao、和对应的xml文件
	2. pojo时和db互为为对应的数据模型
	3. dao是一个接口供Service调用，service中一般都是定义接口，然后在service中定义了一个Impl去实现接口去调用dao的接口
	4. xml是dao层接口的一个实现，sql语句都写在xml里面，生成的包含大部分CRUD，但复杂的还需要自己去实现

#### 1.2 实现
1. 现阶段掌握到的实现方式，是先在系统内执行SQL语句，在数据库中先建数据库、表，建立表后，数据库使用Navicat确认后，在使用插件生成pojo、dao、xml文件
2. 配置pom.xml，需要添加对应的sql驱动和generator的插件
	1. **添加驱动**，本文用的是mysql，用的是springboot实现的，所以在init的时候需要将mysql的驱动加上
	2. **添加插件**，在pom.xml中的<build>标签中，在<plugins>的标签中，配置<plugin>含有org.mybatis.generator的标签，添加之后更新一下，会在IDE右侧的Maven的菜单的Plugins中找到generator的插件

	```
	<plugin>
		<groupId>org.mybatis.generator</groupId>
		<artifactId>mybatis-generator-maven-plugin</artifactId>
		<version>1.3.2</version>
		<configuration>
			<verbose>true</verbose>
			<overwrite>true</overwrite>
		</configuration>
	</plugin>
	```

3. 需要创建一个generatorConfig.xml文件，并配合创建一个database.properties文件存放数据库的相关键值对
	1. 这里没有做环境隔离，对数据库的密码没有处理加密
	2. 当前的database.properties文件和generatorConfig.xml在统一文件夹下，没能解决在环境隔离的时候，使用插件访问database.properties文件，database中的文件自己配置
	3. 这个文件的前面有一个头，这个具体是怎么自动生成的没有找到，只有下一个工程使用的时候，拷贝这个模版了

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>

    <!-- 倒入配置特性-->
    <properties resource="datasource.properties"></properties>

    <!-- 指定特定数据库的jdbc驱动jar包的位置-->
    <classPathEntry location="${db.driverLocation}"/>

    <context id="default" targetRuntime="MyBatis3">

        <!-- optional, 目的是在创建class时，对注释进行控制，也就是没有乱七八糟的注解了-->
        <commentGenerator>
            <property name="suppressDate" value="true"/>
            <property name="suppressAllComments" value="true"/>
        </commentGenerator>

        <!-- jdbc的数据库连接，使用插件的时候开始成功与否就看这个了-->
        <jdbcConnection
                driverClass="${db.driverClassName}"
                connectionURL="${db.url}"
                userId="${db.username}"
                password="${db.password}">
        </jdbcConnection>

        <!-- Model模型生成器，用来生成含有主键的key类，记录类，以及查询Example类
            targetPackage     指定生成的model生成所在的包名
            targetProject     指定在该项目下所在的路径
        -->
        <javaModelGenerator targetPackage="com.example.mybatisdemo.pojo" targetProject="./src/main/java">
            <!-- 是否允许子包，即targetPackage.schemaName.tableName-->
            <property name="enableSubPackages" value="false"/>
            <!-- 是否对model添加构造函数-->
            <property name="constructorBased" value="true"/>
            <!-- 是否对类CHAR类型的列的数据进行trim操作-->
            <property name="trimStrings" value="true"/>
            <!-- 建立的Model对象是否不可该改变，即生成的Model对象不回有setter，只有构造方法，这种方法可以使用lombok注解实现-->
            <property name="immutable" value="false"/>
        </javaModelGenerator>

        <!-- mapper映射文件生成所在的目录，为每一个数据库的表生成对应的SQlMap文件 -->
        <sqlMapGenerator targetPackage="mappers" targetProject="./src/main/resources">
            <property name="enbaleSubPackages" value="false"/>
        </sqlMapGenerator>

        <!-- mapper接口dao生成的位置-->
        <javaClientGenerator type="XMLMAPPER" targetPackage="com.example.mybatisdemo.dao" targetProject="./src/main/java">
            <!-- enableSubPackages：是否让schema作为包的后嘴-->
            <property name="enableSubPackages" value="false"/>
        </javaClientGenerator>


        <!-- 要生成的对应的表格-->
        <table tableName="t_coffee" domainObjectName="Coffee" enableCountByExample="false" enableUpdateByExample="false" enableDeleteByExample="false" enableSelectByExample="false" selectByExampleQueryId="false"></table>
   </context>
</generatorConfiguration>	
```

#### 1.3 注意事项
1. 使用spring建立的功能，也需要在pom中添加mysql的引用
2. 若使用环境隔离的话，需要对对应的properties文件的键值对做好区分，分清线上和线下和测试环境
3. 插件不成功先看看log，可能是驱动名或某些单词拼错了
4. Mapper一般都是在resources中，**插件生成mapper后，表中的插入事件和更新时间在insert需要改为now()，在update需要将更新时间改为now()!**，这样就不用在前面操作这两个时间的参数了！