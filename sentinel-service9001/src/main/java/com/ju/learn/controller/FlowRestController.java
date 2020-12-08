package com.ju.learn.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlowRestController {
    @RequestMapping
    public Object testA(){
        return "testA";
    }

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
}
