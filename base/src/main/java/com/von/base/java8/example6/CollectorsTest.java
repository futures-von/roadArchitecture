package com.von.base.java8.example6;

import com.von.base.java8.example4.Dish;

import java.util.*;

import static com.von.base.java8.example4.StreamTest.menu;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

/**
 * 收集和规约
 */
public class CollectorsTest {

    public static void main(String[] args) {
        // 用reducing连接字符串
        // 以下哪一种reducing收集器的用法能够合法地替代joining收集器（如6.2.3节用法）？
        // String shortMenu = menu.stream().map(Dish::getName).collect(joining());

        // (1)
        String shortMenu = menu.stream().map(Dish::getName)
                .collect(reducing((s1, s2) -> s1 + s2)).get();

        // (2)
//        shortMenu = menu.stream()
//                .collect(reducing((d1, d2) -> d1.getName() + d2.getName())).get();

        // (3)
        shortMenu = menu.stream()
                .collect(reducing("", Dish::getName, (s1, s2) -> s1 + s2));

        // 答案：语句1和语句3是有效的，语句2无法编译。
        // (1) 这会将每道菜转换为菜名，就像原先使用joining收集器的语句一样。然后用一个
        // String作为累加器归约得到的字符串流，并将菜名逐个连接在它后面。
        // (2) 这无法编译，因为reducing接受的参数是一个BinaryOperator<t>，也就是一个
        // BiFunction<T,T,T>。这就意味着它需要的函数必须能接受两个参数，然后返回一个相同类
        // 型的值，但这里用的Lambda表达式接受的参数是两个菜，返回的却是一个字符串。
        // (3) 这会把一个空字符串作为累加器来进行归约，在遍历菜肴流时，它会把每道菜转换成
        // 菜名，并追加到累加器上。请注意，我们前面讲过，reducing要返回一个Optional并不需
        // 要三个参数，因为如果是空流的话，它的返回值更有意义——也就是作为累加器初始值的空字
        // 符串。
        // 请注意，虽然语句1和语句3都能够合法地替代joining收集器，它们在这里是用来展示我
        // 们为何可以（至少在概念上）把reducing看作本章中讨论的所有其他收集器的概括。然而就
        // 实际应用而言，不管是从可读性还是性能方面考虑，我们始终建议使用joining收集器。

        // 分组
        Map<Dish.Type, List<Dish>> dishesByType =
                menu.stream().collect(groupingBy(Dish::getType));


        // 多级分组
        // 要实现多级分组，我们可以使用一个由双参数版本的Collectors.groupingBy工厂方法创建的收集器，
        // 它除了普通的分类函数之外，还可以接受collector类型的第二个参数。
        // 那么要进行二级分组的话，我们可以把一个内层groupingBy传递给外层groupingBy，
        // 并定义一个为流中项目分类的二级标准。

        Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel =
                menu.stream().collect(
                        groupingBy(Dish::getType,
                                groupingBy(dish -> {
                                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                                    else return CaloricLevel.FAT;
                                })
                        )
                );

        // 按子组收集数据
        // 在上一节中，我们看到可以把第二个groupingBy收集器传递给外层收集器来实现多级分组。
        // 但进一步说，传递给第一个groupingBy的第二个收集器可以是任何类型，
        // 而不一定是另一个groupingBy。
        // 例如，要数一数菜单中每类菜有多少个，
        // 可以传递counting收集器作为groupingBy收集器的第二个参数：
        Map<Dish.Type, Long> typesCount = menu.stream().collect(
                groupingBy(Dish::getType, counting()));

        // 还要注意，普通的单参数groupingBy(f)（其中f是分类函数）实际上是groupingBy(f, toList())的简便写法。
        // 再举一个例子，你可以把前面用于查找菜单中热量最高的菜肴的收集器改一改，按照菜的类型分类：
        Map<Dish.Type, Optional<Dish>> mostCaloricByType =
                menu.stream()
                        .collect(groupingBy(Dish::getType,
                                maxBy(comparingInt(Dish::getCalories))));
        // 这个分组的结果显然是一个map，以Dish的类型作为键，以包装了该类型中热量最高的Dish的Optional<Dish>作为值：
        // {FISH=Optional[salmon], OTHER=Optional[pizza], MEAT=Optional[pork]}

        // 注意 这个Map中的值是Optional，因为这是maxBy工厂方法生成的收集器的类型，但实际上，
        // 如果菜单中没有某一类型的Dish，这个类型就不会对应一个Optional. empty()值，
        // 而且根本不会出现在Map的键中。groupingBy收集器只有在应用分组条件后，第一次在
        // 流中找到某个键对应的元素时才会把键加入分组Map中。这意味着Optional包装器在这
        // 里不是很有用，因为它不会仅仅因为它是归约收集器的返回类型而表达一个最终可能不
        // 存在却意外存在的值。
        // 1. 把收集器的结果转换为另一种类型
        // 因为分组操作的Map结果中的每个值上包装的Optional没什么用，所以你可能想要把它们去掉。
        // 要做到这一点，或者更一般地来说，把收集器返回的结果转换为另一种类型，
        // 你可以使用Collectors.collectingAndThen工厂方法返回的收集器，
        // 如下所示。
        Map<Dish.Type, Dish> dishMap = menu.stream()
                .collect(groupingBy(Dish::getType,
                        collectingAndThen(
                                maxBy(comparingInt(Dish::getCalories)),
                                Optional::get)));

        // 这个工厂方法接受两个参数——要转换的收集器以及转换函数，并返回另一个收集器。
        // 这个收集器相当于旧收集器的一个包装，
        // collect操作的最后一步就是将返回值用转换函数做一个映射。
        // 在这里，被包起来的收集器就是用maxBy建立的那个，
        // 而转换函数Optional::get则把返回的Optional中的值提取出来。
        // 前面已经说过，这个操作放在这里是安全的，
        // 因为reducing收集器永远都不会返回Optional.empty()。
        // 其结果是下面的Map：
        // {FISH=salmon, OTHER=pizza, MEAT=pork}
        // 把好几个收集器嵌套起来很常见，它们之间到底发生了什么可能不那么明显。图6-6可以直
        // 观地展示它们是怎么工作的。从最外层开始逐层向里，注意以下几点。
        //  收集器用虚线表示，因此groupingBy是最外层，根据菜肴的类型把菜单流分组，得到三
        // 个子流。
        //  groupingBy收集器包裹着collectingAndThen收集器，因此分组操作得到的每个子流
        // 都用这第二个收集器做进一步归约。
        //  collectingAndThen收集器又包裹着第三个收集器maxBy。
        //  随后由归约收集器进行子流的归约操作，然后包含它的collectingAndThen收集器会对
        // 其结果应用Optional:get转换函数。
        //  对三个子流分别执行这一过程并转换而得到的三个值，也就是各个类型中热量最高的
        // Dish，将成为groupingBy收集器返回的Map中与各个分类键（Dish的类型）相关联的值。
        // 2. 与groupingBy联合使用的其他收集器的例子
        // 一般来说，通过groupingBy工厂方法的第二个参数传递的收集器将会对分到同一组中的所
        // 有流元素执行进一步归约操作。例如，你还重用求出所有菜肴热量总和的收集器，不过这次是对
        // 每一组Dish求和：
        Map<Dish.Type, Integer> totalCaloriesByType =
                menu.stream().collect(groupingBy(Dish::getType,
                        summingInt(Dish::getCalories)));

        // 然而常常和groupingBy联合使用的另一个收集器是mapping方法生成的。这个方法接受两
        // 个参数：一个函数对流中的元素做变换，另一个则将变换的结果对象收集起来。其目的是在累加
        // 之前对每个输入元素应用一个映射函数，这样就可以让接受特定类型元素的收集器适应不同类型
        // 的对象。我们来看一个使用这个收集器的实际例子。比方说你想要知道，对于每种类型的Dish，
        // 菜单中都有哪些CaloricLevel。我们可以把groupingBy和mapping收集器结合起来，如下所示：
        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
                menu.stream().collect(
                        groupingBy(Dish::getType, mapping(
                                dish -> {
                                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                                    else return CaloricLevel.FAT;
                                },
                                toSet())));

        // 这里，就像我们前面见到过的，传递给映射方法的转换函数将Dish映射成了它的
        // CaloricLevel：生成的CaloricLevel流传递给一个toSet收集器，它和toList类似，不过是
        // 把流中的元素累积到一个Set而不是List中，以便仅保留各不相同的值。如先前的示例所示，这
        // 个映射收集器将会收集分组函数生成的各个子流中的元素，让你得到这样的Map结果：
        // {OTHER=[DIET, NORMAL], MEAT=[DIET, NORMAL, FAT], FISH=[DIET, NORMAL]}
        // 由此你就可以轻松地做出选择了。如果你想吃鱼并且在减肥，那很容易找到一道菜；同样，
        // 如果你饥肠辘辘，想要很多热量的话，菜单中肉类部分就可以满足你的饕餮之欲了。请注意在上
        // 一个示例中，对于返回的Set是什么类型并没有任何保证。但通过使用toCollection，你就可
        // 以有更多的控制。例如，你可以给它传递一个构造函数引用来要求HashSet：
        Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType1 =
                menu.stream().collect(
                        groupingBy(Dish::getType, mapping(
                                dish -> {
                                    if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                                    else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                                    else return CaloricLevel.FAT;
                                },
                                toCollection(HashSet::new))));

        // 分区
        // 分区是分组的特殊情况：由一个谓词（返回一个布尔值的函数）作为分类函数，它称分区函
        // 数。分区函数返回一个布尔值，这意味着得到的分组Map的键类型是Boolean，于是它最多可以
        // 分为两组——true是一组，false是一组。例如，如果你是素食者或是请了一位素食的朋友来共
        // 进晚餐，可能会想要把菜单按照素食和非素食分开：
        Map<Boolean, List<Dish>> partitionedMenu =
                menu.stream().collect(partitioningBy(Dish::isVegetarian));
        // 这会返回下面的Map：
        // {false=[pork, beef, chicken, prawns, salmon], true=[french fries, rice, season fruit, pizza]}
        // 那么通过Map中键为true的值，就可以找出所有的素食菜肴了：
        // List<Dish> vegetarianDishes = partitionedMenu.get(true);
        // 请注意，用同样的分区谓词，对菜单List创建的流作筛选，
        // 然后把结果收集到另外一个List中也可以获得相同的结果：
        List<Dish> vegetarianDishes =
                menu.stream().filter(Dish::isVegetarian).collect(toList());

        // 分区的优势
        // 分区的好处在于保留了分区函数返回true或false的两套流元素列表。在上一个例子中，要
        // 得到非素食Dish的List，你可以使用两个筛选操作来访问partitionedMenu这个Map中false
        // 键的值：一个利用谓词，一个利用该谓词的非。而且就像你在分组中看到的，partitioningBy
        // 工厂方法有一个重载版本，可以像下面这样传递第二个收集器：
        Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType =
                menu.stream().collect(
                        partitioningBy(Dish::isVegetarian,
                                groupingBy(Dish::getType)));
        // 这将产生一个二级Map：
        // {false={FISH=[prawns, salmon], MEAT=[pork, beef, chicken]}, true={OTHER=[french fries, rice, season fruit, pizza]}}

        // 这里，对于分区产生的素食和非素食子流，分别按类型对菜肴分组，得到了一个二级Map，
        // 和6.3.1节的二级分组得到的结果类似。再举一个例子，你可以重用前面的代码来找到素食和非素
        // 食中热量最高的菜：
        Map<Boolean, Dish> mostCaloricPartitionedByVegetarian =
                menu.stream().collect(
                        partitioningBy(Dish::isVegetarian,
                                collectingAndThen(
                                        maxBy(comparingInt(Dish::getCalories)),
                                        Optional::get)));
        // 这将产生以下结果：
        // {false=pork, true=pizza}
        // 我们在本节开始时说过， 你可以把分区看作分组一种特殊情况。
        // groupingBy 和 partitioningBy收集器之间的相似之处并不止于此；
        // 你在下一个测验中会看到，还可以按照和6.3.1节中分组类似的方式进行多级分区。

        // 现在我们已经讨论过了Collectors类的静态工厂方法能够创建的所有收集器，并介绍了使
        // 用它们的实际例子。表6-1将它们汇总到一起，给出了它们应用到Stream<T>上返回的类型，以
        // 及它们用于一个叫作menuStream的Stream<Dish>上的实际例子。
        // 表6-1 Collectors类的静态工厂方法
        // 工厂方法             返回类型               用 于
        // toList               List<T>                把流中所有项目收集到一个List
        // 使用示例：List<Dish> dishes = menuStream.collect(toList());

        // toSet                Set<T>                 把流中所有项目收集到一个Set，删除重复项
        // 使用示例：Set<Dish> dishes = menuStream.collect(toSet());

        // toCollection         Collection<T>          把流中所有项目收集到给定的供应源创建的集合
        // 使用示例：Collection<Dish> dishes = menuStream.collect(toCollection(), ArrayList::new);

        // counting             Long                   计算流中元素的个数
        // 使用示例：long howManyDishes = menuStream.collect(counting());

        // summingInt           Integer                对流中项目的一个整数属性求和
        // 使用示例：int totalCalories = menuStream.collect(summingInt(Dish::getCalories));

        // averagingInt         Double                 计算流中项目Integer 属性的平均值
        // 使用示例：double avgCalories = menuStream.collect(averagingInt(Dish::getCalories));

        // summarizingInt       IntSummaryStatistics   收集关于流中项目Integer 属性的统计值，例如最大、最小、总和与平均值
        // 使用示例：IntSummaryStatistics menuStatistics = menuStream.collect(summarizingInt(Dish::getCalories));

        // joining              String                  连接对流中每个项目调用toString 方法所生成的字符串
        // 使用示例：String shortMenu = menuStream.map(Dish::getName).collect(joining(", "));

        // maxBy                Optional<T>             一个包裹了流中按照给定比较器选出的最大元素的Optional，或如果流为空则为Optional.empty()
        // 使用示例：Optional<Dish> fattest = menuStream.collect(maxBy(comparingInt(Dish::getCalories)));

        // minBy                Optional<T>             一个包裹了流中按照给定比较器选出的最小元素的Optional，或如果流为空则为Optional.empty()
        // 使用示例：Optional<Dish> lightest = menuStream.collect(minBy(comparingInt(Dish::getCalories)));

        // reducing             归约操作产生的类型      从一个作为累加器的初始值开始，利用BinaryOperator 与流中的元素逐个结合，从而将流归约为单个值
        // 使用示例：int totalCalories = menuStream.collect(reducing(0, Dish::getCalories, Integer::sum));

        // collectingAndThen    转换函数返回的类型 包裹另一个收集器，对其结果应用转换函数
        // 使用示例：int howManyDishes = menuStream.collect(collectingAndThen(toList(), List::size));

        // groupingBy           Map<K, List<T>>         根据项目的一个属性的值对流中的项目作问组，并将属性值作为结果Map的键
        // 使用示例：Map<Dish.Type,List<Dish>> dishesByType = menuStream.collect(groupingBy(Dish::getType));

        // partitioningBy       Map<Boolean,List<T>>    根据对流中每个项目应用谓词的结果来对项目进行分区
        // 使用示例：Map<Boolean,List<Dish>> vegetarianDishes = menuStream.collect(partitioningBy(Dish::isVegetarian));

        // 本章开头提到过，所有这些收集器都是对Collector接口的实现，
        // 因此我们会在本章剩余部分中详细讨论这个接口。
        // 我们会看看这个接口中的方法，然后探讨如何实现你自己的收集器。
    }

    public enum CaloricLevel {DIET, NORMAL, FAT}
}
