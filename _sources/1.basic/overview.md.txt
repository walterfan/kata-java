```{contents} Table of Contents
:depth: 3
```
# Java 语言概述


## Java 8到Java 21之间的变化


### 语言特性

* Lambda表达式：Java 8引入，可简洁地表示可传递给方法或存储在变量中的代码块。如Runnable接口的实现，Java 8前需匿名内部类，之后可写成() -> System.out.println("Hello, Lambda!");。

* 方法引用：Java 8引入，与Lambda表达式相关，可直接引用已有方法。如System.out::println等同于x -> System.out.println(x)。

* 接口默认方法：Java 8开始，接口可含默认方法实现。如

```java
interface MyInterface {
    default void myMethod()
    { System.out.println("Default method implementation"); }
}
```

* 局部变量类型推断：

  Java 10开始，用var关键字让编译器推断局部变量类型。如 `var num = 10;`，编译器会推断num为int型。

* 记录类型：

  Java 14引入，用于创建不可变数据类，自动生成构造函数、访问器等。
  如 `record Point(int x, int y) {}`，可直接用new Point(1, 2)创建实例。

* 模式匹配：Java 16开始增强，

  如 `if (obj instanceof String s) { System.out.println(s.length()); }`，
  可直接使用类型匹配后的变量。

集合框架改进

* Stream API：Java 8引入，可对集合进行函数式操作。如

```java
  List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
  numbers.stream().map(n -> n * 2).forEach(System.out::println);
```
  将列表元素加倍输出。

* Optional类：Java 8引入，用于表示可能为空的值。如

```java
  Optional<String> optionalValue = Optional.ofNullable(getValue());
  if (optionalValue.isPresent()) {
    System.out.println(optionalValue.get());
  }
```

* 集合工厂方法：Java 9开始，可更方便地创建不可变集合。
  如 `List.of(1, 2, 3)` 创建不可变列表，`Set.of(1, 2, 3)`创建不可变集合。

### 并发增强

* CompletableFuture：Java 8引入，用于异步编程和处理异步任务的结果。
如 `CompletableFuture.supplyAsync(() -> calculateValue()).thenAccept(result -> System.out.println(result));`，异步计算值并在完成时打印。

* 虚拟线程：Java 21引入，以更轻量级方式实现高并发。
  如`Thread.startVirtualThread(() -> { System.out.println("Virtual thread is running"); });`

### 其他变化

* HTTP客户端API：Java 11开始，提供了新的HTTP客户端，用于发送HTTP请求和处理响应。如
```
  HttpClient client = HttpClient.newHttpClient();
  HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://www.example.com")).build();
  client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(System.out::println);。
```

* 文本块：Java 15引入，用于更方便地处理多行文本。如
```java
  String text = """ This is a text block.
  It can span multiple lines. """;
```

## Java 7 新特性

JSR303 Java Validation中也有不少输入验证的注解 , Hibernate Validator 是一个典型实现

### Switch 支持基于字串和枚举值判断
```
switch(user.getGender())
{
case  "male": 
    //...
    break;
case "female"
    //...
    break;
default
    //...
}
```
### 支持数字之间用下划线分隔 
int key = 123_456_789

### 支持二进制常数
int value = 0b1001;//9

### catch 语句可以捕捉多个异常
如果是同一类型的异常, 子类要放在前面
```
try {
} catch(NumberFormatException | RuntimeException e)
}
```

### try-with-resources
资源句柄泄漏是Java程序中常犯的错误, 在C++中的最常用的做法是构造函数是打开资源句柄, 析构函数里关闭资源句柄
所以Java的新做法是把resource 放在try 语句的参数中, try 语句结束时会自动释放资源

```
static String readFirstLineFromFileWithFinallyBlock(String path)
                                                     throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try {
        return br.readLine();
    } finally {
        if (br != null) br.close();
    }
}
```
to
```
static String readFirstLineFromFile(String path) throws IOException {
    try (BufferedReader br =
                   new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }
}
```

### 变长参数支持泛型

```
@SafeVarargs
public <T> void printArgs(T... args) {
    for(T value: args) 
    {
        System.out.println(value.toString());
    }

}
```

## Java8 新特性


### Lambda
按 JSR335 定义, Java8 增加了Lambda的支持, 即用表达式的方法来表示函数式接口

所谓函数式接口是指一个简单的接口只包含一个方法, 类似一个函数, 比如
```
interface Runnable {
    void run();
}
```
过去的写法
```
Runnable task = new Runnable() {
  public run() {
        System.out.println("I'm running");
  }
}
```
现在的写法
```
Runnable task = () ->  System.out.println("I'm running");
```

### Optional
这是一个容器对象, 用来避免频繁判断null以及NPE(Null Pointer Exception), 它可能包含或不包含非空值。 如果存在一个值，isPresent（）将返回true，get（）将返回值。

它还提供了依赖于包含值是否存在的附加方法，如orElse（）（如果值不存在则返回一个默认值）以及ifPresent（）（如果该值存在，则执行一个代码块）。

举例如下, 用Optional 方便了许多, 详见
```
        String x = "walter";
        if (x != null) {
            String t = x.trim();
            if (t.length() > 1) {
                System.out.println(t);
            }
        }

        Optional.ofNullable(x).
                map(String::trim).
                filter(t -> t.length() > 1).
                ifPresent(System.out::println);
```

### 函数接口Functional Interface
函数接口为lambda表达式和方法引用提供目标类型。 每个函数接口都有一个抽象方法，称为该函数接口的函数方法，lambda表达式的参数和返回类型被匹配或适配到该方法。 

其实任何具有单个抽象方法SAM(Single Abstract Method) 的接口都可称为 functional interface, 可以用注解 @FunctionalInterface  修饰, 当然这并不是必需的, 不过编译器可以凭此识别这个接口是否只包含 SAM

函数接口可以在多个上下文中提供目标类型，例如赋值上下文，方法调用或者类型转换上下文：

```
    // 赋值上下文
     Predicate<String> p = String::isEmpty;

     // 方法调用上下文
     stream.filter(e -> e.getSize() > 10)...

     // 类型转换上下文
     stream.map((ToIntFunction) e -> e.getSize())...
```

有几个基本的函数形式，包括Function（从T到R的一元函数），Consumer（从T到void的一元函数），Predicate（从T到布尔的一元函数）以及Supplier（从n到n的函数到R）。

常用的函数接口详见 https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html

### Stream

Stream 就是一个元素序列, 它支持顺序及并行的聚合操作, 例如计算一些红色widget的宽度之和, 无需使用循环, 分支和累加语句:

```
     int sum = widgets.stream()
                      .filter(w -> w.getColor() == RED)
                      .mapToInt(w -> w.getWeight())
                      .sum();
```

举个stream , lambda, 及 函数接口的例子

```

@Slf4j
public class StreamTest {

    @Data
    class User {
        final String firstName;
        final String lastName;
        final String email;

        public User(String firstNamename, String lastName, String email) {
            this.firstName = firstNamename;
            this.lastName = lastName;
            this.email = email;
        }



    }

    @Test
    public void testStream() {

        Function<User, String> fullNameFunction = u -> { String fullName = u.getFirstName() + " " + u.getLastName(); return fullName; };
        List<User> users = new ArrayList<>();
        users.add(new User("walter", "fan", "walter.fan@world.com"));
        users.add(new User("walt", "zhou", "walter.zhou@world.com"));
        List<String> names = users.stream().map(fullNameFunction).collect(Collectors.toList());
        log.info("--- name list ---");
        names.forEach(log::info);
        assertEquals("walter fan" , names.get(0));


        List<Integer> numbers = Arrays.asList(3, 2, 2, 3, 7, 3, 5);
        List<Integer> squaresList = numbers.stream().map( i -> i*i).distinct().collect(Collectors.toList());
        log.info("--- numbers ---");
        squaresList.forEach(x -> System.out.println(x));

    }
}

```

输出为

```
--- name list ---
walter fan
walt zhou
--- numbers ---
9
4
49
25
```

这里使用了注解 @Slf4j 来自动生成 log 成员 和 @Data 来自动生成 Getter, Setter, ToString
详见 https://projectlombok.org/features/all

pom.xml 中请加上
```
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>6.9.10</version>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.16.18</version>
        <scope>provided</scope>
    </dependency>
```



## Reference
* http://www.oracle.com/technetwork/java/javase/8-whats-new-2157071.html
* https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html
* https://www.techempower.com/blog/2013/03/26/everything-about-java-8/