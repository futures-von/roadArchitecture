package com.von.base.java8.example12;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

/**
 * java8 新版本的日期函数使用
 */
public class DateApi {
    public static void main(String[] args) {
        // 1、--------以下主要是从一个不可变的瞬时对象中获取，对应瞬时的取值
        // 瞬时包含一个接口TemporalField，通过其实现类可以获的对应的瞬时对象
        // 这个类无法表示日期，只能以毫秒的精度表示时间。
        // 更糟糕的是它的易用性，由于某些原因未知的设计决策，这个类的易用性被深深地损害了。
        // 比如：年份的起始选择是1900年，月份的起始从0开始。
        // 这意味着，如果你想要用Date表示Java 8的发布日期，即2014年3月18日，需要创建下面
        // 这样的Date实例
        Date date = new Date(114, 2, 18);

        System.out.println(date);

        // ptint is : Tue Mar 18 00:00:00 CST 2014

        // java8 新的日期时间对象，切不可变。
        // LocalDate、LocalTime、Instant、Duration 以及Period
        System.out.println(LocalDate.now());

        // 通过指定日期实例化LocalDate
        LocalDate localDate = LocalDate.of(2014, 3, 18);
        // print 当前日期
        System.out.println("today is : " + localDate);
        // 获取当前日期的年份
        System.out.printf("year is: %d", localDate.getYear()).println();
        // 获取当前日期的月份英文
        System.out.printf("month is: %s", localDate.getMonth()).println();
        // 获取当前日期的月份的阿拉伯数字
        System.out.printf("month is: %d", localDate.getMonth().getValue()).println();
        // 获取当前日期的所在月份的天数
        System.out.printf("day is: %d", localDate.getDayOfMonth()).println();
        // 获取当前日期的对应星期的英文
        System.out.printf("day of week is: %s", localDate.getDayOfWeek()).println();
        // 获取当前日期的对应星期的阿拉伯数字
        System.out.printf("day of week is: %d", localDate.getDayOfWeek().getValue()).println();
        // 获取当前日期的对应月份的最后一天
        System.out.printf("the month end day is: %d", localDate.lengthOfMonth()).println();
        // 获取当前年份是否为闰年
        System.out.printf("the month end day is: %b", localDate.isLeapYear()).println();
        // 获取当前时间
        System.out.println(LocalTime.now());
        LocalTime localTime = LocalTime.of(18, 22, 36, 999876123);
        System.out.println(localTime);
        // 是否在当前时间之后
        System.out.println(localTime.isAfter(LocalTime.now()));
        // 是否在当前时间之前
        System.out.println(localTime.isBefore(LocalTime.now()));


        // 传递一个TemporalField参数给get方法拿到同样的信息。
        // TemporalField是一个接口，它定义了如何访问temporal对象某个字段的值。temporal（瞬时的）
        // ChronoField枚举实现了这一接口，所以你可以很方便地使用get方法得到枚举元素的值，如下所示。
        System.out.println(localTime.get(ChronoField.HOUR_OF_DAY));

        // LocalDate和LocalTime都可以通过解析代表它们的字符串创建。
        // 使用静态方法parse，你可以实现这一目的：
        LocalDate parseDate = LocalDate.parse("2014-03-18");
        LocalTime time = LocalTime.parse("13:45:20");
        System.out.println(parseDate + " " + time);

//        LocalDate date1 = LocalDate.parse("20240318"); java.time.format.DateTimeParseException
//        System.out.println(date1);

        // 合并日期和时间，不带有时区信息，包含纳秒
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);

//        LocalDate localDate1 = localDateTime.toLocalDate();
//        LocalTime localTime1 = localDateTime.toLocalTime();

        // 机器的日期和时间格式, Instant用于表示机器识别的方式
        // 作为人，我们习惯于以星期几、几号、几点、几分这样的方式理解日期和时间。毫无疑问，
        // 这种方式对于计算机而言并不容易理解。从计算机的角度来看，建模时间最自然的格式是表示一
        // 个持续时间段上某个点的单一大整型数。这也是新的java.time.Instant类对时间建模的方
        // 式，基本上它是以Unix元年时间（传统的设定为UTC时区1970年1月1日午夜时分）开始所经历的
        // 秒数进行计算。
//        Instant.ofEpochMilli()
        System.out.println(Instant.ofEpochSecond(3).getEpochSecond());
        System.out.println(Instant.ofEpochSecond(3).toEpochMilli());
        System.out.println(Instant.ofEpochSecond(3).getNano());
        System.out.println(Instant.ofEpochSecond(3, 0));
        System.out.println(Instant.ofEpochSecond(2, 1_000_000_000));
        System.out.println(Instant.ofEpochSecond(4, -1_000_000_000));
        System.out.println(Instant.now().getEpochSecond());

        // 正如你已经在LocalDate及其他为便于阅读而设计的日期时间类中所看到的那样，
        // Instant类也支持静态工厂方法now，它能够帮你获取当前时刻的时间戳。我们想要特别强调一
        // 点，Instant的设计初衷是为了便于机器使用。它包含的是由秒及纳秒所构成的数字。所以，它
        // 无法处理那些我们非常容易理解的时间单位。
        // 由于LocalDateTime和Instant是为不同的目的而设计的，一个是为了便于人阅读使用，
        // 另一个是为了便于机器处理，所以你不能将二者混用。

        // 定义Duration-持续的，用于对人类友好的，只能处理秒和纳秒
        // 此外，由于Duration类主要用于以秒和纳秒衡量时间的长短，你不能仅向between方法传递一个LocalDate对象做参数。
        // Period-阶段，只能处理日期相关的。
        // 如果你需要以年、月或者日的方式对多个时间单位建模，可以使用Period类。
        // 使用该类的工厂方法between，你可以使用得到两个LocalDate之间的时长，

        LocalTime t1 = LocalTime.of(12, 32, 55);
        LocalTime t2 = LocalTime.of(12, 42, 15);
        Duration duration = Duration.between(t1, t2);
        System.out.println(duration.getSeconds());
        System.out.println(duration.negated());

        Duration duration1 = Duration.between(Instant.now(), Instant.now());
        System.out.println(duration1);

        Period between = Period.between(LocalDate.of(2025, 4, 22), LocalDate.of(2025, 4, 27));
        System.out.println(between.getDays());

//        between       是 创建两个时间点之间的interval
//        from          是 由一个临时时间点创建interval
//        of            是 由它的组成部分创建interval 的实例
//        parse         是 由字符串创建interval 的实例
//        addTo         否 创建该interval 的副本，并将其叠加到某个指定的temporal 对象
//        get           否 读取该interval 的状态
//        isNegative    否 检查该interval 是否为负值，不包含零
//        isZero        否 检查该interval 的时长是否为零
//        minus         否 通过减去一定的时间创建该interval 的副本
//        multipliedBy  否 将interval 的值乘以某个标量创建该interval 的副本
//        negated       否 以忽略某个时长的方式创建该interval 的副本
//        plus          否 以增加某个指定的时长的方式创建该interval 的副本
//        subtractFrom  否 从指定的temporal 对象中减去该interval


        // 2、--------操纵、解析和格式化日期
        // 如果你已经有一个LocalDate对象，想要创建它的一个修改版，最直接也最简单的方法是使
        // 用withAttribute方法。withAttribute方法会创建对象的一个副本，并按照需要修改它的属
        // 性。注意，下面的这段代码中所有的方法都返回一个修改了属性的对象。它们都不会修改原来的
        // 对象！
        // 以比较直观的方式操纵LocalDate的属性
        LocalDate date1 = LocalDate.of(2014, 3, 18);
        System.out.println(date1);
        LocalDate date2 = date1.withYear(2011);
        System.out.println(date2);
        LocalDate date3 = date2.withDayOfMonth(25);
        System.out.println(date3);
        LocalDate date4 = date3.with(ChronoField.MONTH_OF_YEAR, 9);
        System.out.println(date4);

        System.out.println();

        // 以相对方式修改LocalDate对象的属性
        LocalDate date11 = LocalDate.of(2014, 3, 18);
        System.out.println(date11);
        LocalDate date22 = date11.plusWeeks(1); // 加法
        System.out.println(date22);
        LocalDate date33 = date22.minusYears(3); // 减法
        System.out.println(date33);
        LocalDate date44 = date33.plus(6, ChronoUnit.MONTHS);
        System.out.println(date44);

//        from      是 依据传入的Temporal 对象创建对象实例
//        now       是 依据系统时钟创建Temporal 对象
//        of        是 由Temporal 对象的某个部分创建该对象的实例
//        parse     是 由字符串创建Temporal 对象的实例
//        atOffset  否 将Temporal 对象和某个时区偏移相结合
//        atZone    否 将Temporal 对象和某个时区相结合
//        format    否 使用某个指定的格式器将Temporal 对象转换为字符串（Instant 类不提供该方法）
//        get       否 读取Temporal 对象的某一部分的值
//        minus     否 创建Temporal 对象的一个副本，通过将当前Temporal 对象的值减去一定的时长创建该副本
//        plus      否 创建Temporal 对象的一个副本，通过将当前Temporal 对象的值加上一定的时长创建该副本
//        with      否 以该Temporal 对象为模板，对某些状态进行修改创建该对象的副本

//        使用TemporalAdjuster
//        TemporalAdjuster类中的工厂方法
//        dayOfWeekInMonth          创建一个新的日期，它的值为同一个月中每一周的第几天
//        firstDayOfMonth           创建一个新的日期，它的值为当月的第一天
//        firstDayOfNextMonth       创建一个新的日期，它的值为下月的第一天
//        firstDayOfNextYear        创建一个新的日期，它的值为明年的第一天
//        firstDayOfYear            创建一个新的日期，它的值为当年的第一天
//        firstInMonth              创建一个新的日期，它的值为同一个月中，第一个符合星期几要求的值
//        lastDayOfMonth            创建一个新的日期，它的值为当月的最后一天
//        lastDayOfNextMonth        创建一个新的日期，它的值为下月的最后一天
//        lastDayOfNextYear         创建一个新的日期，它的值为明年的最后一天
//        lastDayOfYear             创建一个新的日期，它的值为今年的最后一天
//        lastInMonth               创建一个新的日期，它的值为同一个月中，最后一个符合星期几要求的值
//        next/previous             创建一个新的日期，并将其值设定为日期调整后或者调整前，第一个符合指定星期几要求的日期
//        nextOrSame/previousOrSame 创建一个新的日期，并将其值设定为日期调整后或者调整前，第一个符合指定星期几要求的日期，如果该日期已经符合要求，直接返回该对象

        // example

        LocalDate lastDate = LocalDate.of(2025, 4, 5);
        System.out.println(lastDate.with(nextOrSame(DayOfWeek.SUNDAY)));
        System.out.printf("last month: %s \n", lastDate.with(lastDayOfMonth()));

//        你大概会希望在你代码的多个地方使用同样的方式去操作日期，为了达到这一目的，我们
//        建议你像我们的示例那样将它的逻辑封装到一个类中。对于你经常使用的操作，都应该采用类
//        似的方式，进行封装。最终，你会创建自己的类库，让你和你的团队能轻松地实现代码复用。
//        如果你想要使用Lambda表达式定义TemporalAdjuster对象，推荐使用Temporal-Adjusters类的静态工厂方法ofDateAdjuster，
//        它接受一个UnaryOperator<LocalDate>
//        正常情况，增加1天读取当前日期增加恰当的天数后，返回修改的日期如果当天是周六，增加2天如果当天是周 五，增加3天
//        类型的参数，代码如下：
//        TemporalAdjuster nextWorkingDay = TemporalAdjusters.ofDateAdjuster(
//                temporal -> {
//                    DayOfWeek dow =
//                            DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
//                    int dayToAdd = 1;
//                    if (dow == DayOfWeek.FRIDAY) dayToAdd = 3;
//                    if (dow == DayOfWeek.SATURDAY) dayToAdd = 2;
//                    return temporal.plus(dayToAdd, ChronoUnit.DAYS);
//                });
//        date = date.with(nextWorkingDay);

//        3、--------打印输出及解析日期时间对象
//        处理日期和时间对象时，格式化以及解析日期时间对象是另一个非常重要的功能。新的
//      java.time.format包就是特别为这个目的而设计的。这个包中，最重要的类是DateTime-
//      Formatter。创建格式器最简单的方法是通过它的静态工厂方法以及常量。像BASIC_ISO_DATE
//      和ISO_LOCAL_DATE 这样的常量是DateTimeFormatter 类的预定义实例。所有的
//  DateTimeFormatter实例都能用于以一定的格式创建代表特定日期或时间的字符串。比如，下
//  面的这个例子中，我们使用了两个不同的格式器生成了字符串：
        LocalDate dateNew = LocalDate.of(2014, 3, 18);
        String s1 = dateNew.format(DateTimeFormatter.BASIC_ISO_DATE);
        String s2 = dateNew.format(DateTimeFormatter.ISO_LOCAL_DATE);

        System.out.printf("format date is %s, %s \n", s1, s2);
        // 你也可以通过解析代表日期或时间的字符串重新创建该日期对象。所有的日期和时间API
        //都提供了表示时间点或者时间段的工厂方法，你可以使用工厂方法parse达到重创该日期对象
        //的目的：
        LocalDate date5 = LocalDate.parse("20140318", DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate date6 = LocalDate.parse("2014-03-18", DateTimeFormatter.ISO_LOCAL_DATE);
        //和老的java.util.DateFormat相比较，所有的DateTimeFormatter实例都是线程安全
        //的。所以，你能够以单例模式创建格式器实例，就像DateTimeFormatter所定义的那些常量，
        //并能在多个线程间共享这些实例。DateTimeFormatter类还支持一个静态工厂方法，它可以按
        //照某个特定的模式创建格式器，代码清单如下。

//        按照某个模式创建DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date7 = LocalDate.of(2014, 3, 18);
        String formattedDate = date1.format(formatter);
        LocalDate date8 = LocalDate.parse(formattedDate, formatter);
//        这段代码中，LocalDate的formate方法使用指定的模式生成了一个代表该日期的字符串。
//        紧接着，静态的parse方法使用同样的格式器解析了刚才生成的字符串，并重建了该日期对象。
//        ofPattern方法也提供了一个重载的版本，使用它你可以创建某个Locale的格式器，代码清单
//        如下所示。
//        创建一个本地化的DateTimeFormatter
        DateTimeFormatter italianFormatter =
                DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
        LocalDate date9 = LocalDate.of(2014, 3, 18);
        String formattedDate1 = date9.format(italianFormatter); // 18. marzo 2014
        LocalDate date10 = LocalDate.parse(formattedDate1, italianFormatter);
//        最后，如果你还需要更加细粒度的控制，DateTimeFormatterBuilder类还提供了更复杂
//        的格式器，你可以选择恰当的方法，一步一步地构造自己的格式器。另外，它还提供了非常强大
//        的解析功能，比如区分大小写的解析、柔性解析（允许解析器使用启发式的机制去解析输入，不
//        精确地匹配指定的模式）、填充， 以及在格式器中指定可选节。比如， 你可以通过
//        DateTimeFormatterBuilder自己编程实现我们在代码清单12-11中使用的italianFormatter，
//        代码清单如下。
//        代码清单12-12 构造一个DateTimeFormatter
        DateTimeFormatter italianFormatter1 = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.ITALIAN);

        LocalDate localDate11 = LocalDate.now();
        System.out.println(localDate11.format(italianFormatter1));
//        目前为止，你已经学习了如何创建、操纵、格式化以及解析时间点和时间段，但是你还不了
//        解如何处理日期和时间之间的微妙关系。比如，你可能需要处理不同的时区，或者由于不同的历
//        法系统带来的差异。接下来的一节，我们会探究如何使用新的日期和时间API解决这些问题。

        // 4、--------处理不同的时区和历法

//        之前你看到的日期和时间的种类都不包含时区信息。时区的处理是新版日期和时间API新增
//        加的重要功能，使用新版日期和时间API时区的处理被极大地简化了。新的java.time.ZoneId
//        类是老版java.util.TimeZone的替代品。它的设计目标就是要让你无需为时区处理的复杂和
//        繁琐而操心，比如处理日光时（Daylight Saving Time，DST）这种问题。跟其他日期和时间类一
//        样，ZoneId类也是无法修改的。
//        时区是按照一定的规则将区域划分成的标准时间相同的区间。在ZoneRules这个类中包含了
//        40个这样的实例。你可以简单地通过调用ZoneId的getRules()得到指定时区的规则。每个特定
//        的ZoneId对象都由一个地区ID标识，比如：
        ZoneId romeZone = ZoneId.of("Europe/Rome");
//        地区ID都为“{区域}/{城市}”的格式，这些地区集合的设定都由英特网编号分配机构（IANA）
//        的时区数据库提供。你可以通过Java 8的新方法toZoneId将一个老的时区对象转换为ZoneId：
//        ZoneId zoneId = TimeZone.getDefault().toZoneId();
//        一旦得到一个ZoneId对象，你就可以将它与LocalDate、LocalDateTime或者是Instant
//        对象整合起来，构造为一个ZonedDateTime实例，它代表了相对于指定时区的时间点，代码清
//        单如下所示。
//        为时间点添加时区信息
        LocalDate date12 = LocalDate.of(2014, Month.MARCH, 18);
        ZonedDateTime zdt1 = date12.atStartOfDay(romeZone);
        LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
        ZonedDateTime zdt2 = dateTime.atZone(romeZone);
        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(romeZone);
// 图12-1对ZonedDateTime的组成部分进行了说明，相信能够帮助你理解LocaleDate、
// LocalTime、LocalDateTime以及ZoneId之间的差异。
// 2025-04-27T21:31:55.941 + 08:00[china/chongqing]
// LocalDate|LocalTime|ZoneId
// LocalDateTime
//         ZonedDateTime

//        通过ZoneId，你还可以将LocalDateTime转换为Instant：
//        LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
//        Instant instantFromDateTime = dateTime.toInstant(romeZone);
//        你也可以通过反向的方式得到LocalDateTime对象：
//        Instant instant = Instant.now();
//        LocalDateTime timeFromInstant = LocalDateTime.ofInstant(instant, romeZone);

//        利用和UTC/格林尼治时间的固定偏差计算时区
//        另一种比较通用的表达时区的方式是利用当前时区和UTC/格林尼治的固定偏差。比如，基
//        于这个理论，你可以说“纽约落后于伦敦5小时”。这种情况下，你可以使用ZoneOffset类，它
//        是ZoneId的一个子类，表示的是当前时间和伦敦格林尼治子午线时间的差异：
//        ZoneOffset newYorkOffset = ZoneOffset.of("-05:00");
//“05:00”的偏差实际上对应的是美国东部标准时间。注意，使用这种方式定义的ZoneOffset
//        并未考虑任何日光时的影响，所以在大多数情况下，不推荐使用。由于ZoneOffset也是ZoneId，
//        所以你可以像代码清单12-13那样使用它。你甚至还可以创建这样的OffsetDateTime，它使用
//        ISO-8601的历法系统，以相对于UTC/格林尼治时间的偏差方式表示日期时间。
//        LocalDateTime dateTime = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45);
//        OffsetDateTime dateTimeInNewYork = OffsetDateTime.of(date, newYorkOffset);
//        新版的日期和时间API还提供了另一个高级特性，即对非ISO历法系统（non-ISO calendaring）
//        的支持。
    }
}
