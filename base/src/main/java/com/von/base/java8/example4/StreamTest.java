package com.von.base.java8.example4;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 流
 * 流到底是什么呢？
 * 简短的定义就是“从支持数据处理操作的源生成的元素序列”
 * 让我们一步步剖析这个定义。
 *  元素序列——就像集合一样，流也提供了一个接口，可以访问特定元素类型的一组有序
 * 值。因为集合是数据结构，所以它的主要目的是以特定的时间/空间复杂度存储和访问元
 * 素（如ArrayList 与 LinkedList）。但流的目的在于表达计算，比如你前面见到的
 * filter、sorted和map。集合讲的是数据，流讲的是计算。我们会在后面几节中详细解
 * 释这个思想。
 *  源——流会使用一个提供数据的源，如集合、数组或输入/输出资源。 请注意，从有序集
 * 合生成流时会保留原有的顺序。由列表生成的流，其元素顺序与列表一致。
 *  数据处理操作——流的数据处理功能支持类似于数据库的操作，以及函数式编程语言中
 * 的常用操作，如filter、map、reduce、find、match、sort等。流操作可以顺序执
 * 行，也可并行执行。
 * 此外，流操作有两个重要的特点。
 *  流水线——很多流操作本身会返回一个流，这样多个操作就可以链接起来，形成一个大
 * 的流水线。这让我们下一章中的一些优化成为可能，如延迟和短路。流水线的操作可以
 * 看作对数据源进行数据库式查询。
 *  内部迭代——与使用迭代器显式迭代的集合不同，流的迭代操作是在背后进行的。
 * 请注意，和迭代器类似，流只能遍历一次。遍历完之后，我们就说这个流已经被消费掉了。
 * 你可以从原始数据源那里再获得一个新的流来重新遍历一遍，就像迭代器一样（这里假设它是集
 * 合之类的可重复的源，如果是I/O通道就没戏了）。
 * 所以要记得，流只能消费一次！
 */
public class StreamTest {
    public static final List<Dish> menu = Arrays.asList(
            new Dish("pork", false, 800, Dish.Type.MEAT),
            new Dish("beef", false, 700, Dish.Type.MEAT),
            new Dish("chicken", false, 400, Dish.Type.MEAT),
            new Dish("french fries", true, 530, Dish.Type.OTHER),
            new Dish("rice", true, 350, Dish.Type.OTHER),
            new Dish("season fruit", true, 120, Dish.Type.OTHER),
            new Dish("pizza", true, 550, Dish.Type.OTHER),
            new Dish("prawns", false, 300, Dish.Type.FISH),
            new Dish("salmon", false, 450, Dish.Type.FISH));

    public static void main(String[] args) {

        // 数值流
        // 求最大值 - 此种方式包含案=暗装撤箱操作,如果数据量较大会存在性能问题
        // reduce 包含两个参数：一个是初始值， 一个是BinaryOperator函数接口

        Integer reduce = menu.stream().map(Dish::getCalories).reduce(0, Integer::max);
        System.out.println(reduce);

        // 原始类型流特化
        // Java 8引入了三个原始类型特化流接口来解决这个问题：
        // IntStream、DoubleStream和LongStream，
        // 分别将流中的元素特化为int、long和double，从而避免了暗含的装箱成本。
        // 每个接口都带来了进行常用数值归约的新方法，比如对数值流求和的sum，找到最大元素的max。
        // 此外还有在必要时再把它们转换回对象流的方法。
        // 要记住的是，这些特化的原因并不在于流的复杂性，
        // 而是装箱造成的复杂性——即类似int和Integer之间的效率差异。

        // 1. 映射到数值流
        // 将流转换为特化版本的常用方法是mapToInt、mapToDouble和mapToLong。
        // 这些方法和前面说的map方法的工作方式一样，只是它们返回的是一个特化流，而不是Stream<T>
        // 例如，你可以像下面这样用mapToInt对menu中的卡路里求和：
        // int calories = menu.stream().mapToInt(Dish::getCalories).sum();
        // 这里，mapToInt会从每道菜中提取热量（用一个Integer表示），
        // 并返回一个IntStream（而不是一个Stream<Integer>）。
        // 然后你就可以调用IntStream接口中定义的sum方法，对卡路里求和了！
        // 请注意，如果流是空的，sum默认返回0。IntStream还支持其他的方便方法，
        // 如max、min、average等。
        // 求和
        int sum = menu.stream().mapToInt(Dish::getCalories).sum();
        System.out.println(sum);
        // 2.转换回对象流
        // 同样，一旦有了数值流，你可能会想把它转换回非特化流。
        // 例如，IntStream上的操作只能产生原始整数： IntStream 的map 操作接受的Lambda 必须接受int 并返回int
        // （ 一个IntUnaryOperator）。但是你可能想要生成另一类值，比如Dish。
        // 为此，你需要访问Stream接口中定义的那些更广义的操作。
        // 要把原始流转换成一般流（每个int都会装箱成一个Integer），可以使用boxed方法，
        // 如下所示：
        IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
        Stream<Integer> stream = intStream.boxed();
        // 你在下一节中会看到，在需要将数值范围装箱成为一个一般流时，boxed尤其有用。

        // 3. 默认值OptionalInt
        // 求和的那个例子很容易，因为它有一个默认值：0。
        // 但是，如果你要计算IntStream中的最大元素，就得换个法子了，因为0是错误的结果。
        // 如何区分没有元素的流和最大值真的是0的流呢？
        // 前面我们介绍了Optional类，这是一个可以表示值存在或不存在的容器。Optional可以用
        // Integer、String等参考类型来参数化。对于三种原始流特化，也分别有一个Optional原始类
        // 型特化版本：OptionalInt、OptionalDouble和OptionalLong。
        // 例如，要找到IntStream中的最大元素，可以调用max方法，它会返回一个OptionalInt：
        // OptionalInt maxCalories = menu.stream()
        // .mapToInt(Dish::getCalories)
        // .max();
        // 现在，如果没有最大值的话，你就可以显式处理OptionalInt去定义一个默认值了：
        // int max = maxCalories.orElse(1);
        // 求最大值
        OptionalInt max = menu.stream().mapToInt(Dish::getCalories).max();
        System.out.println(max.orElse(-1));
        // 最小值
        OptionalInt min = menu.stream().mapToInt(Dish::getCalories).min();
        System.out.println(min.orElse(-1));
    }
}

