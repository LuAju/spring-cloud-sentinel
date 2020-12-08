package com.ju.learn.myHandler;

import com.alibaba.csp.sentinel.slots.block.BlockException;

public class RateLimitBlockHandler {
    // 必须要是静态方法
    public static Object rateLimitHandler(BlockException blockException){
        return "自定义异常返回方法";
    }
}
