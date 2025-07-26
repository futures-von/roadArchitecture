package com.von.base.java8.example1;

import java.util.function.Function;

/**
 * 函数复合
 * Java 8自带一些常用的函数式接口，放在java.util.function包里，包括Predicate
 * <T>、Function<T,R>、Supplier<T>、Consumer<T>和BinaryOperator<T>
 *
 * 为了避免装箱操作，对Predicate<T>和Function<T, R>等通用函数式接口的原始类型
 * 特化：IntPredicate、IntToLongFunction等。
 */
public class FunctionTest {


    public static void main(String[] args) {

        // 函数复合
        // 最后，你还可以把Function接口所代表的Lambda表达式复合起来。Function接口为此配
        // 了andThen和compose两个默认方法，它们都会返回Function的一个实例。
        // andThen方法会返回一个函数，它先对输入应用一个给定函数，再对输出应用另一个函数。
        Function<Integer, Integer> a = x -> x + 1;
        Function<Integer, Integer> b = x -> x * 2;
        Function<Integer, Integer> at = a.andThen(b);
        System.out.println(at.apply(1));
        // 你也可以类似地使用compose方法，
        // 先把给定的函数用作compose的参数里面给的那个函数，
        // 然后再把函数本身用于结果
        Function<Integer, Integer> c = a.compose(b);
        System.out.println(c.apply(1));

        // 总结：这两个方法的区别在于执行顺序：
        // andThen => 先执行自己，再执行其他
        // compose => 与andThen相反，先执行其他，再执行自己

    }
}
