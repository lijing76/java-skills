package com.jing.lambda;

import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

public class LambdaTest01 {
    public static void main(String[] args) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("新线程中Run方法被执行了！");
//            }
//        }).start();

        new Thread(()->{System.out.println("新线程中Run方法被执行了！");}).start();
    }

    @Test
    public void test01(){
        int result=0;
        int resultLambda=0;

        //匿名类的实现
        result = calculateNum(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left*left+right*right;
            }
        });

        //Lambda的实现
        resultLambda = calculateNum((left,right)->{return left*left+right*right;});

        System.out.println("calucate result:" + result);
        System.out.println("calucate lambda result:" + resultLambda);
    }

    public static int calculateNum(IntBinaryOperator operator){
        int a=10;
        int b=20;
        return operator.applyAsInt(a,b);
    }

    @Test
    public void test02(){

        printNum(new IntPredicate() {
            @Override
            public boolean test(int value) {
                return value%2==0;
            }
        });

        printNum((a)->{return a%2==0;});
    }

    public static void printNum(IntPredicate predicate){
        int[] arr={1,2,3,4,5,6,7,8,9,10};
        for (int i : arr) {
            if(predicate.test(i)){
                System.out.println(i);
            }
        }
    }

    public static <R> R typeConver(Function<String, R> function){
        String str="1235";
        R result = function.apply(str);
        return result;
    }

    @Test
    public void test03(){
        String result = typeConver(s->{
            return s;
        });
        System.out.println("test03 result:" + result);

        
        Integer intResult = typeConver(s->{
            return Integer.valueOf(s);
        });
        System.out.println("intResult: " + intResult);
    }

    public static void foreachArr(IntConsumer consumer){
        int[] arr = {1,2,3,4,5,6,7,8,9,10};
        for (int i:arr) {
            consumer.accept(i);
        }
    }

    @Test
    public void test04(){
        foreachArr(i->{
            System.out.println("i="+i);
        });
    }

}
