package com.von.base.java8.example1;

import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public class FunctionInterface {

    public static final List<Apple> appleList = new ArrayList<>();

    static {
        appleList.add(new Apple("green", 12.5));
        appleList.add(new Apple("red", 8.5));
        appleList.add(new Apple("yellow", 6.5));
        appleList.add(new Apple("green", 9.2));
        appleList.add(new Apple("red", 9.2));
        appleList.add(new Apple("yellow", 9.2));
        appleList.add(new Apple("red", 9.2));
        appleList.add(new Apple("green", 11.3));
    }

    public static void main(String[] args) {



        List<Apple> greenList = new ArrayList<>();
        for (Apple apple : appleList) {
            if (apple.getColor().equalsIgnoreCase("green")) {
                greenList.add(apple);
            }
        }

        greenList.forEach(System.out::println);

        System.out.println("----------------------------------");

        List<Apple> appleList1 = filterApple(greenList, FunctionInterface::chooseColor);
        appleList1.forEach(System.out::println);
        System.out.println("---------------------------------- 基于策略模式，你需要实现很多的策略类");
        List<Apple> appleList2 = filter(appleList, new AppleGreenColorPredicate());
        appleList2.forEach(System.out::println);
        List<Apple> appleList3 = filter(appleList, new AppleHeavyWeightPredicate());
        appleList3.forEach(System.out::println);
        System.out.println("---------------------------------- 基于匿名类的方式");

        List<Apple> appleList4 = filter(appleList, new ApplePredicate<Apple>() {
            @Override
            public boolean test(Apple apple) {
                return apple.getWeight() > 8;
            }
        });
        appleList4.forEach(System.out::println);

        System.out.println("---------------------------------- 基于lambda的方式");

        List<Apple> appleList5 = filter(appleList, (apple) -> apple.getColor().equalsIgnoreCase("green"));
        appleList5.forEach(System.out::println);
        // 行为参数化就是可以帮助你处理频繁变更的需求的一种软件开发模式。一言以蔽之，它意味
        //着拿出一个代码块，把它准备好却不去执行它。这个代码块以后可以被你程序的其他部分调用，
        //这意味着你可以推迟这块代码的执行。例如，你可以将代码块作为参数传递给另一个方法，稍后
        //再去执行它。这样，这个方法的行为就基于那块代码被参数化了。例如，如果你要处理一个集合，
        //可能会写一个方法：
        // 可以对列表中的每个元素做“某件事”
        // 可以在列表处理完后做“另一件事”
        // 遇到错误时可以做“另外一件事”
        //行为参数化说的就是这个
    }

    static class Apple {
        private String color;
        private double weight;

        public Apple(String color, double weight) {
            this.color = color;
            this.weight = weight;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Apple{" +
                    "color='" + color + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }

    interface Predicate<T> {
        Boolean test(T t);
    }

    static boolean chooseColor(Apple apple) {
        return apple.getColor().equalsIgnoreCase("green");
    }

    static boolean gtWeight(Apple apple) {
        return apple.getWeight() > 150;
    }

    static List<Apple> filterApple(List<Apple> appleList, Predicate<Apple> p) {
        List<Apple> apples = new ArrayList<>();
        for (Apple apple : appleList) {
            if (p.test(apple)) {
                apples.add(apple);
            }
        }
        return apples;
    }

    interface ApplePredicate<T> {
        boolean test(T t);
    }


    static class AppleHeavyWeightPredicate implements ApplePredicate<Apple> {
        public boolean test(Apple apple) {
            return apple.getWeight() > 150;
        }
    }

    static class AppleGreenColorPredicate implements ApplePredicate<Apple> {
        public boolean test(Apple apple) {
            return "green".equals(apple.getColor());
        }
    }

    static List<Apple> filter1(List<Apple> apples, ApplePredicate<Apple> predicate) {
        List<Apple> results = new ArrayList<>();
        for (Apple apple : apples) {
            if (predicate.test(apple)) {
                results.add(apple);
            }
        }
        return results;
    }

    /**
     * 继续泛化
     *
     * @param lists
     * @param predicate
     * @param <T>
     * @return
     */
    static <T> List<T> filter(List<T> lists, ApplePredicate<T> predicate) {
        List<T> results = new ArrayList<>();
        for (T t : lists) {
            if (predicate.test(t)) {
                results.add(t);
            }
        }
        return results;
    }


}