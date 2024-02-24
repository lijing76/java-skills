package com.jing.generics.test;

import lombok.Data;

@Data
public class TestImpl<T,V> implements Test<T,V>{
    T value1 = null;
    V value2 = null;

    @Override
    public void setMethod1(T t) {
        this.setValue1(t);
    }

    @Override
    public void setMethod2(V v) {
        this.setValue2(v);
    }
}
