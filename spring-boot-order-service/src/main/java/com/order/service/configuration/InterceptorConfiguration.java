package com.order.service.configuration;

import com.order.service.constants.Constants;
import com.order.service.controller.CartController;
import com.order.service.serviceImpl.CartServiceImpl;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Configuration
public class InterceptorConfiguration implements HandlerInterceptor {
    private final OpenTelemetry openTelemetry;
    private final Tracer tracer;

    @Autowired
    public InterceptorConfiguration(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
        this.tracer = openTelemetry.getTracer(CartController.class.getName(), "0.1.0");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Span parentSpan = tracer.spanBuilder("interceptor").setSpanKind(SpanKind.SERVER).startSpan();
        try (Scope scope = parentSpan.makeCurrent()) {
            SpanContext spanContext = parentSpan.getSpanContext();
            String traceId = spanContext != null ? spanContext.getTraceId() : "Unable get traceId";
            System.out.println("Trace ID " + traceId);
            request.setAttribute(Constants.X_TRACE_ID, traceId);
            request.setAttribute(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
            return true;
        } finally {
            parentSpan.end();
//            CartServiceImpl.setSpan(parentSpan);
        }
    }
}
