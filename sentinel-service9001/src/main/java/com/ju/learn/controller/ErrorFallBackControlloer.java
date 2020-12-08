package com.ju.learn.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
