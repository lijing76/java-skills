package com.jing.generics.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Box<E> {
    private E box = null;
}
