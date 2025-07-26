package com.von.base.java8.example6;

import com.von.base.java8.example4.Dish;

import java.util.ArrayList;
import java.util.List;

import static com.von.base.java8.example4.StreamTest.menu;

/**
 * 收集器接口
 */
public class CollectorsInterfaceTest {

    public static void main(String[] args) {
        // Collector接口包含了一系列方法，为实现具体的归约操作（即收集器）提供了范本。
        // 我们已经看过了Collector接口中实现的许多收集器，例如toList或groupingBy。这也意味着，
        // 你可以为Collector接口提供自己的实现，从而自由地创建自定义归约操作。在6.6节中，我们
        // 将展示如何实现Collector接口来创建一个收集器，来比先前更高效地将数值流划分为质数和非
        // 质数。
        // 要开始使用Collector接口，我们先看看本章开始时讲到的一个收集器——toList工厂方
        // 法，它会把流中的所有元素收集成一个List。我们当时说在日常工作中经常会用到这个收集器，
        // 而且它也是写起来比较直观的一个，至少理论上如此。通过仔细研究这个收集器是怎么实现的，
        // 我们可以很好地了解Collector接口是怎么定义的，以及它的方法所返回的函数在内部是如何为
        // collect方法所用的。
        // 首先让我们在下面的列表中看看Collector接口的定义，它列出了接口的签名以及声明的五
        // 个方法。
        // Collector接口
        // public interface Collector<T, A, R> {
        //      1. 建立新的结果容器：supplier方法
        //      supplier方法必须返回一个结果为空的Supplier，也就是一个无参数函数，在调用时它会
        //      创建一个空的累加器实例，供数据收集过程使用。很明显，对于将累加器本身作为结果返回的收
        //      集器，比如我们的ToListCollector，在对空流执行操作的时候，这个空的累加器也代表了收集过程的结果
        //      Supplier<A> supplier();

        //      2. 将元素添加到结果容器：accumulator方法
        //      accumulator方法会返回执行归约操作的函数。当遍历到流中第n个元素时，这个函数执行
        //      时会有两个参数：保存归约结果的累加器（已收集了流中的前 n1 个项目），还有第n个元素本身。
        //      该函数将返回void，因为累加器是原位更新，即函数的执行改变了它的内部状态以体现遍历的
        //      元素的效果。
        //      BiConsumer<A, T> accumulator();

        //      3. 对结果容器应用最终转换：finisher方法
        //      在遍历完流后，finisher方法必须返回在累积过程的最后要调用的一个函数，
        //      以便将累加器对象转换为整个集合操作的最终结果。
        //      Function<A, R> finisher();

        //      4. 合并两个结果容器：combiner方法
        //      四个方法中的最后一个——combiner方法会返回一个供归约操作使用的函数，
        //      它定义了对流的各个子部分进行并行处理时，各个子部分归约所得的累加器要如何合并。
        //      BinaryOperator<A> combiner();

        //      5. characteristics方法
        //      最后一个方法——characteristics会返回一个不可变的Characteristics集合，它定义
        //      了收集器的行为——尤其是关于流是否可以并行归约，以及可以使用哪些优化的提示。
        //      Characteristics是一个包含三个项目的枚举。
        //       UNORDERED——归约结果不受流中项目的遍历和累积顺序的影响。
        //       CONCURRENT——accumulator函数可以从多个线程同时调用，且该收集器可以并行归
        //      约流。如果收集器没有标为UNORDERED，那它仅在用于无序数据源时才可以并行归约。
        //       IDENTITY_FINISH——这表明完成器方法返回的函数是一个恒等函数，可以跳过。
        //      这种情况下，累加器对象将会直接用作归约过程的最终结果。
        //      这也意味着，将累加器A不加检查地转换为结果R是安全的。List已经是我们要的最终结果，
        //      用不着进一步转换了，但它并不是UNORDERED，因为用在有序流上的时候，
        //      我们还是希望顺序能够保留在得到的List中。
        //      最后，它是CONCURRENT的，但我们刚才说过了，仅仅在背后的数据源无序时才会并行处理。
        //      我们迄今开发的ToListCollector是IDENTITY_FINISH的，因为用来累积流中元素的
        //      Set<Characteristics> characteristics();
        // }
        //  本列表适用以下定义。
        //   T是流中要收集的项目的泛型。
        //   A是累加器的类型，累加器是在收集过程中用于累积部分结果的对象。
        //   R是收集操作得到的对象（通常但并不一定是集合）的类型。
        //  例如，你可以实现一个ToListCollector<T>类，将Stream<T>中的所有元素收集到一个
        //  List<T>里，它的签名如下：
        //  public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
        //  我们很快就会澄清，这里用于累积的对象也将是收集过程的最终结果。


        // 进行自定义收集而不去实现Collector
        // 对于IDENTITY_FINISH的收集操作，还有一种方法可以得到同样的结果而无需从头实现新
        // 的Collectors接口。Stream有一个重载的collect方法可以接受另外三个函数——supplier、
        // accumulator和combiner，其语义和Collector接口的相应方法返回的函数完全相同。所以比
        // 如说，我们可以像下面这样把菜肴流中的项目收集到一个List中：
        List<Dish> dishes = menu.stream().collect(
                ArrayList::new,
                List::add,
                List::addAll);
        // 我们认为，这第二种形式虽然比前一个写法更为紧凑和简洁，却不那么易读。此外，以恰当
        // 的类来实现自己的自定义收集器有助于重用并可避免代码重复。另外值得注意的是，这第二个
        // collect方法不能传递任何Characteristics，所以它永远都是一个IDENTITY_FINISH和
        // CONCURRENT但并非UNORDERED的收集器。
    }

}
