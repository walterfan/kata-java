# Java 测试实战

## 测试用例的组织
首先要胸有成竹, 哪些测试应该做, 在[微服务实战测试之理论篇] 中已提过一些指导原则, 我们要根据这些原则, 结合要测试的特性, 把所有有可能出错的地方覆盖到, 防患于未然.

借用 Cucumber 中的定义的 Gherkin 语法, 一个特性 Feature 有若干个场景 Scenario 
而每个场景都必须有独立的意义, 并且不依赖任何其他场景而独立运行.

以表格的形式组织测试用例是比较常见的做法

| 特性Feature | 场景Scenario |  给定Given | 事件 When | 结果 Then |
| -- | -- |  -- | -- | -- |
| 作为系统用户, 我想在任务即将截止设置三次提醒来通知我, 从而我能即时采取措施而不至于超时 |  今天周一, 我要在周日前交稿  |  截止日期前一天要有邮件提醒  | 周六到了 | 收到提醒邮件 |

也可以用 wiki 或其他文件格式来存储用例, 推荐用格式化, 易解析的格式化文本文件, 比如 json.

结构层次为 1) Test Suite -- 2) Test Case -- 3) Test Steps(Given, When, Then)

例如:

```
{
  "testsuites": [
    {
      "name": "login_module_test",
      "testcases": [
        {
          "name": "login_by_phone_step1",
          "feature": "login",
          "scenario": "login by mobile phone",
          "given": "input mobile phone number",
          "when": "submit",
          "then": "send a sms for authenticate code"
        },
        {
          "name": "login_by_phone_step2",
          "feature": "login",
          "scenario": "login by mobile phone",
          "given": "input mobile phone number and authenticate code",
          "when": "submit",
          "then": "redirect the user's home paeg"
        },
        {
          "name": "login_by_error_password",
          "feature": "login",
          "scenario": "login by username and password",
          "given": "input username, password, and captcha",
          "when": "submit",
          "then": "dispatch login failure message: you inputed improper username or password"
        }
      ]
    }
  ]
}
```

也可以自己写一个注解来自己生成测试用例, 我们在文末给出一个例子


## 单元测试
在微服务实战测试之理论篇中我们提到测试的分类和测试金字塔, 单元测试是基石, 做好单元是首要的测试工作, 以我熟悉的几种语言为例

测试的写法就四步 SEVT (TVes 许多电视倒过来)
1) 准备 setup
2) 执行 exercise
3) 验证 verify
4) 清理 teardown

简单测试可以忽略1) 和 4) 步

### Java 单元测试

哪些库我们可以用呢

如果你使用 spring-boot-starter-test ‘Starter’ (test scope),  你会发现它所提供的下列库:

* [JUnit](http://junit.org/) — The de-facto standard for unit testing Java applications.
* [Spring Test](http://docs.spring.io/spring/docs/5.0.0.RC3/spring-framework-reference/testing.html#integration-testing) & Spring Boot Test — Utilities and integration test support for Spring Boot applications.
* [AssertJ](http://joel-costigliola.github.io/assertj/) — A fluent assertion library.
* [Hamcrest](http://hamcrest.org/JavaHamcrest/) — A library of matcher objects (also known as constraints or predicates).
* [Mockito](http://mockito.org/) — A Java mocking framework.
* [JSONassert](https://github.com/skyscreamer/JSONassert) — An assertion library for JSON.
* [JsonPath](https://github.com/jayway/JsonPath) — XPath for JSON.



单元测试框架的鼻祖是 junit, 为什么不用 junit 呢? TestNG 有什么独到之处可以后来居上呢? 原因就在于 testng 更为强大的功能, 如 @Test 注解, 可以指定 testcase 的依赖关系, 调用次数, 调用顺序, 超时时间, 并发线程数以及期望的异常, 考虑得非常周到.

当然, 这只是个人喜好, Junit 新版本也多了很多改进.

举个例子, Fibonacci 数列大家很熟悉, 用 Java8 的 stream, lambda 的新写法比老的写法酷很多, 代码行数少了许多.

老写法
```
public List<Integer> fibonacci1(int size) {
        List<Integer> list = new ArrayList<>(size);
        int n0 = 1, n1 = 1, n2;
        list.add(n0);
        list.add(n1);

        for(int i=0;i < size - 2; i++) {
            n2 = n1 + n0;
            n0 = n1;
            n1 = n2;
            list.add(n2);
        }
        return list;
    }
```
新写法
```
public List<Integer> fibonacci2(int size) {
        return Stream.iterate(new int[]{1, 1}, x -> new int[]{x[1], x[0] + x[1]})
                .limit(size).map(x -> x[0])
                .collect(Collectors.toList());
    }

```
然而性能如何呢? 写个单元测试吧

```
package com.github.walterfan.example.java8;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by walter on 24/03/2017.
 * @see http://testng.org/doc/documentation-main.html
 */
public class LambdaPerfTest {
    private static final Logger logger = LoggerFactory.getLogger(LambdaPerfTest.class);

    private Map<Integer, Long> oldFibonacciResults;
    private Map<Integer, Long> newFibonacciResults;


    @BeforeClass
    public void init() {
        oldFibonacciResults = new ConcurrentSkipListMap<>();
        newFibonacciResults = new ConcurrentSkipListMap<>();
    }

    @AfterClass
    public void summarize() {
        int rounds = oldFibonacciResults.size();

        System.out.println("--- old vs. new ---");
        oldFibonacciResults.forEach((key, value) -> {
            System.out.println(key + ": " + value + " vs. " + newFibonacciResults.get(key));
           //TODO: add assert for performance compare
        });

    }

    public List<Integer> fibonacci1(int size) {
        List<Integer> list = new ArrayList<>(size);
        int n0 = 1, n1 = 1, n2;
        list.add(n0);
        list.add(n1);

        for(int i=0;i < size - 2; i++) {
            n2 = n1 + n0;
            n0 = n1;
            n1 = n2;
            list.add(n2);
        }
        return list;
    }

    public List<Integer> fibonacci2(int size) {
        return Stream.iterate(new int[]{1, 1}, x -> new int[]{x[1], x[0] + x[1]})
                .limit(size).map(x -> x[0])
                .collect(Collectors.toList());
    }

    @DataProvider
    public Object[][] getFibonacciSize() {
        return new Object[][]{
                {10},
                {50},
                {100},
                {1000},
                {10000}
        };
    }

    @Test(dataProvider = "getFibonacciSize", description = "old fibonacci", timeOut = 1000)
    public void testOldFibonacci(int size) {
        long duration = testFibonacci("testFibonacci1", size, x->fibonacci1(x));
        oldFibonacciResults.put(size, duration);
    }

    @Test(dataProvider = "getFibonacciSize", description = "lambda fibonacci", timeOut = 1000)
    public void testNewFibonacci(int size) {
        long duration = testFibonacci("testFibonacci2", size, x->fibonacci2(x));
        newFibonacciResults.put(size, duration);
    }

    public long testFibonacci(String name, int size, Function<Integer, List<Integer> > func) {

        Stopwatch stopwatch = Stopwatch.createStarted();
        List<Integer> list = func.apply(size);
        stopwatch.stop();
        long duration = stopwatch.elapsed(TimeUnit.MICROSECONDS);

        list.stream().forEach(x -> System.out.print(x +", "));
        System.out.println(String.format("\n--> %s (%d): %d\n" , name, size, duration));
        return duration;
    }

}
```

做了5组数列长度从10到10000 的测试, 输出结果如下

```
--- old vs. new ---
10: 34 vs. 28965
50: 9 vs. 154
100: 13 vs. 669
1000: 112 vs. 2600
10000: 1019 vs. 13548
```

不测不知道, 一测吓一跳, 新的写法看起来不错, 但是性能完败, 关键在于多做了两次转换(map , collect), 这里的测试代码用到了 @BeforeClass, @AfterClass, @Test, @DataProvider, TestNG 还有一些不错的功能, 比如 @threadPoolSize, @expectedExceptions, 详情参见 http://testng.org/doc/documentation-main.html

不知道你发现没有, 这里有个大问题, 这段测试代码缺少 Assert, 多数情况下对于功能测试必需要有 assert , 这些 assert 就是检查点, 没有检查点的测试起不到真正的作用. 你不可能去看每个测试的输出, 当然这里说的是单元测试,而对于性能测试, 一般要出一个性能测试的报告, Assert 检查点也不是必需的

所以我们应该这样写, 尽量多地加断言, 例如我们对 google 的 libphonenumber 作一个简单的测试

```
package com.github.walterfan.devaid.util;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@Slf4j
public class PhoneNumberUtilTest {

    @Test
    public void testIsNumberNsnMatch() {
        String phoneNumberOne = "+86055112345678";
        String phoneNumberTwo = "86055112345678";
        PhoneNumberUtil.MatchType matchType = PhoneNumberUtil.getInstance().isNumberMatch(phoneNumberOne, phoneNumberTwo);
        log.info("matchType is {}", matchType);
        assertFalse(matchType == PhoneNumberUtil.MatchType.NO_MATCH);
        assertFalse(matchType == PhoneNumberUtil.MatchType.NOT_A_NUMBER);
        assertEquals(matchType , PhoneNumberUtil.MatchType.NSN_MATCH);



    }


    @Test
    public void testIsNumberShortMatch() {
        String phoneNumberOne = "+86055112345678";
        String phoneNumberTwo = "086(0551)1234-5678";
        PhoneNumberUtil.MatchType matchType = PhoneNumberUtil.getInstance().isNumberMatch(phoneNumberOne, phoneNumberTwo);
        assertFalse(matchType == PhoneNumberUtil.MatchType.NO_MATCH);
        assertFalse(matchType == PhoneNumberUtil.MatchType.NOT_A_NUMBER);
        assertEquals(matchType , PhoneNumberUtil.MatchType.SHORT_NSN_MATCH);
    }

    @Test
    public void testGetCountryCode() {

        String strPhoneNumber = "+86-0551-12345678";
        try {
            Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(strPhoneNumber, "US");
            log.info("phoneNumber.getCountryCode() is {}", phoneNumber.getCountryCode());
            assertTrue(phoneNumber.getCountryCode() == 86);
        } catch (NumberParseException e) {
            fail(e.getMessage());
        }


    }
}

```

### 数据驱动测试

* 举例如下, 被测试类为 HttpUtil

```
public class HttpUtil
{
    public static boolean hasFieldValue(String httpHeader, String fieldKey, String fieldVal) {
		if(null == httpHeader || null == fieldKey || null == fieldVal) {
			return false;
		}

		String[] toggles = httpHeader.split(";");
		for(String toggle: toggles) {
			String[] toggleKeyVal = toggle.split("=");
			if(toggleKeyVal.length > 1) {
				String key = StringUtils.trim(toggleKeyVal[0]);
				String val = StringUtils.trim(toggleKeyVal[1]);

				if(fieldKey.equals(key) && fieldVal.equalsIgnoreCase(val))  {
					return true;
				}
			}
		}
		return false;
	}

}
```

* 我们会用多个不同的 HTTP 头域来测试这个待测方法是否可正确地把相应头域的值判断出来, 用到的测试数据不必手工构造, 可以放在一个在 Object[][]为返回结果的方法中返回, 这些数据会逐个喂给测试方法, 决窍在于这个注解: ``` @Test(dataProvider= "makeHttpHeadFields") ```

所以我们的一个测试方法最终 会生成 8 个测试用例

具体代码如下

```
public class HttpUtilTest {

    @DataProvider
    public Object[][] makeHttpHeadFields() {

        return new Object[][] {
                { "acl_enabled= true", true },
                { "acl_enabled=true; auth_type=oauth", true },
                { "acl_enabled =TRue; auth_type=basic", true  },
                { "acl_enabled = false; auth_type=basic", false  },
                { " acl_enabled = ; auth_type=basic", false  },
                { "auth_type=basic", false  },
                { "", false  }

        };


    }

    @Test(dataProvider= "makeHttpHeadFields")
    public void testHasFieldValue(String toggleHeader, boolean ret) {
        Assert.assertEquals(HttpUtil.hasFieldValue(toggleHeader, "acl_enabled", "true") ,ret);

    }
}
```

运行结果如下

![Test Results](http://upload-images.jianshu.io/upload_images/1598924-9b8f17873d0d4f46.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

对于单元测试的测试用例组织主要是要逻辑分支覆盖, 符合 [微服务实战测试之理论篇](https://www.jianshu.com/p/d84dcb2887f1) 中所提到的三大原则
* FIRST 原则
* Right-BICEP 
* CORRECT 检查原则

还有很多线程测试, 性能测试, 压力测试, 异常测试, API 测试, 以及消费者契约测试, 
这些测试我们后面慢慢道来, Mock 和 API 测试可参见 [微服务实战之Mock](https://www.jianshu.com/p/0f17bd2410e4)

下面我们就之前提到的测试用例的组织编写一个 TestCase 注解和它的注解处理器, 可在很方便地生成测试用例文档

## 编写注解来自动生成测试用例


```
package com.github.walterfan.hello.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface TestCase {
    String value();
    String feature() default "";
    String scenario() default "";
    String given() default "";
    String when() default "";
    String then() default "";
    //String[] checkpoints();
}


```

在编译阶段处理注解并生成测试用例文档

```
package com.github.walterfan.hello.annotation;


import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.github.walterfan.hello.annotation.TestCase")
@AutoService(Processor.class)
public class TestCaseProcessor extends AbstractProcessor {
    public final static String TABLE_TITLE1 = "| ## | feature | case | scenario | given | when | then |\n";
    public final static String TABLE_TITLE2 = "|---|---|---|---|---|---|---|\n";
    public final static String TABLE_ROW = "| %d | %s | %s | %s | %s | %s | %s |\n";

    private File testcaseFile = new File("./TestCases.md");
    private StringBuilder testcaseBuilder = new StringBuilder();
    private AtomicInteger testCaseNum = new AtomicInteger(0);

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        testcaseBuilder.append("## Testcases");
        testcaseBuilder.append("\n");
        testcaseBuilder.append(TABLE_TITLE1);
        testcaseBuilder.append(TABLE_TITLE2);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(testcaseFile))) {
            bw.write(testcaseBuilder.toString());
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        StringBuilder sb = new StringBuilder();
        for (TypeElement annotation : annotations) {

            for (Element element : roundEnvironment.getElementsAnnotatedWith(annotation)) {

                TestCase testCase = element.getAnnotation(TestCase.class);

                if (testCase != null) {
                    String line = String.format(TABLE_ROW, testCaseNum.incrementAndGet(), testCase.feature(), testCase.value(), testCase.scenario(), testCase.given(), testCase.when(), testCase.then());
                    sb.append(line);

                }
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(testcaseFile, true))) {
            bw.write(sb.toString());
            System.out.println("testcases:\n" + sb.toString());
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

```

假设我们有一个简单的类 User

```
package com.github.walterfan.hello.annotation;


import lombok.Data;

import java.util.Calendar;
import java.util.Date;


@Data
public class User {
    private String name;
    private String email;
    private Date birthDay;


    public int age() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDay);

        return Math.abs(now.get(Calendar.YEAR) - birth.get(Calendar.YEAR));
    }

}

```

我们写一个测试类

```
package com.github.walterfan.hello.annotation;

import com.github.walterfan.hello.annotation.User;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import static org.testng.Assert.assertEquals;


public class UserTest {

    @Test
    @TestCase(value = "testAge", feature = "UserManage", scenario = "CreateUser" ,given = "setBirthday", when="retrieveAge", then = "Age is current time minus birthday")
    public void testAge() throws ParseException {
        User user = new User();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date birthDay = formatter.parse("1980/02/10");
        user.setBirthDay(birthDay);

        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birthDay);

        int diffYear = Calendar.getInstance().get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
        System.out.println("diffYear: "+ diffYear);
        assertEquals(user.age(), diffYear);
    }

    @Test
    @TestCase(value = "testName", feature = "UserManage", scenario = "UpdateUser" ,given = "setName", when="retrieveName", then = "name is same")
    public void testName() throws ParseException {
        String name = "Walter";
        User user = new User();
        user.setName(name);
        user.getName().equals(name);
    }
}

```

编译这个类会自动生成一个 TestCase.md, 内容如下

```
 Testcases
| ## | feature | case | scenario | given | when | then |
|---|---|---|---|---|---|---|
| 1 | UserManage | testAge | CreateUser | setBirthday | retrieveAge | Age is current time minus birthday |
| 2 | UserManage | testName | UpdateUser | setName | retrieveName | name is same |
```
也就是

| ## | feature | case | scenario | given | when | then |
|---|---|---|---|---|---|---|
| 1 | UserManage | testAge | CreateUser | setBirthday | retrieveAge | Age is current time minus birthday |
| 2 | UserManage | testName | UpdateUser | setName | retrieveName | name is same |

完整代码可参见 https://github.com/walterfan/helloworld/tree/master/helloannotation

## 参考资料
* [TestNG 官方文档](http://testng.org/doc/documentation-main.html)

