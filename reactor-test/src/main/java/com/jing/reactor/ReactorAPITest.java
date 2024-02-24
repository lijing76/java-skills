package com.jing.reactor;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ReactorAPITest {

    @Test
    void testFilter(){
        Flux.just(1,2,3,4)
                .log()
                .filter(s->s%2==0)
//                .log()
                .subscribe(System.out::println);

    }

    @Test
    void testFlatMap(){
        Flux.just("zhang san","li si")
                .flatMap(v->{
                    String[] s = v.split(" ");
                    return Flux.fromArray(s); //把数据包装成多元素流
                })
                .log()
                .subscribe();
    }

    @Test
    void testConcatAPIs(){

        //Test concatMap - 一个元素可以变很多单个；
        Flux.just("zhang san","li si")
                .concatMap(s->Flux.just(s+"-- haha"))
                .log()
                .subscribe();

        //Test concat -- 老流 + 新流，新老流中的元素类型可以不同
        Flux.concat(Flux.just(1,2,3), Flux.just("a","b","c"))
                .log()
                .subscribe();

        //Test conactWith -- 老流 + 新流，新老流中的元素类型必须相同
        Flux.just(1,2,3)
                .concatWith(Flux.just(4,5,6))
                .log()
                .subscribe();
    }

    @Test
    void testTransform(){
        AtomicInteger atomic = new AtomicInteger(0);
        Flux<String> flux = Flux.just("a","b","c")
                //.transform(values->{  //transform：不会共享外部变量 - 无状态转换
                .transformDeferred(values->{ //transformDeferred：共享外部变量 - 有状态转换
                    // ++automic
                    if (atomic.incrementAndGet() == 1) {
                        //如果是第一次调用，老流中的所有元素转成大写
                        return values.map(String::toUpperCase);
                    }else{
                        //如果不是第一次调用，原封不动返回
                        return values;
                    }
                });

        flux.subscribe(v-> System.out.println("订阅者1：v="+v));
        flux.subscribe(v-> System.out.println("订阅者2 ：v="+v));

    }

    @Test
    void testEmpty(){
        //Mono.just(null); - 流里有一个null值的元素
        //Mono.empty(); - 流里没有元素,只有完成信号/结束信号
        getMono()
                .defaultIfEmpty("x") //如果发布者元素为空，指定默认值，否则用发布者的值 - 静态兜底数据
                .switchIfEmpty(Mono.just("haha")) //如果发布者元素为空，切换到给定的新元素 - 动态兜底方法
                .subscribe(v-> System.out.println("v="+v));
    }

    Mono<String> getMono(){
        //return Mono.just("a");
        return Mono.empty();
    }
    @Test
    void testMerge() throws InterruptedException {
        Flux.merge(
                        Flux.just(1,2,3).delayElements(Duration.ofMillis(300)),
                        Flux.just("a","b","c").delayElements(Duration.ofMillis(500)),
                        Flux.just("haha","hehe","heihei","xixi")).delayElements(Duration.ofMillis(400))
                .log()
                .subscribe(v-> System.out.println("v="+v));
        Thread.sleep(10000);
    }

    //Tuple: 元组
    //zip()最多可以传入8组Flux进行zip
    @Test
    void testZip() {
        Flux.just(1,2,3,4)
                .zipWith(Flux.just("a","b","c"))
                .subscribe(v-> System.out.println("v="+v));

        Flux.zip(Flux.just(1,2,3,4),Flux.just("a","b","c"),Flux.just(11,22,33,44,55))
                .subscribe(v-> System.out.println(Thread.currentThread()+"v="+v));
    }

    @Test
    void testMixedFlux() {
        Flux.just(1,2,3,"b")
                .subscribe(v-> System.out.println("v="+v+",v type="+v.getClass()));
    }

    //默认：
    //subscriber:消费者可以感知正常元素与流发生的错误
    @Test
    void testError() {
        Flux.just(1,2,0)
                .map(i->"100/"+i+" = "+(100/i))
                .onErrorReturn("Error...")
                .subscribe(v-> System.out.println("v="+v+",v type="+v.getClass()));
    }

    @Test
    void testRetryAndTimeout() throws InterruptedException {
        Flux.just(1,2,3,4)
                .log()
                .delayElements(Duration.ofSeconds(3))
                .timeout(Duration.ofSeconds(2))
                .retry(3) //把流从头到尾重新请求一次
                .map(v->v+"haha")
                .subscribe(v-> System.out.println("v = " + v));

        Thread.sleep(20000);
    }

    @Test
    void testSinks() throws InterruptedException {
        //Sinks：接收器，数据管道，所有数据顺着这个管道往下走
        //Sinks.many();//发送Flux数据
        //Sinks.one();//发送Mono数据
        //Sinks.many().unicast(); //单播，这个管道只能绑定一个订阅者
        //Sinks.many().multicast(); //多播，这个管道能绑定多个订阅者
        //Sinks.many().replay(); //重放，这个管道能重放元素，是否给后来的订阅者把之前的元素发给它，类似于Kafka consumer
/*        Sinks.Many<Object> many = Sinks.many()
                .unicast()//单播
                .onBackpressureBuffer(new LinkedBlockingQueue<>(5)); //背压队列*/

/*        Sinks.Many<Object> many = Sinks.many()
                .multicast() //多播 //订阅者默认从订阅的时刻开始接元素
                .onBackpressureBuffer(); //背压队列*/

        //支持回放，并限制最多回放个数，底层利用队列保持重放数据
        Sinks.Many<Object> many = Sinks.many().replay().limit(3);

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
            many.tryEmitNext("a-"+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        many.asFlux().subscribe(v-> System.out.println("v1 = " + v));

        new Thread(()->{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            many.asFlux().subscribe(v-> System.out.println("v2 = " + v));
        }).start();

        Thread.sleep(11000);
    }

    @Test
    void testCache() throws InterruptedException {
        Flux<Integer> cache = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .cache(3);//缓存元素

        cache.subscribe(v-> System.out.println("v1 = " + v));

        new Thread(()->{
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            cache.subscribe(v-> System.out.println("v2 ===== " + v));
        }).start();

        Thread.sleep(11000);
    }

    @Test //测试阻塞式API，回到阻塞式的编程世界。
    void testBlock(){ //拿到处理完成后的数据的集合
        List<Integer> integers = Flux.just(1, 2, 3, 4, 5)
                .map(i -> i + 10)
                .collectList()
                .block(); //block()也是一种订阅者

        System.out.println("integers = " + integers);
    }

    @Test
    void testParallelFlux() throws IOException {
        //百万数据，8个线程，每个线程处理100个数据，进行分批处理，直到处理结束
        Flux.range(1,100)
                .buffer(10)
                .parallel(8)
                .runOn(Schedulers.newParallel("yy"))
                .log()
                .subscribe(v-> System.out.println("v = " + v));

        System.in.read();
    }

    //Context API https://projectreactor.io/docs/core/release/reference/#context

    //Threadlocal在响应式编程中无法使用
    //响应式中，数据流期间共享数据，Context API: Context:读写，ContextView:只读
    @Test
    void testThreadLocal(){
        //必须用支持Context的中间操作
        Flux.just(1,2,3)
                .transformDeferredContextual((flux,context)->{
                    System.out.println("flux = " + flux);
                    System.out.println("context = " + context);
                    return flux.map(i->i+"===>"+context.get("prefix"));
                })
                //上游能拿到下游的最近一次数据
                .contextWrite(Context.of("prefix","哈哈")) //Threadlocal共享了数据，上游的所有人能看到；Context由下游传播给上游
                .contextWrite(Context.of("aaa","bbb"))
                .subscribe(v-> System.out.println("v = " + v));

        //以前，命令式编程
        //controller->service->dao

        //响应式编程 dao->service->controller 从下游反向传播

    }

}
