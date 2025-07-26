package com.von.base.java8.example5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 流范围
 */
public class StreamRangeTest {
    public static void main(String[] args) {
        // 数值范围
        // 和数字打交道时，有一个常用的东西就是数值范围。
        // 比如，假设你想要生成1和100之间的所有数字。
        // Java 8引入了两个可以用于IntStream和LongStream的静态方法，
        // 帮助生成这种范围：range和rangeClosed。
        // 这两个方法都是第一个参数接受起始值，第二个参数接受结束值。
        // 但range是不包含结束值的，而rangeClosed则包含结束值。

        // 不包含100
        IntStream evenNumbers = IntStream.range(1, 100).filter(n -> n % 2 == 0);
        evenNumbers.forEach(System.out::println);
        System.out.println("-----------------------------------------------");
        // 包含100
        IntStream evenNumbers1 = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0);
        evenNumbers1.forEach(System.out::println);

        // 生成勾股定理
//        IntStream.rangeClosed(1, 100)
//                .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
//                .boxed()
//                .map(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});
//
//        IntStream.rangeClosed(1, 100)
//                .filter(b -> Math.sqrt(a*a + b*b) % 1 == 0)
//                .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)});

        Stream<int[]> pythagoreanTriples =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a ->
                                IntStream.rangeClosed(a, 100)
                                        .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
                                        .mapToObj(b ->
                                                new int[]{a, b, (int) Math.sqrt(a * a + b * b)})
                        );

        pythagoreanTriples.limit(10).forEach(t -> System.out.printf("%d, %d, %d", t[0], t[1], t[2]).println());


        // 优化
        // 目前的解决办法并不是最优的，因为你要求两次平方根。
        // 让代码更为紧凑的一种可能的方法是，
        // 先生成所有的三元数(a*a, b*b, a*a+b*b)，然后再筛选符合条件的。
        Stream<double[]> pythagoreanTriples2 =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a ->
                                IntStream.rangeClosed(a, 100)
                                        .mapToObj(
                                                b -> new double[]{a, b, Math.sqrt(a * a + b * b)})
                                        .filter(t -> t[2] % 1 == 0));

        pythagoreanTriples2.limit(10).forEach(t -> System.out.printf("%f, %f, %f", t[0], t[1], t[2]).println());


        // 由值创建流
        Stream.of("abc", "hello", "nihao", "welcome").map(String::toUpperCase).forEach(System.out::println);

        // 创建一个空流
        Stream<Object> empty = Stream.empty();

        // 由数组创建流
        Integer[] integers = {1, 2, 3, 4, 5, 6, 7};
        Stream<Integer> stream = Arrays.stream(integers);
        System.out.println(stream.reduce(0, Integer::sum));
        // 下面的代码执行如下报错
        // Exception in thread "main" java.lang.IllegalStateException: stream has already been operated upon or closed

        // 因为stream流只能被消费一次
        // 改成如下方式
        stream = Arrays.stream(integers);
        System.out.println(stream.mapToInt(Integer::intValue).sum());


        // 由文件创建流
//        try {
//            long count = Files.lines(Path.of("")).count();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        // 由函数生成流：创建无限流
        // Stream API提供了两个静态方法来从函数生成流：Stream.iterate和Stream.generate。
        // 这两个操作可以创建所谓的无限流：不像从固定集合创建的流那样有固定大小的流。
        // 由iterate和generate产生的流会用给定的函数按需创建值，因此可以无穷无尽地计算下去！
        // 一般来说，应该使用limit(n)来对这种流加以限制，以避免打印无穷多个值。

        // 1、迭代
        Stream.iterate(0, n -> n + 2)
                .limit(10)
                .forEach(System.out::println);
        // 斐波纳契元组序列
        Stream.iterate(new int[]{0, 1},
                        t -> new int[]{t[1], t[0]+t[1]})
                .limit(20)
                .forEach(t -> System.out.println("(" + t[0] + "," + t[1] +")"));
        // 2、生成
        // 与iterate方法类似，generate方法也可让你按需生成一个无限流。
        // 但generate不是依次对每个新生成的值应用函数的。
        // 它接受一个Supplier<T>类型的Lambda提供新的值。
        Stream.generate(Math::random)
                .limit(5)
                .forEach(System.out::println);

    }
}
