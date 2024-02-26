package com.jing.webflux;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import java.io.IOException;


public class FluxMainApplication {
    public static void main(String[] args) throws IOException {
        //快速自己编写一个能处理请求的服务器

        //1.创建一个能处理HTTP请求的处理器
        HttpHandler httpHandler = (ServerHttpRequest request, ServerHttpResponse response)->{
            System.out.println(Thread.currentThread()+":请求进入："+request.getURI());
            //编写请求处理的业务

//            response.getHeaders();
//            response.getCookies(); //获取cookie
//            response.getStatusCode(); //获取状态码
//            response.bufferFactory();
//            response.writeWith(); //把xxx写出去，也返回Mono<void>,代表处理结束。
//            response.setComplete();//返回Mono<void>,代表处理结束。

            //数据的发布者：Mono<DataBuffer>, Flux<DataBuffer>

            //创建响应数据的DataBuffer
            DataBufferFactory dataBufferFactory = response.bufferFactory();

            //准备数据Buffer
            DataBuffer buffer = dataBufferFactory.wrap(new String(request.getURI() + " Hello~!").getBytes());

            //需要一个DataBuffer的发布者
            return response.writeWith(Mono.just(buffer));

        };

        //2.启动一个服务器，监听8080端口，监听数据，拿到数据交给httpHandler进行请求处理
        ReactorHttpHandlerAdapter adaptor = new ReactorHttpHandlerAdapter(httpHandler);

        //3.启动Netty服务器
        HttpServer.create()
                .host("localhost")
                .port(8080)
                .handle(adaptor)
                .bindNow();

        System.out.println("服务器启动完成，监听8080端口，接受请求。。。");

        System.in.read();
        System.out.println("服务器停止。。。");
    }
}
