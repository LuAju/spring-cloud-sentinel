package com.ju.learn.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.ju.learn.myHandler.RateLimitBlockHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
