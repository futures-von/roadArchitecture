package com.von.base.java8.example1;

import java.util.Comparator;
import java.util.function.Predicate;

import static com.von.base.java8.example1.FunctionInterface.appleList;

/**
 * 谓词复合
 */
public class PredicateTest {

    public static void main(String[] args) {
        // 比较器复合
        appleList.stream()
                .sorted(Comparator.comparing(FunctionInterface.Apple::getWeight))
                .forEach(System.out::println);
        System.out.println("------------------------------------------------------------------------");
        // 逆序
        appleList.stream()
                .sorted(Comparator.comparing(FunctionInterface.Apple::getWeight).reversed())
                .forEach(System.out::println);
        System.out.println("------------------------------------------------------------------------");
        // 比较器链, 如果存在相同的值，则添加另外一种排序规则
        // 比如下面现按照重量排序，如果重量相同可按照颜色排序
        appleList.stream()
                .sorted(Comparator.comparing(FunctionInterface.Apple::getWeight)
                        .thenComparing(FunctionInterface.Apple::getColor))
                .forEach(System.out::println);
        System.out.println("------------------------------------------------------------------------");


        // 谓词复合
        // 谓词接口包括三个方法：negate、and和or，让你可以重用已有的Predicate来创建更复杂的谓词。
        // 获取所有非红苹果
        Predicate<FunctionInterface.Apple> redPredicate = apple -> apple.getColor().equalsIgnoreCase("red");
        Predicate<FunctionInterface.Apple> greenPredicate = apple -> apple.getColor().equalsIgnoreCase("green");
        Predicate<FunctionInterface.Apple> weightPredicate = apple -> apple.getWeight() > 6;

        // 获取非红苹果
        appleList.stream().filter(redPredicate.negate()).forEach(System.out::println);

        System.out.println("------------------------------------------------------------------------");

        // 获取红苹果或者绿苹果
        appleList.stream().filter(redPredicate.or(greenPredicate)).forEach(System.out::println);

        System.out.println("------------------------------------------------------------------------");

        // 获取红苹果并且重量大于6
        appleList.stream().filter(redPredicate.and(weightPredicate)).forEach(System.out::println);

        System.out.println("------------------------------------------------------------------------");


    }
}
