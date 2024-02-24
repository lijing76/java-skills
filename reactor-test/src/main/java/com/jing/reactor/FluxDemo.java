package com.jing.reactor;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.*;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;

public class FluxDemo {
    public static void main(String[] args) {

    }

    @Test
    public void TestFlux01() throws InterruptedException {
        //        Mono: 0|1个元素的流
//        Flux: N个元素的流

        // 1. 多元素的流
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5);

        // 流不消费就没用；消费：订阅
        just.subscribe(i-> System.out.println("i="+i));
        //一个数据流可以有很多消费者
        just.subscribe(System.out::println);

        //对于每个消费者来说，流都是一样的；广播模式



    }

    @Test
    public void TestFlux02() throws IOException, InterruptedException {
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1)); //每秒产生一个从0开始的递增数字

        flux.subscribe(System.out::println);

        //System.in.read();
        sleep(5000);
    }

    @Test
    public void TestFluxOnComplete() throws IOException, InterruptedException {
        //事件感知API，当流发生事件的时候，触发一个回调；doOnXxx;
        Flux<Integer> flux = Flux.just(1,2,3)
               // .delayElements(Duration.ofSeconds(1))
                .doOnComplete(()-> System.out.println("流结束了！"))
                .doOnCancel(()-> System.out.println("流被取消了！"))
                .doOnNext(i-> System.out.println("doOnNext - " + i));

        flux.subscribe(i-> {
            System.out.println("1 consumed: "+i);
            try{
                sleep(1000);
            }
            catch (Exception e){
                System.out.println("sleep error");
            }
        }
        );
        flux.subscribe(i-> System.out.println("2 consumed: "+i));

        sleep(5000);
    }

    @Test
    public void TestBaseSubscriber(){
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5)
                .doOnNext(i-> System.out.println("publisher doOnNext: "+i));

        just.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("hookOnSubscribe");
                request(1);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("hookOnNext: " + value);
                if(value<3)
                    request(1);
                else
                    cancel();
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("hookOnComplete");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.out.println("hookOnError");
            }

            @Override
            protected void hookOnCancel() {
                System.out.println("hookOnCancel");
            }

            @Override
            protected void hookFinally(SignalType type) {
                System.out.println("hookFinally");
            }

            @Override
            public String toString() {
                return super.toString();
            }
        });

        just.subscribe(i-> System.out.println("2 consumed: "+i));

    }

    @Test
    public void TestOnErrorComplete(){
        //onErrorXxx, doOnXxxx
        //doOnXxx: 发生这个事件的时候产生一个回调，通知你（不能改变）
        //onXxx: 发生这个事件后执行一个动作，可以改变元素、信号
        //AOP: 普通通知（前置，后置，异常，返回） 环绕通知（ProceedingJoinPoint)
        Flux<String> flux = Flux.range(1,10)
                .map(i->{
                    System.out.println("map..."+i);
                    if(i==9){
                        i=i/(9-i);
                    }
                    return "哈哈："+i;
                })
                .onErrorComplete();
        flux.subscribe(v-> System.out.println(v),
                throwable-> System.out.println(throwable),
                ()-> System.out.println("completed"));

    }

    @Test
    public void TestMono(){
        Mono<Integer> integerMono = Mono.just(1);
        integerMono.subscribe(System.out::println);
    }

    @Test
    public void TestBuffer(){
        var flux=Flux.range(1,10)
                .buffer(3); //缓冲区:只能缓存3个元素,消费者以此最多可以拿到3个元素，凑满数批量发给消费者
        flux.subscribe(v-> System.out.println("类型："+v.getClass()+" 值："+v));

        flux.subscribe(v-> {
            v.stream().forEach(System.out::println);
            System.out.println("============");
        });
    }

    @Test
    public void TestLimitRate(){
       Flux.range(1,100)
                .log()
                .limitRate(30) //一次预取30个元素
                .subscribe();
        //75%预取策略 limitRate(100)
        // 第一次抓取100个元素，如果75%的元素已经处理了，继续抓取75%的元素；
    }

    @Test
    public void TestGenerate(){ //同步环境生成数据，用generate
        var flux = Flux.generate(()->0,//初始值
                (state,sink) -> {
            /*for (int i = 0; i < 100; i++) {
                sink.next("哈哈：" + i); //传递数据；可能会抛出【不受检异常（运行时异常）、受检异常（编译时异常）】
            }*/
                    if(state<10) //发送10个数据
                        sink.next(state); //把元素传出去
                    else
                        sink.complete(); //完成
            return state+1;
        });

        flux.log()
                .subscribe();
    }

    @Test
    public void TestCreate() throws InterruptedException { //异步/多线程环境生成数据，用create
/*        var flux = Flux.create(fluxSink -> {
            fluxSink.next("哈哈")
        })

        flux.log()
                .subscribe();*/


        Flux.create(fluxSink -> {
            MyListener myListener = new MyListener(fluxSink);
            for (int i = 0; i < 100; i++) {
                myListener.online("用户"+i);
            }
        })
                .log()
                .subscribe();
    }

    class MyListener{
        FluxSink<Object> sink;

        public MyListener(FluxSink<Object> sink){
            this.sink = sink;
        }
        public void online(String userName){
            System.out.println(Thread.currentThread()+"-用户登录了："+userName);
            sink.next(userName); //传入用户
        }
    }

    @Test
    public void TestHandle(){ //自定义流里的数据返回规则/逻辑
        Flux.range(1,10)
                .handle((value,sink)->{
                    System.out.println("拿到的值："+value);
                    sink.next("aaa"); //向下发送数据的通道
                })
                .log()
                .subscribe();
    }

    @Test
    public void TestSchedulers(){ //自定义线程调度，Schedulers-调度器

        //调度器：线程池
        Schedulers.boundedElastic(); //有界、弹性调度,不是无限扩充的线程池；线程池中有10*CPU core的线程。队列默认100K，keepAliveTime 60s
        Schedulers.immediate();//没有执行线程池，当前线程运行所有操作。
        Schedulers.single();//使用一个固定的单线程
        Schedulers.parallel();//并发池
        Schedulers.fromExecutor(new ThreadPoolExecutor(4,8,60, TimeUnit.SECONDS,new LinkedBlockingQueue<>(1000)));//自己new一个线程池

        Flux.range(1,10)
                .publishOn(Schedulers.immediate())
                .log()
                .publishOn(Schedulers.single())
                .log()
                .publishOn(Schedulers.boundedElastic())
                .log()
                .publishOn(Schedulers.parallel())
                .log()
                .subscribeOn(Schedulers.single())
                .log()
                .subscribeOn(Schedulers.boundedElastic())
                .log()
                .subscribe();
    }

    @Test
    public void TestSchedulers2() throws InterruptedException { //自定义线程调度，Schedulers-调度器
        Scheduler scheduler = Schedulers.parallel();
        Flux<Integer> flux = Flux.range(1,3)
                .map(i->i*2)
                .log() //只要不指定线程池，默认发布者用的线程就是订阅者的线程。
                .publishOn(scheduler)
                .log()
                .map(i->i*10);
        new Thread(()->flux.subscribe(System.out::println)).start();
        Thread.sleep(300);
    }
}
