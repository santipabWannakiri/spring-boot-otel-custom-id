package com.product.service.configuraiton;

import com.product.service.constants.Constants;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Configuration
public class InterceptorConfiguration implements HandlerInterceptor {

    private final OpenTelemetry openTelemetry;

    private final Tracer tracer;

    private final TextMapSetter<HttpHeaders> setter;

    private final TextMapGetter<HttpServletRequest> getter;

    @Autowired
    public InterceptorConfiguration(OpenTelemetry openTelemetry, TextMapSetter<HttpHeaders> setter, TextMapGetter<HttpServletRequest> getter) {
        this.openTelemetry = openTelemetry;
        this.tracer = openTelemetry.getTracer(InterceptorConfiguration.class.getName(), "0.1.0");
        this.setter = setter;
        this.getter = getter;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Context extractedContext = openTelemetry.getPropagators().getTextMapPropagator().extract(Context.current(), request, getter);
        try (Scope contextScope = extractedContext.makeCurrent()) {
            Span requestSpan = tracer.spanBuilder("product-interceptor").setSpanKind(SpanKind.SERVER).startSpan();
            try (Scope scope = requestSpan.makeCurrent()) {
                SpanContext spanContext = requestSpan.getSpanContext();
                String traceId = spanContext != null ? spanContext.getTraceId() : "Unable get traceId";
                System.out.println("Product - Interceptor Trace ID : " + traceId);
                request.setAttribute(Constants.X_TRACE_ID, traceId);
                request.setAttribute(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
                request.setAttribute(Constants.X_OTEL_CONTEXT, Context.current());
                return true;
            } finally {
                requestSpan.end();
            }
        }

    }


}