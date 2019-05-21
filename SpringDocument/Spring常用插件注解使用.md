## 1. 
#### 一、注解
1. **@RestController**
	1. 相当于@ResponseBody ＋ @Controller合在一起的作用
	2. 返回json数据不需要在方法前面加@ResponseBody注解了，但使用@RestController这个注解，就不能返回jsp,html页面，视图解析器无法解析jsp,html页面

	
#### 二、Actuator插件
