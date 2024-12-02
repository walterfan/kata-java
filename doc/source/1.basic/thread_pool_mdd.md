# Thread Pool Optimization by MDD
## JDK 的线程池简介

多线程池是我们最常用的并行编程工具，多线程是性能优化在多核处理器时代是最常用的手段。而线程池是处理并发请求和任务的常用方法，使用线程池可以减少在创建和销毁线程上所花的时间以及系统资源的开销，解决系统资源利用不足的问题，创建一个线程池来并发的任务看起来非常简单，其实线程池的参数是很有讲究的。

以 Java 为例，一个标准的线程池创建方法如下：

```
/** Thread Pool Executor */
public ThreadPoolExecutor(
   int corePoolSize, //核心线程数
   int maxPoolSize, //最大线程数
   long keepAliveTime, //存活时间，超过corePoolSize的空闲线程在此时间之后会被回收
   TimeUnit unit, //存活时间单位
   BlockingQueue<Runnable> workQueue//阻塞的任务队列
   RejectedExecutionHandler handler //当队列已满，线程数已达maxPoolSize时的策略
) {...}

```

### 线程池的参数包括：

- corePoolSize：核心线程数，即线程池初始化的时候就存在的线程数量。
- maximumPoolSize：最大线程数，即线程池能够容纳同时执行的最大线程数量。
- keepAliveTime：线程空闲时间，即当线程池中线程数量大于corePoolSize时，如果空闲时间达到keepAliveTime，线程池会关闭空闲的线程。
- unit：keepAliveTime的时间单位。
- workQueue：任务队列，即存放任务的队列。
- threadFactory：线程工厂，用于创建新的线程。
- handler：拒绝策略，即当任务队列满，且线程池线程数量达到maximumPoolSize时，如何处理新任务。
线程池的工作原理:

### 线程池的工作流程
- 线程池创建时，会根据参数初始化线程池，创建corePoolSize个线程，等待任务 incoming。
- 当有新的任务进来时，线程池会尝试将任务放入workQueue中由工作线程消费，如果workQueue没有满，则放入成功，线程池继续等待任务 incoming。
- 当workQueue满了，线程池会尝试创建新的线程，如果线程数量没有达到maximumPoolSize，则创建新的线程，否则，线程池会尝试将任务放入handler中，handler会根据拒绝策略决定如何处理。
- 当线程池中的线程空闲一段时间 (keepAliveTime) ，且线程数量大于corePoolSize，则线程池会尝试关闭空闲的线程，直到线程数量等于corePoolSize。

虽然JDK 提供了一些默认实现，比如:

* static ExecutorService newCachedThreadPool()
* static ExecutorService	newFixedThreadPool(int nThreads)
* static ScheduledExecutorService	newScheduledThreadPool(int corePoolSize)

这些线程池并不能满足不了各种各样的业务场景，我们要为 ThreadPoolExecutor 设置更加合理的线程池参数来达到最优，以满足应用的性能需求。

## 1. 根据经验和通式公式按需求设置相对合理的参数
拿线程数来说， 我们需要考虑线程数设置多少才合适， 这个取决于诸多因素：

*  服务器的 CPU 资源。
*  取决任务的类型和其消耗的资源情况。

如果任务是读写数据库， 那么它取决于数据库连接池的连接数目， 以及数据库的负载和性能， 而如果任务是通过网络访问第三方服务，那么它取决于网络负载大小，以及第三方服务的负载和性能。
通常来说，CPU 密集型的任务占用CPU 时间较长，线程数可以设置的小一点， I/O密集型的任务占用CPU时间较短，线程数可以设的大一点。
我们的目的是充分利用给到我们的 CPU 资源，如果线程的任务有很多等待时间，比如等待磁盘和网络I/O，那么就把线程数设多一点，如果任务本身非常耗费CPU的计算资源，CPU 处理时间较长，那么就把线程数设得小一点。


根据以下公式

```
线程数 = CPU核数 * 希望的CPU使用率 * (1 + 等待时间/处理时间)
```

假设我们的服务器为4核CPU，我们要创建一个线程池来发送度量数据指标到远端的 Kafka 上，网络延迟约为50ms，数据解析编码压缩时间大约5ms，CPU占用率希望在10%之内。根据下面的计算结果，得出我们需要4.4, 约5个线程

```
4 * 0.1 * (1 + 50 / 5) = 4.4
```

于是， 我们设置参数如下：

| 参数 | 赋值 |  解释 |
|---|---|---|
|  int corePoolSize | 5| 核心线程数|
| int maxPoolSize| 10 |最大线程数|
| long keepAliveTime | 5000 |线程保活时间，超过核心线程数的空闲线程在此时间之后会被回收，这个值设长一点有利于避免频繁的创建和销毁线程|
|  TimeUnit unit |TimeUnit.MILLISECOND |保活时间的单位, 这里用毫秒|
| BlockingQueue<Runnable> workQueue | new LinkedBlockingQueue(500) | 暂存线程任务的阻塞队列，先入先出的场景就用LinkedBlockingQueue 好了|
| ThreadFactory threadfactory| new DefaultThreadFactory() | 线程创建工厂|
| RejectedExecutionHandler handler| new DefaultRejectedExecutionHandler() |当线程队列和线程数已满，或者线程池关闭，对新任务的拒绝服务策略，内置的有4种策略: <br>1) AbortPolicy, <br>2) CallerRunsPolicy, <br>3) DiscardPolicy, <br>4) DiscardOldestPolicy|

## 2. 根据度量指标进行调整

为了进行充分的度量，我们必需对线程池的各种指标进行记录和展示。
先来简单了解一些度量术语，详情参见[https://metrics.dropwizard.io/4.1.2/manual/core.html](https://metrics.dropwizard.io/4.1.2/manual/core.html)


### MetricRegistry

各种度量数据的容器，类似于 windows 的系统注册表，各项度量数据都可以在其中进行注册。


### 度量类型

* Gauge 计量器，它代表一个瞬时变化的值，比如连接数，线程数等

* Counter 计数器，它代表一个连续变化的值，比如线程队列长度，不会突变，但是会递增或递减

* Meter 测量仪, 它用来统计基于时间单位的处理速率，比如TPS(每秒事务数)， DAU(日均活跃用户）等

* Timer 计时器，它用来统计所花费时间的统计分布值，比如线程的忙闲程度，平均响应时间等

### 线程相关度量指标

1. 线程数： 最大，最小和实时的线程数
2. 线程队列长度： 最大长度限制和实时长度
3. 任务处理速率：任务提交与完成速度
4. 任务运行数量
5. 线程的忙闲比
6. 任务被拒绝的数量
7. 任务在队列中等待的时间：最大和实时的等待时间
8. 超过最大等待时间的任务数量

## 线程的度量与监控的方法

1. 创建线程池并注册各项度量指标

2. 运行线程池并收集度量指标

3. 观察度量指标并相应地调整参数

## 线程的度量与监控的实例

我们可以应用 dropwizard 的 metrics 库中的 [https://metrics.dropwizard.io/](https://metrics.dropwizard.io/) 类库 InstrumentedExecutorService 来帮助我们进行上述指标的统计，部分关键代码如下：

## InstrumentedExecutorService

```
public class InstrumentedExecutorService implements ExecutorService {
    private static final AtomicLong NAME_COUNTER = new AtomicLong();
    private final ExecutorService delegate;
    private final Meter submitted;
    private final Counter running;
    private final Meter completed;
    private final Timer idle;
    private final Timer duration;

    public InstrumentedExecutorService(ExecutorService delegate, MetricRegistry registry) {
        this(delegate, registry, "instrumented-delegate-" + NAME_COUNTER.incrementAndGet());
    }

    public InstrumentedExecutorService(ExecutorService delegate, MetricRegistry registry, String name) {
        this.delegate = delegate;
        this.submitted = registry.meter(MetricRegistry.name(name, new String[]{"submitted"}));
        this.running = registry.counter(MetricRegistry.name(name, new String[]{"running"}));
        this.completed = registry.meter(MetricRegistry.name(name, new String[]{"completed"}));
        this.idle = registry.timer(MetricRegistry.name(name, new String[]{"idle"}));
        this.duration = registry.timer(MetricRegistry.name(name, new String[]{"duration"}));
    }
    //...
    private class InstrumentedRunnable implements Runnable {
        private final Runnable task;
        private final Timer.Context idleContext;

        InstrumentedRunnable(Runnable task) {
            this.task = task;
            this.idleContext = idle.time();
        }

        @Override
        public void run() {
            idleContext.stop();
            running.inc();
            try (Timer.Context durationContext = duration.time()) {
                task.run();
            } finally {
                running.dec();
                completed.mark();
            }
        }
    }
}
```

它通过装饰器模式对原来的 Executor Service 进行包装，记录了 submited, running, completed, idle , duration 这些指标，我们可以另外再记录一些指标，部分代码如下：


## 1) 先定义一个线程池参数对象

```
package com.github.walterfan.helloconcurrency;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;


/**
 * @Author: Walter Fan
 **/
@Getter
@Setter
@Builder
public class ThreadPoolParam {
    private int minPoolSize;
    private int maxPoolSize;
    private Duration keepAliveTime;
    private int queueSize;
    private String threadPrefix;
    private boolean daemon;
    private MetricRegistry metricRegistry;

}

```
##  2) 再写一个创建线程池的工具类：
* ThreadPoolUtil.java
```

package com.github.walterfan.helloconcurrency;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.InstrumentedExecutorService;
import com.codahale.metrics.Meter;

import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.function.Supplier;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * @Author: Walter Fan
 **/
@Slf4j
public class ThreadPoolUtil {
    /*
    和系统内置的 ThreadPoolExecutor.CallerRunsPolicy 差不多，
    如果被拒绝，就用提交任务的线程来执行任务.
    */
    public static class DiscardAndLogPolicy implements RejectedExecutionHandler {
        final MetricRegistry metricRegistry;
        final Meter rejectedMeter;
        final Counter rejectedCounter;

        public DiscardAndLogPolicy(String threadPrefix, MetricRegistry metricRegistry) {
            this.metricRegistry = metricRegistry;
            this.rejectedMeter =  metricRegistry.meter(threadPrefix + ".rejected-meter");
            this.rejectedCounter = metricRegistry.counter(threadPrefix + ".rejected-counter");
        }


        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            rejectedMeter.mark();
            rejectedCounter.inc();
            if (!e.isShutdown()) {
                log.warn("reject task and run {} directly", r);
                r.run();

            }
        }
    }

    //创建线程执行器，注册了几个度量指标
    public static ThreadPoolExecutor createThreadExecutor(ThreadPoolParam threadPoolParam) {
        MetricRegistry metricRegistry = threadPoolParam.getMetricRegistry();

        metricRegistry.register(threadPoolParam.getThreadPrefix() + ".min", createIntGauge(() -> threadPoolParam.getMinPoolSize()));
        metricRegistry.register(threadPoolParam.getThreadPrefix() + ".max", createIntGauge(() -> threadPoolParam.getMaxPoolSize()));
        metricRegistry.register(threadPoolParam.getThreadPrefix() + ".queue_limitation", createIntGauge(() -> threadPoolParam.getQueueSize()));


        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadPoolParam.getMinPoolSize(),
                threadPoolParam.getMaxPoolSize(),
                threadPoolParam.getKeepAliveTime().toMillis(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(threadPoolParam.getQueueSize()),
                createThreadFactory(threadPoolParam),
                createRejectedExecutionHandler(threadPoolParam));

        metricRegistry.register(threadPoolParam.getThreadPrefix() + ".pool_size", createIntGauge(() -> executor.getPoolSize()));
        metricRegistry.register(threadPoolParam.getThreadPrefix() + ".queue_size", createIntGauge(() -> executor.getQueue().size()));
        return executor;
    }

    //创建线程执行服务，用 InstrumentedExecutorService 来包装和度量线程任务
    public static ExecutorService createExecutorService(ThreadPoolParam threadPoolParam) {
        ThreadPoolExecutor executor = createThreadExecutor(threadPoolParam);

        return new InstrumentedExecutorService(executor,
                threadPoolParam.getMetricRegistry(),
                threadPoolParam.getThreadPrefix());
    }

    private static Gauge<Integer> createIntGauge(Supplier<Integer> suppier) {
        return () -> suppier.get();
    }

    public static ThreadFactory createThreadFactory(ThreadPoolParam threadPoolParam) {
        return new ThreadFactoryBuilder()
                .setDaemon(threadPoolParam.isDaemon())
                .setNameFormat(threadPoolParam.getThreadPrefix() + "-%d")
                .build();
    }

    public static RejectedExecutionHandler createRejectedExecutionHandler(ThreadPoolParam threadPoolParam) {
        return new DiscardAndLogPolicy(threadPoolParam.getThreadPrefix(), threadPoolParam.getMetricRegistry());
    }
}

```

注意: 我们在这个线程池中埋设了12个度量指标，看你能不能在代码中找出来设置的地方 。

1. cards-thread-pool.completed	
1. cards-thread-pool.max
1. cards-thread-pool.queue_limitation
1. cards-thread-pool.rejected-meter
1. cards-thread-pool.duration	
1. cards-thread-pool.min		
1. cards-thread-pool.queue_size	
1. cards-thread-pool.running
1. cards-thread-pool.idle		
1. cards-thread-pool.pool_size	
1. cards-thread-pool.rejected-counter	
1. cards-thread-pool.submitted


##  3）用线程池执行多副扑克牌的排序任务

以我们最常用的打扑克牌为例，分别用冒泡排序，插入排序和 JDK 自带的 TimSort 来对若干副牌排序，总共创建20个任务，都放入线程池中执行，当我们采用不同的线程池参数时，效果大不相同。


### 3.1) 扑克牌对象类
* Poker.java

```
package com.github.walterfan.helloconcurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Walter Fan
 **/
public class Poker {
    public static class Card {
        enum Suite {
            Spades(4), Hearts(3), Clubs(2), Diamonds(1);
            int value;

            Suite(int value) {
                this.value = value;

            }

            private static Map<Integer, Suite> valueMap = new HashMap<>();

            static {
                for (Suite suite : Suite.values()) {
                    valueMap.put(suite.value, suite);
                }
            }

            public static Suite valueOf(int pageType) {
                return valueMap.get(pageType);
            }

        }
        Suite suite;
        //1~13
        int point;

        public Card(int suiteValue, int point) {
            this.suite = Suite.valueOf(suiteValue);
            this.point = point;
        }

        public String toString() {
            String strPoint = Integer.toString(point);
            if (point > 10) {
                switch (point) {
                    case 11:
                        strPoint = "J";
                        break;
                    case 12:
                        strPoint = "Q";
                        break;
                    case 13:
                        strPoint = "K";
                        break;

                }
            }

            return suite.name() + ":" + strPoint;
        }

        public int getScore() {
            return suite.value * 100 + point;
        }
    }




    public static List<Card> createCardList(int suiteCount) {
        List<Card> cards = new ArrayList<>(52);
        for(int i = 1; i < 5; i++) {
            for(int j = 1; j < 14 ;++j) {
                cards.add(new Card(i, j));
            }
        }

        List<Card> totalCards = new ArrayList<>(suiteCount );

        for(int j = 0; j < suiteCount; j++) {
            totalCards.addAll(new ArrayList<>(cards));
        }

        Collections.shuffle(totalCards);
        return totalCards;
    }

    public static class CardComparator implements Comparator<Card> {

        @Override
        public int compare(Card o1, Card o2) {
            return o1.getScore() - o2.getScore();
        }
    }

}


```

### 3.2) 排序任务类
任务很简单，就象我们平常打扑克那样，将几副牌排序，可用三种排序方法
 1）冒泡排序
 2）插入排序
 3）Tim 排序，JDK7 中用的一种结合了插入排序和归并排序的高效排序方法

* SortCardTask.java

```
package com.github.walterfan.helloconcurrency;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @Author: Walter Fan
 **/
@Slf4j
public class SortCardTask implements Callable<Long> {
    public enum SortMethod { BUBBLE_SORT, INSERT_SORT, TIM_SORT}
    private final List<Poker.Card> cards;
    private final SortMethod sortMethod;
    private final int taskNumber;

    private final AtomicInteger taskCounter;

    public SortCardTask(List<Poker.Card> cards, SortMethod method, int taskNumber, AtomicInteger taskCounter) {
        this.cards = cards;
        this.sortMethod = method;
        this.taskNumber = taskNumber;
        this.taskCounter = taskCounter;
    }

    @Override
    public Long call() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("* {} begin to sort {} cards by {}", this.taskNumber, cards.size(), sortMethod);
        switch(sortMethod) {
            case BUBBLE_SORT:
                bubbleSort(cards, new Poker.CardComparator());
                break;
            case INSERT_SORT:
                insertSort(cards, new Poker.CardComparator());
                break;
            case TIM_SORT:
                timSort(cards, new Poker.CardComparator());
                break;
        }

        stopwatch.stop();

        long millis = stopwatch.elapsed(MILLISECONDS);
        log.info("* {} end to sort {} cards sort by {} spend {} milliseconds - {}" , this.taskNumber, cards.size(), sortMethod, millis, stopwatch); // formatted string like "12.3 ms"
        taskCounter.incrementAndGet();
        return millis;
    }

    public static <T> void bubbleSort(List<T> aList, Comparator<T> comparator) {
        boolean sorted = false;
        int loopCount = aList.size() - 1;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < loopCount; i++) {
                if (comparator.compare(aList.get(i), aList.get(i + 1)) > 0) {
                    Collections.swap(aList, i, i + 1);
                    sorted = false;
                }
            }
        }
    }

    public static <T> void insertSort(List<T> aList, Comparator<T> comparator) {
        int size = aList.size();
        for (int i = 1; i < size; ++i) {
            T selected = aList.get(i);

            if (size < 10) {
                log.info("{} insert to {}", selected, aList.subList(0, i).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            }

            int j = i - 1;
            //find a position for insert currentElement in the left sorted collection
            while (j >= 0 && comparator.compare(selected, aList.get(j)) < 0) {
                //it does not overwrite existed element because the j+1=i that is currentElement at beginging
                aList.set(j + 1, aList.get(j));
                j--;
            }
            aList.set(j + 1, selected);

        }
    }

    public static <T> void timSort(List<T> aList, Comparator<T> comparator) {
        aList.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "SortCardTask{" +
                "taskNumber=" + taskNumber +
                ", sortMethod=" + sortMethod +
                '}';
    }
}

```

### 3.3) 线程池演示类
* ThreadPoolDemo.java
```
package com.github.walterfan.helloconcurrency;


import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.Slf4jReporter;

import com.codahale.metrics.MetricRegistry;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: Walter Fan
 **/
@Slf4j
public class ThreadPoolDemo {
    private static AtomicInteger finishCounter = new AtomicInteger(0);

    private AtomicInteger taskNumber = new AtomicInteger(0);

    private ExecutorService executorService;

    public ThreadPoolDemo(ThreadPoolParam threadPoolParam) {
        executorService = ThreadPoolUtil.createExecutorService(threadPoolParam);

    }

    public Callable<Long> createTask(int cardSuiteCount, SortCardTask.SortMethod method) {
        List<Poker.Card> cards = Poker.createCardList(cardSuiteCount);
        return new SortCardTask(cards, method, taskNumber.incrementAndGet(), finishCounter);

    }

    public List<Future<Long>> exeucteTasks(List<Callable<Long>> tasks, Duration waitTime)  {
        try {
            return this.executorService.invokeAll(tasks, waitTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("invokeAll interrupt", e);
            return Collections.emptyList();
        }
    }

    public void waitUntil(long ms) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(ms, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("--- start ---");
        MetricRegistry metricRegistry = new MetricRegistry();

        final CsvReporter csvReporter = CsvReporter.forRegistry(metricRegistry)
                .formatFor(Locale.US)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(new File("./"));
        csvReporter.start(100, TimeUnit.MILLISECONDS);

/*        final Slf4jReporter logReporter = Slf4jReporter.forRegistry(metricRegistry)
                .outputTo(log)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        logReporter.start(1, TimeUnit.MINUTES);*/

        ThreadPoolParam threadPoolParam = ThreadPoolParam.builder()
                .minPoolSize(1)
                .maxPoolSize(4)
                .daemon(true)
                .keepAliveTime(Duration.ofSeconds(1))
                .queueSize(5)
                .threadPrefix("cards-thread-pool")
                .metricRegistry(metricRegistry)
                .build();

        ThreadPoolDemo demo = new ThreadPoolDemo(threadPoolParam);
        List<Callable<Long>> tasks = new ArrayList<>();
        //30 tasks, 10, 40, 90 ... 1000 suite cards
        for(int i=1; i<=10; i++) {
            tasks.add(demo.createTask(i*i*10, SortCardTask.SortMethod.BUBBLE_SORT));
            tasks.add(demo.createTask(i*i*10, SortCardTask.SortMethod.INSERT_SORT));
            tasks.add(demo.createTask(i*i*10, SortCardTask.SortMethod.TIM_SORT));
        }



        List<Future<Long>> results = demo.exeucteTasks(tasks, Duration.ofMinutes(1));

        //logReporter.report();
        stopwatch.stop();
        log.info("--- end finish {}, spent {} ---", finishCounter.get(), stopwatch);
        results.stream().filter(x -> !x.isDone()).forEach(x -> log.info("{} is not done", x));


    }
}

```
上述代码让线程池执行了30个排序任务，最多排序1000副牌(52000张), 
10任务用冒泡排序，10个任务用插入排序，10个任务用  Tim 排序, 总共花了18秒多
执行结果如下:

```

17:09:47.341 [main] INFO com.github.walterfan.helloconcurrency.ThreadPoolDemo - --- start ---
17:09:47.497 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 1 begin to sort 520 cards (10 suite） by BUBBLE_SORT
17:09:47.496 [main] WARN com.github.walterfan.helloconcurrency.ThreadPoolUtil - reject task and run java.util.concurrent.FutureTask@61baa894 directly
17:09:47.497 [cards-thread-pool-1] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 7 begin to sort 4680 cards (90 suite） by BUBBLE_SORT
17:09:47.497 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 9 begin to sort 4680 cards (90 suite） by TIM_SORT
17:09:47.497 [cards-thread-pool-2] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 8 begin to sort 4680 cards (90 suite） by INSERT_SORT
17:09:47.498 [main] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 10 begin to sort 8320 cards (160 suite） by BUBBLE_SORT
17:09:47.515 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 9 end to sort 4680 cards (90 suite）sort by TIM_SORT spend 17 milliseconds - 17.70 ms
17:09:47.520 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 2 begin to sort 520 cards (10 suite） by INSERT_SORT
17:09:47.520 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 1 end to sort 520 cards (10 suite）sort by BUBBLE_SORT spend 22 milliseconds - 22.91 ms
17:09:47.521 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 3 begin to sort 520 cards (10 suite） by TIM_SORT
17:09:47.522 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 3 end to sort 520 cards (10 suite）sort by TIM_SORT spend 0 milliseconds - 528.3 μs
17:09:47.522 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 4 begin to sort 2080 cards (40 suite） by BUBBLE_SORT
17:09:47.531 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 2 end to sort 520 cards (10 suite）sort by INSERT_SORT spend 10 milliseconds - 10.60 ms
17:09:47.531 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 5 begin to sort 2080 cards (40 suite） by INSERT_SORT
17:09:47.546 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 5 end to sort 2080 cards (40 suite）sort by INSERT_SORT spend 14 milliseconds - 14.85 ms
17:09:47.547 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 6 begin to sort 2080 cards (40 suite） by TIM_SORT
17:09:47.548 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 6 end to sort 2080 cards (40 suite）sort by TIM_SORT spend 1 milliseconds - 1.666 ms
17:09:47.560 [cards-thread-pool-2] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 8 end to sort 4680 cards (90 suite）sort by INSERT_SORT spend 63 milliseconds - 63.22 ms
17:09:47.579 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 4 end to sort 2080 cards (40 suite）sort by BUBBLE_SORT spend 57 milliseconds - 57.37 ms
17:09:47.715 [cards-thread-pool-1] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 7 end to sort 4680 cards (90 suite）sort by BUBBLE_SORT spend 218 milliseconds - 218.0 ms
17:09:48.171 [main] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 10 end to sort 8320 cards (160 suite）sort by BUBBLE_SORT spend 672 milliseconds - 672.3 ms
17:09:48.171 [main] WARN com.github.walterfan.helloconcurrency.ThreadPoolUtil - reject task and run java.util.concurrent.FutureTask@b065c63 directly
17:09:48.171 [main] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 16 begin to sort 18720 cards (360 suite） by BUBBLE_SORT
17:09:48.171 [cards-thread-pool-2] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 12 begin to sort 8320 cards (160 suite） by TIM_SORT
17:09:48.172 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 13 begin to sort 13000 cards (250 suite） by BUBBLE_SORT
17:09:48.171 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 11 begin to sort 8320 cards (160 suite） by INSERT_SORT
17:09:48.173 [cards-thread-pool-1] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 14 begin to sort 13000 cards (250 suite） by INSERT_SORT
17:09:48.178 [cards-thread-pool-2] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 12 end to sort 8320 cards (160 suite）sort by TIM_SORT spend 6 milliseconds - 6.314 ms
17:09:48.178 [cards-thread-pool-2] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 15 begin to sort 13000 cards (250 suite） by TIM_SORT
17:09:48.187 [cards-thread-pool-2] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 15 end to sort 13000 cards (250 suite）sort by TIM_SORT spend 8 milliseconds - 8.673 ms
17:09:48.228 [cards-thread-pool-3] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 11 end to sort 8320 cards (160 suite）sort by INSERT_SORT spend 56 milliseconds - 56.62 ms
17:09:48.333 [cards-thread-pool-1] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 14 end to sort 13000 cards (250 suite）sort by INSERT_SORT spend 159 milliseconds - 159.2 ms
17:09:49.595 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 13 end to sort 13000 cards (250 suite）sort by BUBBLE_SORT spend 1423 milliseconds - 1.424 s
17:09:50.520 [main] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 16 end to sort 18720 cards (360 suite）sort by BUBBLE_SORT spend 2348 milliseconds - 2.348 s
17:09:50.520 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 17 begin to sort 18720 cards (360 suite） by INSERT_SORT
17:09:50.520 [cards-thread-pool-4] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 22 begin to sort 33280 cards (640 suite） by BUBBLE_SORT
17:09:50.521 [main] WARN com.github.walterfan.helloconcurrency.ThreadPoolUtil - reject task and run java.util.concurrent.FutureTask@449b2d27 directly
17:09:50.521 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 24 begin to sort 33280 cards (640 suite） by TIM_SORT
17:09:50.521 [main] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 26 begin to sort 42120 cards (810 suite） by INSERT_SORT
17:09:50.521 [cards-thread-pool-6] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 25 begin to sort 42120 cards (810 suite） by BUBBLE_SORT
17:09:50.537 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 24 end to sort 33280 cards (640 suite）sort by TIM_SORT spend 16 milliseconds - 16.03 ms
17:09:50.537 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 18 begin to sort 18720 cards (360 suite） by TIM_SORT
17:09:50.545 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 18 end to sort 18720 cards (360 suite）sort by TIM_SORT spend 7 milliseconds - 7.866 ms
17:09:50.545 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 19 begin to sort 25480 cards (490 suite） by BUBBLE_SORT
17:09:50.772 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 17 end to sort 18720 cards (360 suite）sort by INSERT_SORT spend 251 milliseconds - 251.9 ms
17:09:50.772 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 20 begin to sort 25480 cards (490 suite） by INSERT_SORT
17:09:51.614 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 20 end to sort 25480 cards (490 suite）sort by INSERT_SORT spend 841 milliseconds - 841.2 ms
17:09:51.614 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 21 begin to sort 25480 cards (490 suite） by TIM_SORT
17:09:51.615 [main] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 26 end to sort 42120 cards (810 suite）sort by INSERT_SORT spend 1093 milliseconds - 1.094 s
17:09:51.642 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 21 end to sort 25480 cards (490 suite）sort by TIM_SORT spend 28 milliseconds - 28.11 ms
17:09:51.643 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 23 begin to sort 33280 cards (640 suite） by INSERT_SORT
17:09:52.308 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 23 end to sort 33280 cards (640 suite）sort by INSERT_SORT spend 664 milliseconds - 664.9 ms
17:09:52.308 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 27 begin to sort 42120 cards (810 suite） by TIM_SORT
17:09:52.318 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 27 end to sort 42120 cards (810 suite）sort by TIM_SORT spend 9 milliseconds - 9.679 ms
17:09:52.318 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 28 begin to sort 52000 cards (1000 suite） by BUBBLE_SORT
17:09:55.392 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 19 end to sort 25480 cards (490 suite）sort by BUBBLE_SORT spend 4846 milliseconds - 4.847 s
17:09:55.392 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 29 begin to sort 52000 cards (1000 suite） by INSERT_SORT
17:09:56.511 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 29 end to sort 52000 cards (1000 suite）sort by INSERT_SORT spend 1119 milliseconds - 1.119 s
17:09:56.512 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 30 begin to sort 52000 cards (1000 suite） by TIM_SORT
17:09:56.523 [cards-thread-pool-5] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 30 end to sort 52000 cards (1000 suite）sort by TIM_SORT spend 11 milliseconds - 11.68 ms
17:09:58.528 [cards-thread-pool-4] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 22 end to sort 33280 cards (640 suite）sort by BUBBLE_SORT spend 8007 milliseconds - 8.008 s
17:10:02.026 [cards-thread-pool-6] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 25 end to sort 42120 cards (810 suite）sort by BUBBLE_SORT spend 11504 milliseconds - 11.50 s
17:10:07.127 [cards-thread-pool-0] INFO com.github.walterfan.helloconcurrency.SortCardTask - * 28 end to sort 52000 cards (1000 suite）sort by BUBBLE_SORT spend 14808 milliseconds - 14.81 s
17:10:07.128 [main] INFO com.github.walterfan.helloconcurrency.ThreadPoolDemo - --- end finish 30, spent 19.79 s ---

```

我用 CsvReporter 把若干度量指标打印到Csv文件中，共有如下12个 CSV 文件

1. cards-thread-pool.completed.csv		
1. cards-thread-pool.max.csv		
1. cards-thread-pool.queue_limitation.csv	
1. cards-thread-pool.rejected-meter.csv
1. cards-thread-pool.duration.csv		
1. cards-thread-pool.min.csv		
1. cards-thread-pool.queue_size.csv	
1. cards-thread-pool.running.csv
1. cards-thread-pool.idle.csv		
1. cards-thread-pool.pool_size.csv		
1. cards-thread-pool.rejected-counter.csv	
1. cards-thread-pool.submitted.csv

基于这些度量指标，我们可以看到任务特点和线程池的参数是否合理

### 1) 线程任务执行时间
看结果三种排序方法的效率差别很大，只排两副牌时，三种方法差不太多，而排序1000副牌（52000张）时， TimSort 花了大约11 毫秒， InsertSort 花了大约 1 秒 ，而 BubbleSort 花了14 秒多。

对于任务执行时间，我们可以通过记录的度量指标文件来作一个分析，简单画一个线形图，

* csv 文件内容 cards-thread-pool.duration.csv
```
t,count,max,mean,min,stddev,p50,p75,p95,p98,p99,p999,mean_rate,m1_rate,m5_rate,m15_rate,rate_unit,duration_unit
1585473845,0,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,calls/second,milliseconds
1585473846,0,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.000000,calls/second,milliseconds
1585473846,8,63.069663,37.658003,2.579674,25.915099,39.007040,61.809917,63.069663,63.069663,63.069663,63.069663,33.614807,0.000000,0.000000,0.000000,calls/second,milliseconds
1585473846,8,63.069663,37.658003,2.579674,25.915099,39.007040,61.809917,63.069663,63.069663,63.069663,63.069663,23.777415,0.000000,0.000000,0.000000,calls/second,milliseconds
1585473846,8,63.069663,37.658003,2.579674,25.915099,39.007040,61.809917,63.069663,63.069663,63.069663,63.069663,18.448678,0.000000,0.000000,0.000000,calls/second,milliseconds
1585473846,9,384.357751,76.180197,2.579674,111.663094,58.345330,62.339820,384.357751,384.357751,384.357751,384.357751,16.884697,0.000000,0.000000,0.000000,calls/second,milliseconds
# 省略余下内容
```

* 分析任务执行时间的 Python 脚本
```
import matplotlib.pyplot as plt
import pandas as pd

durations = pd.read_csv('cards-thread-pool.duration.csv')
print(durations.head(1))

plt.plot(durations['t'], durations['max'], label = 'max')
plt.plot(durations['t'], durations['mean'], label = 'mean')
plt.plot(durations['t'], durations['min'], label = 'min')



plt.ylabel("milliSeconds")
plt.xlabel("timestamp")
plt.legend(prop = {'size': 10}) 

plt.show()
```
![排序任务时间](https://upload-images.jianshu.io/upload_images/1598924-7e8f37dde11c437f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 2) 线程池中的线程数变化

```
import matplotlib.pyplot as plt
import pandas as pd

durations = pd.read_csv('cards-thread-pool.pool_size.csv')
print(durations.head(1))

plt.plot(durations['t'], durations['value'], label = 'pool size')


plt.ylabel("thread count")
plt.xlabel("timestamp")
plt.legend(prop = {'size': 10}) 

plt.show()
```

![线程池中的线程数](https://upload-images.jianshu.io/upload_images/1598924-6b995dfb61b13dd9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

线程池的线程数应该比较平稳，避免频繁的创建和销毁线程，这张图揭示如果系统资源足够的话，corePoolSize, maxPoolSize 和  keepAliveTime 时间可以适当调大。

### 线程池队列长度

```
import matplotlib.pyplot as plt
import pandas as pd

durations = pd.read_csv('cards-thread-pool.queue_size.csv')
print(durations.head(1))

plt.plot(durations['t'], durations['value'], label = 'queue size')


plt.ylabel("queue size")
plt.xlabel("timestamp")
plt.legend(prop = {'size': 10}) 

plt.show()
```

![线程池队列长度](https://upload-images.jianshu.io/upload_images/1598924-24d786a6efa89dc3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 线程池拒绝的任务数
```
import matplotlib.pyplot as plt
import pandas as pd

durations = pd.read_csv('cards-thread-pool.rejected-counter.csv')
print(durations.head(1))

plt.plot(durations['t'], durations['count'], label = 'rejected count')


plt.ylabel("rejected count")
plt.xlabel("timestamp")
plt.legend(prop = {'size': 10}) 

plt.show()
```
![被拒绝的任务数](https://upload-images.jianshu.io/upload_images/1598924-065d3359c0e4308e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

基于被拒绝的任务数来看，显然核心线程数和队列长度应该增大。


在实际工作中， ConsoleReporter, Slf4jReporter，还是CsvReporter 这些  metrics-core 自带的报告器都是定时采样并打印度量指标，分析查询很不方便。

![](https://upload-images.jianshu.io/upload_images/1598924-3a90380bdb2d94b2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

它们都是对线程池的度量指标定时采样记录，我们可以利用一些时间序列数据库（例如 InfluxDB，Promethues 等）将这些指标保存起来，再利用报表分析工具（Grafana, Graphite等）对它们进行分析。

完整源代码参见 [https://github.com/walterfan/helloworld/tree/master/helloconcurrency/src/main/java/com/github/walterfan/helloconcurrency](https://github.com/walterfan/helloworld/tree/master/helloconcurrency/src/main/java/com/github/walterfan/helloconcurrency)

