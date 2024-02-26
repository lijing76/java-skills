package com.jing.webflux.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class MyWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        var response = exchange.getResponse();

        System.out.println("请求处理放行到目标方法之前。。。");
        Mono<Void> filter = chain
                .filter(exchange) //放行
                .doOnError(err->{
                    //目标方法异常后做事
                    System.out.println("目标方法异常以后。。。");
                })
                .doFinally(signalType -> {
                    //这里才是目标方法执行之后。。
                    System.out.println("目标方法执行以后。。。");
                });

        //上面执行不花时间，以为Mono是异步的
        //System.out.println("目标方法执行以后。。。");
        return filter;
    }
}
