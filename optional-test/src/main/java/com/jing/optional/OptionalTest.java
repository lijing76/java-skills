package com.jing.optional;

import com.jing.stream.bean.Author;
import com.jing.stream.bean.Book;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OptionalTest {
    public static void main(String[] args) {
        Author author = getAuthor();
        if(author != null) {
            System.out.println(author.getName());
        }
    }

    public static Author getAuthor(){
        Author author = new Author(1L,"蒙多",33,"一个从菜刀中明悟哲理的祖安人",null);
        System.out.println("getAuthor method is invoked...");
        return null;
    }

    public static Optional<Author> getAuthorOptional(){
        Author author = new Author(1L,"蒙多",33,"一个从菜刀中明悟哲理的祖安人",null);
        System.out.println("getAuthor method is invoked...");

        List<Book> books = new ArrayList<>();
        books.add(new Book(1L,"刀的两侧是光明与黑暗","哲学,爱情",88,"用一把刀划分了爱恨"));
        books.add(new Book(2L,"一个人不能死在同一把刀下","个人成长,爱情",99,"讲述如何从失败中明悟真理"));

        author.setBooks(books);


        return Optional.ofNullable(author);
        //return return author==null?Optional.empty():Optional.of(author);
    }

    @Test
    public void testOptionalObjectCreation1(){
        Author author = getAuthor();
        Optional<Author> authorOptional = Optional.ofNullable(author);
        authorOptional.ifPresent(author1 -> System.out.println(author1.getName()));
    }

    @Test
    public void testOptionalObjectCreation2(){
        Optional<Author> authorOptional = getAuthorOptional();
        authorOptional.ifPresent(author1 -> System.out.println(author1.getName()));
    }

    @Test
    public void testOrElseGet(){
        Optional<Author> authorOptional = getAuthorOptional();
        System.out.println(authorOptional.orElseGet(() -> new Author()).getName());
    }

    @Test
    public void testOrElseThrow(){
        Optional<Author> optionalAuthor = getAuthorOptional();
        optionalAuthor.orElseThrow(()->new RuntimeException("数据为null"));
    }

    @Test
    public void testFilter(){
        Optional<Author> optionalAuthor = getAuthorOptional();
        optionalAuthor
                .filter(author -> author.getAge()>100)
                .ifPresent(author-> System.out.println(author.getName()));
    }

    @Test
    public void testIsPresent(){
        Optional<Author> optionalAuthor = getAuthorOptional();
        if(optionalAuthor.isPresent()){
            System.out.println(optionalAuthor.get().getName());
        }
    }

    @Test
    public void testMap(){
        Optional<Author> optionalAuthor = getAuthorOptional();
        Optional<List<Book>> optionalBooks = optionalAuthor.map(author -> author.getBooks());
        optionalBooks.ifPresent(books ->System.out.println(books));
    }
}
