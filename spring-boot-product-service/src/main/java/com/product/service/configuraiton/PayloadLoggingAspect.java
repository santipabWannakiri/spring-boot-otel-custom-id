package com.product.service.configuraiton;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class PayloadLoggingAspect {
    @Around("execution(* com.product.service.serviceImpl.*.*(..))")
    public Object payloadLogMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        Object[] request = joinPoint.getArgs();
        log.info(mapper.writeValueAsString(request));

        Object response = joinPoint.proceed();
        if (response instanceof Optional<?>) {
            Optional<?> optionalResponse = (Optional<?>) response;
            if (optionalResponse.isPresent()) {
                // Response is present, handle it
                Object actualResponse = optionalResponse.get();
                log.info(mapper.writeValueAsString(actualResponse));
            } else {
                // Response is empty (Optional is empty)
                log.info("{}.{} : Response is empty");
            }
        } else if (response instanceof List<?>) {
            // Response is a List, handle it
            List<?> listResponse = (List<?>) response;
            log.info(mapper.writeValueAsString(listResponse));
        } else {
            // Handle other types if needed
            log.info(mapper.writeValueAsString(response));
        }
        return response;
    }
}
