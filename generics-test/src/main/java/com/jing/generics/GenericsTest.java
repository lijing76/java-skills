package com.jing.generics;

import com.jing.generics.test.Box;
import com.jing.generics.test.GenericsSubBox;
import com.jing.generics.test.SubBox;
import com.jing.generics.test.TestImpl;
import org.junit.Test;

import java.util.ArrayList;

public class GenericsTest {
    public static void main(String[] args) {
        Box<String> box = new Box<>();
        box.setBox("String Box");
        System.out.println(box.getBox());

        Box<Integer> box1 = new Box<>();
        box1.setBox(100);
        System.out.println(box1.getBox());
    }

    @Test
    public void testSubBox(){
        SubBox subBox = new SubBox();
        subBox.printBox("subBox is printed!");
    }

    @Test
    public void getGenericsSubBox(){
        GenericsSubBox<Integer> subBox = new GenericsSubBox<>();
        subBox.printGenericsSubBox(100);
    }

    @Test //测试接口，及多个泛型
    public void getInterface(){
        TestImpl<String, Integer> testData = new TestImpl<>();
        testData.setMethod1("String Value");
        testData.setMethod2(100);
        System.out.println("Value1<String>: " + testData.getValue1());
        System.out.println("Value2<Integer>: " + testData.getValue2());
    }

    public static <T> T test(T t){
        return t;
    }

    public static <E> void test1(E e){
        System.out.println(e);
    }

    @Test
    public void testGenericsMethod(){
        String str = test("String Value is set!");
        System.out.println(str);

        Integer i = test(100);
        System.out.println("Integer Value is set:" + i);

        test1("haha");
    }

    @Test
    public void testArrayList(){
        ArrayList list = new ArrayList();
        list.add("Test");
        list.add(100);
        for (Object o : list) {
            System.out.println(o);
        }

        ArrayList<String> strList = new ArrayList<>();
        strList.add("Test");
        strList.add("100");
        for (String s : strList) {
            System.out.println(s);
        }
    }
}
