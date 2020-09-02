# AwesomeRetrofitDemo
Some intresting demos of retrofit and okhttp, for [blog](https://juejin.im/post/6860064769534394381)

“这次看点不一样之Retrofit和OkHttp”的伴生demo

演示以下问题示例：

* 动态设置请求超时时间
* 网络优化的判断依据：时长统计工具
* 全局支持人机校验



## 内容说明

Demo: Android 项目，不多做解释了

MockApi：一个用于模拟的接口项目，需要maven，基于SpringBoot，

端口号配置：
./MockApi/src/main/resources/application.properties
application.properties

根据实际的jdk版本或者kotlin版本可能需要修改下注解，如果出现问题可以提一下issue或者博客留言。

使用了拦截器输出请求的信息，查看控制台输出的log即可。

启动：进入MockApi目录执行：

```
mvn spring-boot:run
```

## Demo中对应的三个问题

动态设置时长，为了演示我直接设了1ms，基本都会超时的

时长统计工具，目前已经把信息输出到了界面上

全局支持人机校验，约定code为66666时需要处理人机校验，Demo中省略了人机校验环节，填充了模拟数据直接走完流程，

---

开源不易，如果觉得不错希望可以给项目和文章点个赞，苦兮兮的。
