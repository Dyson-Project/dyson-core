package org.dyson.core.aop.impl;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
public class MeasureTimeAdvice {

    @Around("@annotation(org.dyson.core.aop.MeasureTime)")
    public Object measureTime(ProceedingJoinPoint point) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object object = point.proceed();
        stopWatch.stop();
        log.info("{}.{}() in {} ms", point.getSignature().getDeclaringType().getName(), point.getSignature().getName(), stopWatch.getTotalTimeMillis());
        return object;
    }
}