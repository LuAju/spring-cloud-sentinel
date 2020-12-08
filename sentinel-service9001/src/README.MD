# 准备
## 1. 开启nacos
因为使用了服务注册，所以启动nacos
启动类上加注解 @EnableDiscoveryClient
## 2. 开启sentinel
java -jar运行sentinel
访问 http://localhost:8080 便可以查看到具体的接口（需要重新访问一次，不需要打注解）

# 流控模式

## 直接
当api流量达到阈值时，限流自己
## 关联
关联某个资源，当关联资源到达阈值时，限流自己
## 链路
只指定链路上的限流0
# 流控效果
## 快速失败
超过阈值 直接失败
## 慢热
一开始的阈值不会直接到达设置的阈值，之后才会
## 排队等待
超过阈值，排队等待


# 降级规则
sentinel没有半开状态，降级规则满足时，时间窗口内 服务不可用
## 平均响应时间（秒级）
最大4900，1秒的请求数大于5
## 异常比例
异常比例（秒级统计）超过阈值且QPS大于5
## 异常数
时间窗口内 异常数超过阈值

# 热点参数
略。
根据具体的参数值设置不同的限流规则
     
# ResourceSentinel 兜底方法
## 流控兜底 blockHandler
注解只会对符合限流的方法进行兜底，不会对不满足流控规则的运行时异常兜底。
实例
```java
    @GetMapping("/testB")
    @SentinelResource(value = "testB",blockHandler = "testBFallBack")
    public Object testB(){
        int i = 10 / 0;
        return "testB";
    }

    public Object testBFallBack(BlockException blockException){
        System.out.println("使用自定义请求");
        return "testB 请求出错了";
    }
```
可能会报如下错误
com.alibaba.csp.sentinel.slots.block.degrade.DegradeException: null
原因是 资源名应为@SentinelResource内的value值，兜底方法的返回值应该与其一致，
参数应该一直，且要有BlockException参数放在最后。

创建限流的兜底方法
```java
@RestController
public class RateLimitController {
    @GetMapping("/rateLimit")
    // 添加blockHandler类和方法
    @SentinelResource(value = "rateLimit",
            blockHandlerClass = RateLimitBlockHandler.class,
            blockHandler = "rateLimitHandler")
    public Object rateLimit(){
        return "正常的返回";
    }
}
```
```java
public class RateLimitBlockHandler {
    // 必须要是静态方法，作用域为public， 返回值要一致，参数必须要包括blockException（是否需要包含被兜底方法的参数未知）
    public static Object rateLimitHandler(BlockException blockException){
        return "自定义异常返回方法";
    }
}
```
当资源触发限流规则时，自动触发配置好的兜底方法。

## fallback方法
```java
@RestController
public class ErrorFallBackControlloer {
    @GetMapping("/getError")
    @SentinelResource(value = "getError",fallback = "getError" )
    public Object error(){
        int i = 10 / 0;
        return "error";
    }

    public Object getError(Throwable throwable){
        return "出错了，我兜底"+throwable.getMessage();
    }
}
```

# sentinel 规则持久化
## pom.xml
```xml
   <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentinel-datasource-nacos</artifactId>
        </dependency>
```
## yaml
```yaml
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
    sentinel:
      transport:
        dashboard: localhost:8080
        port: 8719
      #  sentinel规则持久化到数据库
      datasource:
        ds1:
          nacos:
            server-addr: localhost:8848
            dataId: ${spring.application.name}
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: flow
```
失败了，配置后没启动

``` txt
Description:

Application failed to connect to Nacos server: ""

Action:

Please check your Nacos server config

```
