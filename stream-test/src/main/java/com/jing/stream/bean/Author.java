package com.jing.stream.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Author /*implements Comparable<Author>*/{

    private Long id;

    private String name;

    private Integer age;

    private String intro;

    private List<Book> books;

/*    @Override
    public int compareTo(Author a) {
        //return this.getAge()-a.getAge(); //升序
        return a.getAge()-this.getAge();  //降序
    }*/
}
