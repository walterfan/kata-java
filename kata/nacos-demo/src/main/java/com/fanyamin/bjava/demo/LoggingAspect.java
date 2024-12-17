package com.fanyamin.bjava.demo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.fanyamin.bjava.demo.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        System.out.println("AOP Around: " + joinPoint.getSignature() + " executed in " + executionTime + "ms");
        return proceed;
    }

    @Before("execution(* com.fanyamin.bjava.demo.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("AOP Before: " + joinPoint.getSignature().getName());
    }
}