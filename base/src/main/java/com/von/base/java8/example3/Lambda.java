package com.von.base.java8.example3;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Lambda {
    public static void main(String[] args) {
        // 现在，只要知道Lambda表达式可以被赋给一个
        // 变量，或传递给一个接受函数式接口作为参数的方法就好了，当然这个Lambda表达式的签名要
        // 和函数式接口的抽象方法一样。
        String filePath = "d:\\testData.txt";
        String onelineContent = readFileContentProcess((path -> {
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(path, Charset.forName("GBK")))) {
                return bufferedReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }), filePath);
        // read one line content
        System.out.println(onelineContent);

        System.out.println("-------------------------------------------");

        String content = readFileContentProcess((path -> {
            StringBuilder sb = new StringBuilder();
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(path, Charset.forName("GBK")))) {
                while (bufferedReader.ready()) {
                    sb.append(bufferedReader.readLine()).append("\n");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return sb.toString();
        }), filePath);
        System.out.println(content);

        int phoneNumber = 15;
        // 对局部变量的限制
        // 你可能会问自己，为什么局部变量有这些限制。
        // 第一，实例变量和局部变量背后的实现有一个关键不同。实例变量都存储在堆中，
        // 而局部变量则保存在栈上。如果Lambda可以直接访问局部变量，
        // 而且Lambda是在一个线程中使用的，则使用Lambda的线程，
        // 可能会在分配该变量的线程将这个变量收回之后，去访问该变量。
        // 因此，Java在访问自由局部变量时，实际上是在访问它的副本，
        // 而不是访问原始变量。如果局部变量仅仅赋值一次那就没有什么区别了——因此就有了这个限制。
        // 第二，这一限制不鼓励你使用改变外部变量的典型命令式编程模式
        // （我们会在以后的各章中解释，这种模式会阻碍很容易做到的并行处理）。
        // 闭包
        // 你可能已经听说过闭包（closure，不要和Clojure编程语言混淆）这个词，
        // 你可能会想Lambda是否满足闭包的定义。用科学的说法来说，闭包就是一个函数的实例，
        // 且它可以无限制地访问那个函数的非本地变量。
        // 例如，闭包可以作为参数传递给另一个函数。它也可以访问和修改其作用域之外的变量。
        // 现在，Java 8的Lambda和匿名类可以做类似于闭包的事情：
        // 它们可以作为参数传递给方法，并且可以访问其作用域之外的变量。
        // 但有一个限制：它们不能修改定义Lambda的方法的局部变量的内容。
        // 这些变量必须是隐式最终的。可以认为Lambda是对值封闭，而不是对变量封闭。
        // 如前所述，这种限制存在的原因在于局部变量保存在栈上，并且隐式表示它们仅限于其所在线程。
        // 如果允许捕获可改变的局部变量，就会引发造成线程不安全的新的可能性，
        // 而这是我们不想看到的（实例变量可以，因为它们保存在堆中，而堆是在线程之间共享的）。
        Thread thread= new Thread(() -> {
//            Lambda表达式只能捕获指派给它们的局部变量一次。
//            （注：捕获实例变量可以被看作捕获最终局部变量this。
            System.out.println(phoneNumber); // lambda只能捕获变量一次，且不能修改，所以下一行语句会报错
//            phoneNumber = 123;
        });

        thread.start();

        // 如何构建方法引用
        // 方法引用主要有三类。
        // (1) 指向静态方法的方法引用（例如Integer的parseInt方法，写作Integer::parseInt）。
        // (2) 指向任意类型实例方法的方法引用（ 例如String 的length 方法， 写作String::length）。
        // (3) 指向现有对象的实例方法的方法引用
        // （假设你有一个局部变量expensiveTransaction用于存放Transaction类型的对象，
        // 它支持实例方法getValue，那么你就可以写expensiveTransaction::getValue）。
        // 第二种和第三种方法引用可能乍看起来有点儿晕。
        // 类似于String::length的第二种方法引用的思想就是你在引用一个对象的方法，
        // 而这个对象本身是Lambda的一个参数。
        // 例如，Lambda表达式(String s) -> s.toUppeCase()可以写作String::toUpperCase。
        // 但第三种方法引用指的是，你在Lambda中调用一个已经存在的外部对象中的方法。
        // 例如，Lambda表达式
        // ()->expensiveTransaction.getValue()可以写作expensiveTransaction::getValue。


    }

    // 原始方法，改造为行为参数化的lambda方式
    // () -> void
    // 参数列表 连接符 主体 异常处理列表
    // 定义一个函数式接口，要求参数为一个文件路径字符串, 返回文件内容
    private String processFile(String filePath) {
        try(BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static String readFileContentProcess(ReadFileContentFace face, String filePath) {
        return face.read(filePath);
    }

    // 使用函数式接口
    // 函数式接口定义且只定义了一个抽象方法。函数式接口很有用，
    // 因为抽象方法的签名可以描述Lambda表达式的签名。函数式接口的抽象方法的签名称为函数描
    // 述符。所以为了应用不同的Lambda表达式，你需要一套能够描述常见函数描述符的函数式接口。
    // Java API中已经有了几个函数式接口.

    // Java 8中的常用函数式接口
    // 函数式接口            函数描述符               原始类型特化
    // Predicate<T>          T->boolean               IntPredicate,LongPredicate, DoublePredicate
    // Consumer<T>           T->void                  IntConsumer,LongConsumer, DoubleConsumer
    // Function<T,R>         T->R                     IntFunction<R>,IntToDoubleFunction,IntToLongFunction,LongFunction<R>,LongToDoubleFunction,LongToIntFunction,DoubleFunction<R>,ToIntFunction<T>,ToDoubleFunction<T>,ToLongFunction<T>
    // Supplier<T>           ()->T                    BooleanSupplier,IntSupplier, LongSupplier,DoubleSupplier
    // UnaryOperator<T>      T->T                     IntUnaryOperator,LongUnaryOperator,DoubleUnaryOperator
    // BinaryOperator<T>     (T,T)->T                 IntBinaryOperator,LongBinaryOperator,DoubleBinaryOperator
    // BiPredicate<L,R>      (L,R)->boolean
    // BiConsumer<T,U>       (T,U)->void              ObjIntConsumer<T>,ObjLongConsumer<T>,ObjDoubleConsumer<T>
    // BiFunction<T,U,R>     (T,U)->R                 ToIntBiFunction<T,U>,ToLongBiFunction<T,U>,ToDoubleBiFunction<T,U>

    @FunctionalInterface
    interface ReadFileContentFace{
        String read(String filePath);
    }


}
