package com.jing.generics.test;

public class GenericsSubBox<E> extends Box<E>{
    public void printGenericsSubBox(E e){
        super.setBox(e);
        System.out.println(super.getBox());
    }
}
