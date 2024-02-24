package com.jing.generics.test;

public class SubBox extends Box<String>{
    public void printBox(String str){
        super.setBox(str);
        System.out.println(super.getBox());
    }
}
