package com.order.service.configuration;

import com.order.service.constants.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;

@Aspect
@Component
@Slf4j
public class ECSLoggingAspect {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ThreadLocal<Long> eventStartThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> traceIdThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> requestIdThreadLocal = new ThreadLocal<>();

    @Before("execution(* com.order.service.serviceImpl.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            traceIdThreadLocal.set((String) request.getAttribute(Constants.X_TRACE_ID));
            requestIdThreadLocal.set((String) request.getAttribute(Constants.X_REQUEST_ID));
        }
        eventStartThreadLocal.set(startTime);
        initializeCommonMDCFields("CARTS-SERVICE", "https://example.com/carts", "response", "CARTS");
        log.info("Request log to ECS");
    }

    @AfterReturning(pointcut = "execution(* com.order.service.serviceImpl.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        initializeCommonMDCFields("CARTS-SERVICE", "https://example.com/carts", "response", "CARTS");
        MDC.put("event.end", dateFormat.format(System.currentTimeMillis()));
        log.info("Response log to ECS");
        MDC.clear();
        traceIdThreadLocal.remove();
        requestIdThreadLocal.remove();
        eventStartThreadLocal.remove();
    }

    @AfterThrowing(pointcut = "execution(* com.order.service.serviceImpl.*.*(..))", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        initializeCommonMDCFields("CARTS-SERVICE", "https://example.com/carts", "response", "CARTS");
        MDC.put("event.end", dateFormat.format(System.currentTimeMillis()));
        log.error("Exception in {}: {}", joinPoint.getSignature().toShortString(), ex.getMessage(), ex);
        MDC.clear();
        traceIdThreadLocal.remove();
        requestIdThreadLocal.remove();
        eventStartThreadLocal.remove();
    }

    private void initializeCommonMDCFields(String action, String url, String type, String owner) {
        MDC.put("event.action", action);
        MDC.put("event.url", url);
        MDC.put("trace.id", traceIdThreadLocal.get());
        MDC.put("http.request.id", requestIdThreadLocal.get());
        MDC.put("custom.event.type", type);
        MDC.put("custom.log.owner", owner);
        MDC.put("event.start", dateFormat.format(eventStartThreadLocal.get()));
    }

}


