## Redis分布式锁原理

1. Redis分布式锁命令
2. 与时间戳的结合
3. Redis分布式锁流程图
4. Redis分布式锁优化版流程图
5. 内存解析，流程解析
6. Redis分布式锁如何防死锁
7. Redis分布式锁双重防死锁演进

#### 一、Redis分布式锁命令
1. **setnx**<br/>not exist先判断是否存在，是具有原子性的，若是执行两个命令就不具有原子性了
2. **getset**<br/>获取旧的值，set新的值，也是具有原子性的
3. **expire**<br/>设置value的有效期
4. **del**<br/>删除

#### 二、与时间戳的结合
1. 这个有一种自己通过判断，并set一个自定义的key值的value为时间戳的算法，set过后，通过setnx和getset的方式实现的一个锁，并且通过对value超时时间的判断来解除死锁的情况

```
@Scheduled(cron = "0 */1 * * * ?")  // 表示每一分钟(每个1分钟的整数倍)
public void closeOrderTaskV3() {
    log.info("关闭订单定时任务启动");
    long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "5000  "));

    Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));
    if (setnxResult != null && setnxResult.intValue() == 1) {
        // 如果返回值是1，代表设置成功，获取锁
        closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    } else {
        // 未获取到锁，继续判断，判断时间戳，看是否可以重置并获取到锁
        String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        if (lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)) {

            String getSetResult = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockTimeout));
            // 再次用当前时间戳getset
            // 返回给定的key的旧值，->旧值判断，是否可以获取锁
            // 当key没有旧值时，即key不存在时，返回nil -> 获取锁
            // 这里我们set了一个新的value值，返回旧的值
            if (getSetResult == null || (getSetResult != null && StringUtils.equals(lockValueStr, getSetResult))) {
                // 真正获取到锁
                closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            } else {
                log.info("没有获得分布式锁: {}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        } else {
            log.info("没有获得分布式锁: {}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
    }

    log.info("关闭订单定时任务结束");
}
```


#### 三、Redis分布式锁流程图
![9443C0B4-5848-4328-96BA-0A9832558AE0.png](https://i.loli.net/2019/05/09/5cd3af7fd575e.png)


#### 四、Redis分布式锁优化版流程图

![6280C0B9-DB65-4712-A0AB-0CFC1C6243A3.png](https://i.loli.net/2019/05/09/5cd3b0199a73a.png)




