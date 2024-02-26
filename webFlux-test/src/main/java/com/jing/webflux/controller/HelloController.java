package com.jing.webflux.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

//@Controller
@RestController
public class HelloController {

    //WebFlux：向下兼容原来SpringMVC的大多数注解和API
    @GetMapping(value="/hello") //http://localhost:8080/hello?key=hehe
    public String hello(@RequestParam(value="key",required = false,defaultValue = "defaultKey") String key){
        return "Hello World -->" + key;
    }

    //现在推荐的方式
    //返回单个数据：Order, User, String, Map, etc. 用Mono包装：Mono<Order>, Mono<User>,etc.
    //返回多个数据:用Flux包装: Flux<Order>, Flux<User>, etc.
    //配合Flux, 完成SSE(Server Send Event):服务端事件推送
    //SpringMVC以前怎么用，基本上可以无缝切换
    @GetMapping("/haha")
    public Mono<String> haha(){

        return Mono.just("hahaha");
    }

    @GetMapping("/hehe")
    public Flux<String> hehe(){
        return Flux.just("hehe1","hehe2","hehe3");
    }

    //TEXT_EVENT_STREAM_VALUE = "text/event-stream"
    //SSE 测试，ChatGPT都在用 - 内容生成逐步出现的效果 -- 服务端推送
    @GetMapping(value="/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sse(){
        return Flux.range(1,100)
                .map(i->"ha"+i)
                .delayElements(Duration.ofSeconds(1));
    }

    @GetMapping(value="/sse2",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sse2(){
        return Flux.range(1,10)
                .map(i-> {
                    //构建一个SSE对象
                    return ServerSentEvent.builder("ha"+i)
                            .id(i+"")
                            .comment("hei-"+i)
                            .event("haha")
                            .build();
                })
                .delayElements(Duration.ofMillis(500));
    }

    @GetMapping("/test-exception")
    public Mono<String> testException(){

        return Mono.just(0)
                .map(i->10/i)
                .map(i->"haha:"+i);
    }

    //传参Test
    //参考文档：https://docs.spring.io/spring-framework/reference/6.0/web/webflux/controller/ann-methods/arguments.html
    @GetMapping("/parameter")
    public Mono<String> testParameter(ServerWebExchange exchage,
                                      WebSession webSession, HttpMethod httpMethod){

        //ServerWebExchange对象，封装了ServerHttpRequest/ServerHttpResponse对象
        ServerHttpRequest request = exchage.getRequest();
        ServerHttpResponse response = exchage.getResponse();

        //访问Session对象
        Object aaa = webSession.getAttribute("aaa");
        webSession.getAttributes().put("aa","nn");

        String methodName = httpMethod.name();
        System.out.println("methodName = " + methodName);
        return Mono.just("1");
    }

    @GetMapping("/response")
    public ResponseEntity<String> testResonse(){

        return ResponseEntity.status(200)
                .header("aaa","bbb")
               // .contentType(MediaType.APPLICATION_CBOR)
                .body("aaa");

    }

    //Rendering: 一种视图对象
    @GetMapping("/bai")
    public Rendering render(){
        //Rendering.redirectTo("/aaa");//重定向到当前根目录下的 aaa
        return Rendering.redirectTo("http://www.baidu.com").build(); //不能用@RestController, 而要用@Controller

    }

}
