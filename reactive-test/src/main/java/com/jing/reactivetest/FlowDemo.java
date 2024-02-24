package com.jing.reactivetest;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import static java.lang.Thread.sleep;

public class FlowDemo {

    static class MyProcessor extends SubmissionPublisher<String> implements Flow.Processor<String, String> {
        private Flow.Subscription subscription;

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            System.out.println("Processor订阅者开始了");
            this.subscription = subscription;
            this.subscription.request(1);
        }

        @Override
        public void onNext(String item) {
            item = item+":哈哈";
            submit(item);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }
    }

    public static void main(String[] args) {
    }

    @Test
    public void TestPublisherAndSubscriber()  throws InterruptedException{
        // 1. 定义一个发布者；发布数据
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        // 2. 定义一个订阅者，并定义数据处理业务逻辑
        Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {

            private Flow.Subscription mySubscription;

            @Override //在订阅时 onXxxx:在xxxx事件发生时，执行这个回调
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println(Thread.currentThread()+"订阅开始了:"+ subscription);
                this.mySubscription = subscription;

                //从上游请求一个数据
                this.mySubscription.request(1);
            }

            @Override //在下一个元素到达时，执行这个回调，接收到新数据
            public void onNext(String item) {
                System.out.println(Thread.currentThread()+"订阅者，接收到数据:"+ item);
                //再从上游请求一个数据
                if(item.equals("p-7"))
                    mySubscription.cancel();
                else
                    this.mySubscription.request(1);
            }

            @Override //当错误发生时
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread()+"订阅者，接收到错误信号:"+ throwable);
            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread()+"订阅者，接收到完成信号:");
            }
        };

        Flow.Subscriber<String> subscriber2 = new Flow.Subscriber<String>() {

            private Flow.Subscription mySubscription;

            @Override //在订阅时 onXxxx:在xxxx事件发生时，执行这个回调
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println(Thread.currentThread()+"》》》订阅开始了:"+ subscription);
                this.mySubscription = subscription;

                //从上游请求一个数据
                this.mySubscription.request(1);
            }

            @Override //在下一个元素到达时，执行这个回调，接收到新数据
            public void onNext(String item) {
                System.out.println(Thread.currentThread()+"》》》订阅者，接收到数据:"+ item);
                //再从上游请求一个数据
                if(item.equals("p-7"))
                    mySubscription.cancel();
                else
                    this.mySubscription.request(1);
            }

            @Override //当错误发生时
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread()+"订阅者，接收到错误信号:"+ throwable);
            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread()+"订阅者，接收到完成信号:");
            }
        };

        // 3. 绑定发布者和订阅者
        publisher.subscribe(subscriber);
        publisher.subscribe(subscriber2);

        // 4. 发布者发送数据
        for (int i = 0; i < 10; i++) {
            //发布10条数据
            //publisher.submit("p-"+i);
            // Publisher发布的数据都在它的buffer区(一个array)
            System.out.println(Thread.currentThread() + "发送数据:"+i);
            if(i>8)
                publisher.closeExceptionally(new RuntimeException("数字大于9"));
            else{
                publisher.submit("p-"+i);
                sleep(100);
            }
        }

        // Subscriber的onXxxx方法在异步线程处理
        // JVM底层对于整个发布订阅关系做好了 异步+缓存区处理 = 响应式系统

        publisher.close();

        sleep(1000);
    }

    @Test
    public void TestProcessor()  throws InterruptedException{
        // 1. 定义一个发布者；发布数据
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        // 定义一个中间操作，每个元素加一个"哈哈"后缀
        Flow.Processor<String,String> processor1 = new MyProcessor();
        Flow.Processor<String,String> processor2 = new MyProcessor();
        Flow.Processor<String,String> processor3 = new MyProcessor();

        // 2. 定义一个订阅者，并定义数据处理业务逻辑
        Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {

            private Flow.Subscription mySubscription;

            @Override //在订阅时 onXxxx:在xxxx事件发生时，执行这个回调
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println(Thread.currentThread()+"订阅开始了:"+ subscription);
                this.mySubscription = subscription;

                //从上游请求一个数据
                this.mySubscription.request(1);
            }

            @Override //在下一个元素到达时，执行这个回调，接收到新数据
            public void onNext(String item) {
                System.out.println(Thread.currentThread()+"订阅者，接收到数据:"+ item);
                //再从上游请求一个数据
                if(item.equals("p-7"))
                    mySubscription.cancel();
                else
                    this.mySubscription.request(1);
            }

            @Override //当错误发生时
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread()+"订阅者，接收到错误信号:"+ throwable);
            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread()+"订阅者，接收到完成信号:");
            }
        };

        // 3. 绑定发布者和订阅者
        publisher.subscribe(processor1);
        processor1.subscribe(processor2);
        processor2.subscribe(processor3);
        processor3.subscribe(subscriber);

        // 4. 发布者发送数据
        for (int i = 0; i < 10; i++) {
            //发布10条数据
            //publisher.submit("p-"+i);
            // Publisher发布的数据都在它的buffer区(一个array)
            System.out.println(Thread.currentThread() + "发送数据:"+i);
            if(i>8)
                publisher.closeExceptionally(new RuntimeException("数字大于9"));
            else{
                publisher.submit("p-"+i);
                sleep(100);
            }
        }

        // Subscriber的onXxxx方法在异步线程处理
        // JVM底层对于整个发布订阅关系做好了 异步+缓存区处理 = 响应式系统

        publisher.close();

        sleep(1000);

    }
}
