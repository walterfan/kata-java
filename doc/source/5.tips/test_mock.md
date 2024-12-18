# Test Mock
## 模拟对象
一般都叫 Mock 或 Stub, 两者差不多, 都是模拟被测组件对外依赖的模拟, 存根 stub 就在那里, 不需要检查它和被测组件的交互, Mock 则可以用来检查于被测对象的交互

## Mock
Mock 是测试驱动开发必备之利器, 只要有状态, 有依赖, 做单元测试就不能没有 Mock
在 API 或 集成测试的时候, 如果依赖于第三方的 API, 也时常使用 mock server 或 mock proxy

### Mock 的原则
Mockito 是广泛使用的 Java Mock library, 它的 wiki 上有篇文章 - [如何写出好的测试代码](
https://github.com/mockito/mockito/wiki/How-to-write-good-tests), 其中提出了几条使用 mock 的原则:

* 不要 mock 非你所有的类型
* 不要 mock 值对象
* 不要 mock 所有的东西

后两点很好理解, 第一点有些语焉不详, 什么叫非你所有的类型, 我的理解就是如果一个类型不是你与第三方约定的接口, 它属于别人定义的, 你只是拿过来使用, 那么你最好不要去mock 它, 你可以写一个中间层或适配器, 然后mock 这个中间层和适配器, 原因是第三方可以随时更改它的定义和行为, 你把它mock掉了, 你也就不会发现由于别人更改了定义或行为导致的异常. 而你自己写的中间层由你掌控，不必有此担心。

与第三方或其它服务集成测试属于Consumer Test 消费者测试和End to End 端到端的测试的范围

### Mock 使用步骤

* 1.模拟外部依赖并将mock插入到测试代码中
* 2.定义指定的行为及响应
* 3. 执行测试下的代码
* 4.验证代码是否正确执行

验证什么呢, 除了你的程序的应用逻辑, 还有对于所mock的对象的交互验证
* 对于mock 的对象的调用次数验证
* 对于mock 的对象的调用参数验证
* 对于mock 的对象所给出的不同输出结果的反应

### Mock 的问题

mock的时候最烦人的是两个问题

### 1.无法mock
就我熟悉的, 也是应用最广的两门语言 C++ 和 Java 来看

gmock 和 mockito 在大多数情况下都够用了，一般情况下不需要也不应该 mock  私有方法，静态方法和全局方法，当然如果你的代码可测试性及依赖反转做得得没那么好, 实在绕不过去，也有权变之法, C++可以直接改掉其在内存中的函数地址, Java 可以利用反射或修改字节码来搞定.

### 2. 需要mock的太多了

>举例来说, 我曾经做过一个网络电话控制系统, 它会对呼入呼出的电话会做一些语音交互应答(IVR), 并控制后续的电话会议流程, 系统比较复杂, 单元测试也很难做, 因为它用的是自己定义的一门领域特定语言 - Call Control XML, 并由自己的 Call Flow 引擎进行解析执行, 端到端的测试由于环境及配置的复杂性做起来很麻烦, 我的一位同事提出把系统的网络消息发送接收模块 mock 掉, 也就是把对外交互的消息全部 mock 起来,  但是mock的消息数量巨大, 工作量惊人.

我也写了一个类似于 hub 的类, 所有消息会回调到一个 MessageReceiver, MessageReceiver 会直接调用注册上来的各个 MessageHandler, 每个 Handler 只关注自己关心的消息, 具体来说, 每个 Handler 都可以设置一个正则表达式, 当消息头或消息体匹配这个正则表达式, 则由这个 Handler 来处理回应事先 mock好的消息, 回应你自己指定的消息, 从而把这个系统对外的依赖全部 mock 掉, 并测试了所有的交互


## mock 的粒度

根据你测试的对象大小，粒度自然有区别，根据测试三角形，小而美，越大越麻烦, 从小到大可以分为如下三个粒度

##1. mock一个函数

与这个函数的交互全部mock 掉

##2. mock整个类或接口

与这个类或接口的交互全部mock 掉，接口也可指某个API 

##3. mock 整个系统

与系统外部的交互全部mock 掉

总之，模拟外部依赖要区分内外的边界，找到合适的切入点

## Mock 类库和工具

仅就我所熟悉的 Java 和 C++ 举例如下, python, ruby, JavaScript 之类的脚本语言就更简单了 

### Mockito for Java
http://site.mockito.org/

### Powermock for Java
https://github.com/powermock/powermock 它通过自定义类加载器和修改字节码来mock  static methods, constructors, final classes and methods, private methods, removal of static initializers 等等

### GoogleMock for C++
https://github.com/google/googletest/tree/master/googlemock

###  Mock Server
MockServer 用来 mock 整个web service
https://github.com/jamesdbloom/mockserver

### wiremock
WireMock 和上面的 mock server差不多, 是一个 HTTP-based APIs的模拟器.

http://wiremock.org/ 

## 典型示例
接下来, 让我们写几个例子来说明 mock 和相关类库的用法...

### Mock 依赖的类和方法
基本步骤:

1) mock 设置模拟行为
2) call 调用被测试代码
3) verify 检验期望行为

这里以 [Guava Loading Cache](https://www.jianshu.com/p/2d3d30015915) 类为例, 测试它的基本行为是否符合预期

```
package com.github.walterfan.hellotest;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.extern.slf4j.Slf4j;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by yafan on 23/1/2018.
 */
@Slf4j
public class LoadingCacheTest {
    private LoadingCache<String,  String> internalCache;

    @Mock
    private CacheLoader<String, String> cacheLoader;

    @Mock
    private RemovalListener<String, String> cacheListener;

    @Captor
    private ArgumentCaptor<RemovalNotification<String, String>> argumentCaptor;

    private Answer<String> loaderAnswer;

    private AtomicInteger loadCounter = new AtomicInteger(0);

    @BeforeMethod
    public void setup() {

        MockitoAnnotations.initMocks(this);

        this.internalCache = CacheBuilder.newBuilder()
                .maximumSize(3)
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .removalListener(this.cacheListener)
                .build(this.cacheLoader);

        this.loaderAnswer = new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                String key = invocationOnMock.getArgumentAt(0, String.class);
                switch(loadCounter.getAndIncrement()) {
                    case 0:
                        return "alice";
                    case 1:
                        return "bob";
                    case 2:
                        return "carl";
                    default:
                        return "unknown";
                }
            }
        };
    }

    @Test
    public void cacheTest() throws Exception {
        //Mock the return value of loader
        //Mockito.when(cacheLoader.load(Mockito.anyString())).thenReturn("alice");
        Mockito.when(cacheLoader.load(Mockito.anyString())).thenAnswer(loaderAnswer);

        assertTrue("alice".equals(internalCache.get("name")));

        //sleep for 2 seconds
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        assertTrue("bob".equals(internalCache.get("name")));

        verify(cacheLoader, times(2)).load("name");
        verify(cacheListener).onRemoval(argumentCaptor.capture());

        assertEquals(argumentCaptor.getValue().getKey(), "name");
        assertEquals(argumentCaptor.getValue().getValue(), "alice");
        assertEquals(argumentCaptor.getValue().getCause(), RemovalCause.EXPIRED);
    }
}


```

### Mock 静态方法
这里使用 Powermock 和 testng , 如果有 junit 的话, 用法稍有不同
testng 需要从 PowerMockTestCase 继承
junit4 需要加上一个注解 @RunWith(PowerMockRunner.class) 


* 静态类和方法

```
package com.github.walterfan.hellotest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class FileUtils {

    public static final FileFilter javaFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {

            if(file.isDirectory()) {
                return true;
            }
            if(file.getName().endsWith(".java")) {
                return true;
            }

            return false;
        }
    };

    public static List<String> listFiles(File folder, FileFilter filter) {
        List<String> files = new ArrayList<>();
        listDir(new File("."), files, filter);
        return files;
    }

    public static void listDir(File folder, List<String> fileNames, FileFilter filter) {
        File[] files = folder.listFiles(filter);
        for (File file: files) {
            if(file.isFile()) {
                fileNames.add(file.getName());
            } else if (file.isDirectory()) {
                listDir(file, fileNames, filter);
            }
        }
    }
}

```

* 测试类
```
package com.github.walterfan.hellotest;



import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.IObjectFactory;
import org.testng.annotations.Test;


import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.testng.Assert.assertEquals;

//@RunWith(PowerMockRunner.class) -- for junit4
@PrepareForTest(FileUtils.class)
public class FileUtilsTest extends PowerMockTestCase {

    public  int howManyFiles(String path, FileFilter filter) {
        System.out.println("-----------");
        List<String> files = FileUtils.listFiles(new File(path), filter);
        files.forEach(System.out::println);
        return files.size();
    }


    @Test
    public void testHowManyFiles() {

        List<String> fileNames = Arrays.asList("a.java", "b.java", "c.java");
        PowerMockito.mockStatic(FileUtils.class);
        PowerMockito.when(FileUtils.listFiles(Mockito.any(), Mockito.any())).thenReturn(fileNames);

        int count = howManyFiles(".", FileUtils.javaFileFilter);
        assertEquals(count, 3);
    }
}

```

在 pom.xml 中加上

```

<dependency>
			<groupId>org.powermock</groupId>
			<artifactId>powermock-core</artifactId>
			<version>1.7.1</version>
			<scope>test</scope>
            <scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.powermock</groupId>
			<artifactId>powermock-api-mockito</artifactId>
			<version>1.7.1</version>
            <scope>test</scope>
		</dependency>

        <dependency>
            <groupId>org.powermock</groupId>
            <artifactId>powermock-module-junit4</artifactId>
            <version>1.7.1</version>
            <scope>test</scope>
        </dependency>

		<dependency>
			<groupId>org.powermock</groupId>
			<artifactId>powermock-module-testng</artifactId>
			<version>1.7.1</version>
			<scope>test</scope>
		</dependency>

```

###  Mock 第三方服务
假设我们在服务启动时需要调用第三方的服务来获取访问口令

GET $third_service_url/oauth2/api/v1/access_token?client_id=$clientId&client_secret=$clientPass

返回值是 json :

{ "token": "$token"}

我们在本地做测试时并没有部署这个第三方服务, 我们可以用如下方法 mock 掉整个第三方服务的所有 API 调用, 例子代码如下, 这里用到了以上所说的 http://www.mock-server.com 库

![](http://upload-images.jianshu.io/upload_images/1598924-8b39d652b617b7c9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


```
package com.github.walterfan.hellotest;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpHeaders;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockserver.model.HttpResponse.response;
import static org.testng.Assert.assertTrue;

@Slf4j
public class MockServerTest {

    public static final String ACCESS_TOKEN_URL = "/oauth2/api/v1/access_token";

    public static final String ACCESS_TOKEN_RESP = "{ \"token\": \"abcd1234\"}";

    private int listenPort;

    private OkHttpClient httpClient;
    //mock server
    private ClientAndServer mocker;

    
    public MockServerTest() {
        listenPort = 10086;
        httpClient = new OkHttpClient();
    }

    //启动 mock server
    @BeforeSuite
    public void startup() {
        mocker = ClientAndServer.startClientAndServer(listenPort);
    }

    //关闭 mock server
    @AfterSuite
    public void shutdown() {
        mocker.stop(true);
    }

    @Test
    public void testCheckHealth() throws IOException {

        HttpRequest mockReq = new HttpRequest().withMethod("GET").withPath(ACCESS_TOKEN_URL);
        HttpResponse mockResp = new HttpResponse().withStatusCode(200).withBody(ACCESS_TOKEN_RESP).withHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
       //mock API 的返回
       mocker.when(mockReq, Times.exactly(1))
              .respond(mockResp);

        String theUrl = String.format("http://localhost:%d%s?%s" , listenPort, ACCESS_TOKEN_URL, "client_id=test&client_secret=pass");
        Request request = new Request.Builder()
                .url(theUrl)
                .build();

        Response response = httpClient.newCall(request).execute();
        assertTrue(response.isSuccessful());


        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            log.info(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
        //mock server 返回了之前设定的结果
        String strResult = response.body().string();
        log.info(" strResult: {}", strResult);
        assertEquals(strResult, ACCESS_TOKEN_RESP);
        //验证 mock 的交互
        mocker.verify(mockReq);
    }




}

```
输出如下

```
22:09:05.000 [main] DEBUG org.mockserver.client.netty.NettyHttpClient - Sending to: localhost/127.0.0.1:10086 request: {
  "method" : "PUT",
  "path" : "/expectation",
  "headers" : {
    "host" : [ "localhost:10086" ]
  },
  "body" : {
    "type" : "STRING",
    "string" : "{\n  \"httpRequest\" : {\n    \"method\" : \"GET\",\n    \"path\" : \"/oauth2/api/v1/access_token\"\n  },\n  \"httpResponse\" : {\n    \"statusCode\" : 200,\n    \"headers\" : {\n      \"Content-Type\" : [ \"application/json;charset=UTF-8\" ]\n    },\n    \"body\" : \"{ \\\"token\\\": \\\"abcd1234\\\"}\"\n  },\n  \"times\" : {\n    \"remainingTimes\" : 1,\n    \"unlimited\" : false\n  },\n  \"timeToLive\" : {\n    \"unlimited\" : true\n  }\n}",
    "contentType" : "text/plain; charset=utf-8"
  }
}
22:09:05.059 [nioEventLoopGroup-4-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.bytebuf.checkAccessible: true
22:09:05.060 [nioEventLoopGroup-4-1] DEBUG io.netty.util.ResourceLeakDetectorFactory - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@6fc3e619
22:09:05.106 [nioEventLoopGroup-4-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxCapacityPerThread: 32768
22:09:05.106 [nioEventLoopGroup-4-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxSharedCapacityFactor: 2
22:09:05.106 [nioEventLoopGroup-4-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.linkCapacity: 16
22:09:05.106 [nioEventLoopGroup-4-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.ratio: 8
22:09:06.003 [nioEventLoopGroup-3-1] INFO org.mockserver.mock.HttpStateHandler - creating expectation:

	{
	  "httpRequest" : {
	    "method" : "GET",
	    "path" : "/oauth2/api/v1/access_token"
	  },
	  "times" : {
	    "remainingTimes" : 1,
	    "unlimited" : false
	  },
	  "timeToLive" : {
	    "unlimited" : true
	  },
	  "httpResponse" : {
	    "statusCode" : 200,
	    "headers" : {
	      "Content-Type" : [ "application/json;charset=UTF-8" ]
	    },
	    "body" : "{ \"token\": \"abcd1234\"}"
	  }
	}

22:09:06.035 [nioEventLoopGroup-4-1] DEBUG io.netty.buffer.PoolThreadCache - Freed 3 thread-local buffer(s) from thread: nioEventLoopGroup-4-1
22:09:06.106 [nioEventLoopGroup-3-2] INFO org.mockserver.mock.HttpStateHandler - request:

	{
	  "method" : "GET",
	  "path" : "/oauth2/api/v1/access_token",
	  "queryStringParameters" : {
	    "client_secret" : [ "pass" ],
	    "client_id" : [ "test" ]
	  },
	  "headers" : {
	    "content-length" : [ "0" ],
	    "Connection" : [ "Keep-Alive" ],
	    "User-Agent" : [ "okhttp/3.8.0" ],
	    "Host" : [ "localhost:10086" ],
	    "Accept-Encoding" : [ "gzip" ]
	  },
	  "keepAlive" : true,
	  "secure" : false
	}

 matched expectation:

	{
	  "method" : "GET",
	  "path" : "/oauth2/api/v1/access_token"
	}

22:09:06.114 [nioEventLoopGroup-3-2] INFO org.mockserver.mock.HttpStateHandler - returning response:

	{
	  "statusCode" : 200,
	  "headers" : {
	    "Content-Type" : [ "application/json;charset=UTF-8" ],
	    "connection" : [ "keep-alive" ]
	  },
	  "body" : "{ \"token\": \"abcd1234\"}"
	}

 for request:

	{
	  "method" : "GET",
	  "path" : "/oauth2/api/v1/access_token",
	  "queryStringParameters" : {
	    "client_secret" : [ "pass" ],
	    "client_id" : [ "test" ]
	  },
	  "headers" : {
	    "content-length" : [ "0" ],
	    "Connection" : [ "Keep-Alive" ],
	    "User-Agent" : [ "okhttp/3.8.0" ],
	    "Host" : [ "localhost:10086" ],
	    "Accept-Encoding" : [ "gzip" ]
	  },
	  "keepAlive" : true,
	  "secure" : false
	}

 for response action:

	{
	  "statusCode" : 200,
	  "headers" : {
	    "Content-Type" : [ "application/json;charset=UTF-8" ]
	  },
	  "body" : "{ \"token\": \"abcd1234\"}"
	}

22:09:06.122 [main] INFO com.github.walterfan.hellotest.MockServerTest - Content-Type: application/json;charset=UTF-8
22:09:06.123 [main] INFO com.github.walterfan.hellotest.MockServerTest - connection: keep-alive
22:09:06.123 [main] INFO com.github.walterfan.hellotest.MockServerTest - content-length: 22
22:09:06.124 [main] INFO com.github.walterfan.hellotest.MockServerTest -  strResult: { "token": "abcd1234"}

```

pom.xml 如下

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.github.walterfan</groupId>
	<artifactId>hellotest</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>hellotest</name>
	<description>Demo project for Mock Test</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.5.8.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Dalston.SR4</spring-cloud.version>
		<okhttp.version>3.8.0</okhttp.version>
		<mock-server-version>5.3.0</mock-server-version>
		<maven-shade-plugin-version>2.1</maven-shade-plugin-version>
		<metrics.version>3.1.5</metrics.version>
	</properties>

	<dependencies>

			<dependency>
				<groupId>io.dropwizard.metrics</groupId>
				<artifactId>metrics-core</artifactId>
				<version>${metrics.version}</version>
			</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-contract-stub-runner</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-contract-verifier</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-contract-wiremock</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>com.fasterxml.jackson.datatype</groupId>
			<artifactId>jackson-datatype-jsr310</artifactId>
			<version>2.4.0</version>
		</dependency>
		<dependency>
			<groupId>com.jayway.jsonpath</groupId>
			<artifactId>json-path</artifactId>
			<version>2.4.0</version>
		</dependency>
		<dependency>
			<groupId>org.testng</groupId>
			<artifactId>testng</artifactId>
			<version>6.11</version>
		</dependency>

		<dependency>
			<groupId>org.mock-server</groupId>
			<artifactId>mockserver-netty</artifactId>
			<version>${mock-server-version}</version>
		</dependency>

		<dependency>
			<groupId>com.squareup.okhttp3</groupId>
			<artifactId>okhttp</artifactId>
			<version>${okhttp.version}</version>
		</dependency>

		<dependency>
			<groupId>com.github.tomakehurst</groupId>
			<artifactId>wiremock</artifactId>
			<version>2.12.0</version>
		</dependency>

	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>


</project>

``` 

## 参考资料
* https://github.com/google/googletest/blob/master/googlemock/docs/ForDummies.md
* https://github.com/google/googletest/blob/master/googlemock/docs/CheatSheet.md
* https://github.com/google/googletest/blob/master/googlemock/docs/CookBook.md
* [Mockito 官方文档 ](http://static.javadoc.io/org.mockito/mockito-core/2.2.15/org/mockito/Mockito.html)
* [Mockito Refcard](https://dzone.com/refcardz/mockito)
* [如何写出好测试](https://github.com/mockito/mockito/wiki/How-to-write-good-tests)