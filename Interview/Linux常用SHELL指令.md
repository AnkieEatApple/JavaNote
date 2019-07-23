## Linux常用SHELL指令
1. 体系结构主要分为用户态(用户上层活动)和内核态
2. 内核：本质是管理计算机硬件设备的程序。
	* 程序直接管理硬件，包括CPU、内存空间、硬盘接口、网络接口等等，所有的内核操作都要通过内核传递给硬件，
	* 上层软件必须依托内核中的CPU资源、内存资源、I/O资源等
3. 系统调用：内核的访问接口，是一种能再简化的操作
	* 可以看作一次原子操作
	* `uname -a`，查看系统内核版本
	* `man 2 syscalls`，查看所有的系统调用接口
	* `man 2 对应的系统调用接口`，查看帮助
4. 公用函数库：系统调用组合拳
5. Shell：命令解释器，可编程
	* `ls -lrt`，列出当前目录详情
	* `echo $SHELL`，或`cat /etc/shells`查看shell的版本，可以使用`chsh -s SHELL路径`

### 一、查找特定文件
1. find
	* 语法：find path [options] params
	* 作用：在指定目录下查找文件，例子为查找target.java，在home目录下递归寻找，使用的命令为：`find -name "target.java"`
	* 在home目录下查找所有tar开头的文件：`find ~ -name target*`，但是是区分大小写的，参数变为 ～iname就不用考虑大小写的问题了。

### 二、检索文件内容
1. grep
	* 语法：grep [options] pattern file
	* 全称：Global Regular Expression Print
	* 作用：查找文件里符合条件的字符串
	* `grep "Hell" target*`，查找文件里包含有Hell的行都被检索出来了，grep只会筛选出所在字符的行
	
2. 管道操作符 |
	* 可讲指令连接起来，前一个指令的输出作为后一个指令的输入<br/>![管道.png](https://i.loli.net/2019/07/08/5d2345f2c6fde91775.png)
	* 上面的查找find命令可以换成，`find ~ | grep "target.*"`	

3. 使用管道注意的要点
	* 只处理前一个命令正确输出，不处理错误输出
	* 右边命令必须能够接收标准输入流，否则传递过程中数据会被抛弃
	* sed、awk、grep、cut、head、top、less、more、wc、join、sort、split等

4. 在日志中查找存有错误的句式：`grep 'partial\[true\]' bsc-palt-data.info.log | grep -o 'engine\[[0-9a-z]*\]'`，这样会将前面的输出流作为后面的输入流作为进一步筛选，执行之后，只会显示engine[xxxx]相关的元素
5. `ps -ef | grep tomcat`，会产生一条本指令查询的线程，想忽略这个线程的话，需要在后面添加`ps -ef | grep tomcat | grep -v "grep"`即可实现
6. 常用的有
	* `grep 'partial\[true\]' bsc-palt-data.info.log`
	* `grep -o 'engine\[[0-9a-z]*\]'`
	* `grep -v "grep"`，过滤掉相关字符串的内容，可与上面的组合使用

### 三、对文件内容做统计
1. 想统计并检索出某变量为true的出现的次数
2. 当我们发现某个检索引擎的关键变量的值为true的时候，并且超过一定次数的时候，则说明改引擎需要从集群中摘掉，并且进行紧急修复。

##### 3.1 awk指令
1. awk [options] 'cmd' file
2. 一次读区一行文本，按输入分割符进行分片，切成多个组成部分。
3. 将切片直接保存在内建的变量中，\$1, \$2...(\$0表示行的全部)
4. 支持对单个切片的判断，支持循环判断，默认分隔符为空格
	1. 对文件取出第一列和第四列<br/>`awk '{print $1,$4}' netstat.txt`，执行之后\$1保存的是切片1的内容，\$4是切片4的内容，是按列逐行保存起来的。
	2. 对文件根据不同的列的对象条件筛选<br/>`awk '$1=="tcp" && $2==1{print $0}' netstat.txt`，Proto为tcp且Recv-Q为1的整行元素打印出来
	3. 筛选出表头，参照第二个<br/>`awk '($1=="tcp" && $2==1) || NR==1 {print $0}' netstat.txt`
	4. 通过","作为分割符进行筛选<br/>`awk -F "," '{print $2}'`，直接筛选第二列，-F是以什么作为分割符
5. 这里是统计engine出现的次数，上面作为grep查找的输出结果中找到重复出现的engine的出现次数
	1. **`awk '{enginearr[$1]++}END{for(i in enginearr)print i "\t" enginearr[i]}'`**
	2. 特别适合处理类似表格方面的数据
	3. 默认的分割符是空格，可以通过-F改变

### 四、批量替换文本内容
1. 这种一般是脱离了IDE，对java的代码文件进行批量的替换修改。

##### 4.1 sed指令
1. 语法：`sed [option] 'sed command' filename`
2. 全名 stream editor，流编辑器
3. 适合对于文本的行内容进行处理，可以添加正则表达式
	1. `sed 's/^Str/String/ replace.java`<br/>默认将修改的replace.java文件中的Str修改为String，并且输出到终端，但是原有文件并不做修改。"^"开头的意思
	2. `sed -i 's/^Str/String/ replace.java`<br/>可以修改原有的文件
	3. `sed -i 's/\.$/\;/' replace.java`<br/>其中"."和";"都是需要添加"\"转义，而后面的"$"表示一行的结束
	4. `sed -i 's/Jack/me/g' replace.java`<br/>后面的g添加了就表示全文中都更换，不添加g只是修改本行遇到的第一个字符串
	5. `sed -i 's/^ *$/d' replace.java`<br/>`^ *$`中间的空格和*表示开头到结尾都是空格，后面的“d”表示符合条件的都删除
	6. `sed -i 's/Integer/d' replace.java`<br/>删除行内有Integer的行






