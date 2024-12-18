package com.fanyamin.kata.demo;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.*;
import static java.util.stream.LongStream.*;
import java.io.*;
import java.util.logging.Logger;
import org.apache.commons.lang3.time.StopWatch;

public class ParallelPrime {
    private static final Logger logger = Logger.getLogger(ParallelPrime.class.getName());

    static final int COUNT = 100_000;

    public static boolean isPrime(long n) {
        return rangeClosed(2, (long)Math.sqrt(n)).noneMatch(i -> n % i == 0);
    }

    public static void main(String[] args) throws IOException {
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> primes = iterate(2, i -> i+1)
                .parallel()
                .filter(ParallelPrime::isPrime)
                .limit(COUNT)
                .mapToObj(Long::toString)
                .toList();
        watch.stop();
        logger.info("Time Elapsed(ms): " + watch.getTime(TimeUnit.MILLISECONDS));
        Files.write(Paths.get("primes.txt"), primes, StandardOpenOption.CREATE);

    }
}
