package com.jing.stream;

import com.jing.stream.bean.Author;
import com.jing.stream.bean.Book;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamTest {
    public static void main(String[] args) {

    }

    private static List<Author> getAuthors(){
        Author author1 = new Author(1L,"蒙多",33,"一个从菜刀中明悟哲理的祖安人",null);
        Author author2 = new Author(2L,"亚拉索",15,"狂风也追逐不上他的思考速度",null);
        Author author3 = new Author(3L,"易",14,"是这个世界在限制他的思维",null);
        Author author4 = new Author(3L,"易",14,"是这个世界在限制他的思维",null);

        List<Book> books1 = new ArrayList<>();
        List<Book> books2 = new ArrayList<>();
        List<Book> books3 = new ArrayList<>();

        books1.add(new Book(1L,"刀的两侧是光明与黑暗","哲学,爱情",88,"用一把刀划分了爱恨"));
        books1.add(new Book(2L,"一个人不能死在同一把刀下","个人成长,爱情",99,"讲述如何从失败中明悟真理"));

        books2.add(new Book(3L,"那风吹不到的地方","哲学",85,"带你用思维去领略世界的尽头"));
        books2.add(new Book(3L,"那风吹不到的地方","哲学",85,"带你用思维去领略世界的尽头"));
        books2.add(new Book(4L,"吹或不吹","爱情,个人传记",56,"一个哲学家的恋爱观注定很难把他所在的时代理解"));

        books3.add(new Book(5L,"你的剑就是我的剑","爱情",56,"无法想象一个武者能对他的伴侣这么的宽容"));
        books3.add(new Book(6L,"风与剑","个人传记",100,"两个哲学家灵魂与肉体的碰撞会激起怎么样的火花呢？"));
        books3.add(new Book(6L,"风与剑","个人传记",100,"两个哲学家灵魂与肉体的碰撞会激起怎么样的火花呢？"));

        author1.setBooks(books1);
        author2.setBooks(books2);
        author3.setBooks(books3);
        author4.setBooks(books3);

        List<Author> authorList = new ArrayList<>(Arrays.asList(author1,author2,author3,author4));

        return authorList;
    }

    @Test
    public void TestListSteam(){
        List<Author> authors = getAuthors();
        System.out.println(authors);

        //打印所有年龄小于18的作家的名字，并且要注意去重。
        authors.stream() //集合转换成流
                .distinct()
                .filter(author -> author.getAge()<18)
                .forEach(author -> System.out.println(author.getName()));
    }

    @Test
    public void TestArrayStream(){
        Integer[] arr = {1,2,3,4,5};
        //方法一：使用Arrays.stream()方法
        System.out.println("方法一：Arrays.stream()");
        Stream<Integer> stream = Arrays.stream(arr);
        stream
                .distinct()
                .filter(i->i>2)
                .forEach(i-> System.out.println(i));

        //方法二：使用Stream.of()方法
        System.out.println("方法二：Stream.of()");
        Stream<Integer> stm = Stream.of(arr);
        stm
                .distinct()
                .filter(i->i>2)
                .forEach(i-> System.out.println(i));

    }

    @Test
    public void TestMapStream(){
        Map<String, Integer> map = new HashMap<>();
        map.put("蜡笔小新",19);
        map.put("黑子",17);
        map.put("日向翔阳",16);

        Stream<Map.Entry<String, Integer>> stream = map.entrySet().stream();
        stream
                .filter(e->e.getValue()>16)
                .forEach(e-> System.out.println(e));

        Stream<Map.Entry<String, Integer>> stream1 = map.entrySet().stream();
        stream1
                .map((e)->e.getKey())
                .forEach(name-> System.out.println(name));

    }

    @Test
    public void TestSorted(){
        List<Author> authors = getAuthors();
        authors.stream()
                .distinct()
                .sorted((o1,o2)->o1.getAge()-o2.getAge())
                .forEach(a-> System.out.println(a));
    }

    @Test
    public void TestLimit(){
        List<Author> authors = getAuthors();
        authors.stream()
                .distinct()
                .sorted((o1,o2)->o1.getAge()-o2.getAge())
                .limit(2)
                .forEach(a-> System.out.println(a));
    }

    @Test
    public void TestSkip(){
        List<Author> authors = getAuthors();
        authors.stream()
                .distinct()
                .sorted((o1,o2)->o1.getAge()-o2.getAge())
                .skip(1)
                .forEach(a-> System.out.println(a));
    }

    @Test
    public void TestFlatMap(){
        List<Author> authors = getAuthors();
        authors.stream()
                .flatMap(author->author.getBooks().stream())
                .distinct()
                .forEach(a-> System.out.println(a));
    }

    @Test
    public void TestFlatMap2(){
        List<Author> authors = getAuthors();
        authors.stream()
                .flatMap(author->author.getBooks().stream())
                .distinct()
                .flatMap(book-> Arrays.stream(book.getCategory().split(",")))
                .distinct()
                .forEach(a-> System.out.println(a));
    }

    @Test
    public void TestMaxMin(){
        List<Author> authors = getAuthors();
        Optional<Integer> max = authors.stream()
                .flatMap(author -> author.getBooks().stream())
                .distinct()
                .map(book -> book.getScore())
                .max((o1, o2) -> o1 - o2);
        System.out.println(max.get());
    }

    @Test
    public void TestCollectList(){
        //获取一个存放所有作者名字的list集合
        List<Author> authors = getAuthors();
        List<String> nameList = authors.stream()
                .map(author -> author.getName())
                .collect(Collectors.toList());
        System.out.println(nameList);
    }

    @Test
    public void TestCollectSet(){
        //获取一个存放所有书名的set集合
        List<Author> authors = getAuthors();
        Set<String> bookSet = authors.stream()
                .flatMap(author -> author.getBooks().stream())
                .map(book->book.getName())
                .collect(Collectors.toSet());
        System.out.println(bookSet);
    }

    @Test
    public void TestCollectMap(){
        //获取一个Map集合，map的key为作者名，value为List<Book>
        List<Author> authors = getAuthors();
        var collect = authors.stream()
                .distinct()
                .collect(Collectors.toMap(author -> author.getName(), author -> author.getBooks()));
        System.out.println(collect);
    }

    @Test
    public void TestAnyMatch(){
        //判断是否有年龄在29岁以上的作家
        List<Author> authors = getAuthors();
        var check = authors.stream()
                .anyMatch(author -> author.getAge() > 29);
        System.out.println(check);
    }

    @Test
    public void TestAllMatch(){
        //判断是否所有作家年龄在19岁以上
        List<Author> authors = getAuthors();
        var check = authors.stream()
                .allMatch(author -> author.getAge() > 19);
        System.out.println(check);
    }

    @Test
    public void TestNoneMatch(){
        //判断是否有作家年龄在100岁以上
        List<Author> authors = getAuthors();
        var check = authors.stream()
                .noneMatch(author -> author.getAge() > 100);
        System.out.println(check);
    }

    @Test
    public void TestFindAny(){
        //获取任意一个年龄大于18的作家，如果存在就输出他的名字
        List<Author> authors = getAuthors();
        Optional<Author> optionalAuthor = authors.stream()
                .filter(author -> author.getAge() > 18)
                .findAny();

        //Optional.ifPresent()可以避免空指针问题
        optionalAuthor.ifPresent(author -> System.out.println(author.getName()));
    }

    @Test
    public void TestFindFirst(){
        //获取年龄最小的作家，输出他的名字
        List<Author> authors = getAuthors();
        var first = authors.stream()
                .sorted((author1, author2) -> author1.getAge() - author2.getAge())
                .findFirst();

        first.ifPresent(author-> System.out.println(author.getName()+":"+author.getAge()));
    }

    @Test
    public void TestReduce1(){
        //对所有作者的年龄求和
        List<Author> authors = getAuthors();
        Integer sum = authors.stream()
                .distinct()
                .map(author -> author.getAge())
                .reduce(0, (result, age) -> result + age);
        System.out.println(sum);

    }

    @Test
    public void TestReduce2(){
        //求所有作者的年龄最大值
        List<Author> authors = getAuthors();
        Integer maxAge = authors.stream()
                .distinct()
                .map(author -> author.getAge())
                .reduce(0, (result, age) -> result>age?result:age);
        System.out.println(maxAge);
    }

    @Test
    public void TestReduce3(){
        //求所有作者的年龄最小值
        List<Author> authors = getAuthors();
        Integer minAge = authors.stream()
                .distinct()
                .map(author -> author.getAge())
                .reduce(Integer.MAX_VALUE, (result, age) -> result>age?age:result); //两个参数的方式
        System.out.println(minAge);
    }

    @Test
    public void TestReduce4(){
        //求所有作者的年龄最小值
        List<Author> authors = getAuthors();
        Optional<Integer> minAgeOptional = authors.stream()
                .distinct()
                .map(author -> author.getAge())
                .reduce((result, age) -> result>age?age:result);//一个参数的方式
        minAgeOptional.ifPresent(ma-> System.out.println(ma));
    }

    @Test
    public void TestAnd(){
        //打印年龄大于17并且姓名长度大于1的作家
        List<Author> authors = getAuthors();
        authors.stream()
                .filter(new Predicate<Author>() { //无法转化成Lambda表达式
                    @Override
                    public boolean test(Author author) {
                        return author.getAge()>17;
                    }
                }.and(new Predicate<Author>() {
                    @Override
                    public boolean test(Author author) {
                        return author.getName().length()>1;
                    }
                }))
                .forEach(author -> System.out.println(author.getName() + ":::" + author.getAge()));

    }

    @Test
    public void TestParallelStream(){
        Stream<Integer> stream = Stream.of(1,2,3,4,5,6,7,8,9,10);

        Optional<Integer> reduce = stream
                .parallel() //转化为并行流
                .peek(num-> System.out.println(num + "::"+Thread.currentThread().getName())) //专门用于调试的方法
                .filter(num -> num >5)
                .reduce(Integer::sum);

        reduce.ifPresent(System.out::println);

    }

}
